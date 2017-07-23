package io.mycat.jredis.memory;

import io.mycat.jredis.datastruct.BaseStruct;
import io.mycat.jredis.util.UnsafeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Desc:
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class MemoryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryManager.class);

    private static long baseAddress;//记录分配的内存首地址
    private static long totalSize;//分配内存的总大小

    private static long offset;//记录当前分配的地址偏移量
    private static TreeMap<Long, LinkedBlockingDeque<Long>> freeMemory = new TreeMap();//记录释放的资源

    /**
     * 分配指定大小的堆外内存
     *
     * @param size
     */
    public static void allocateMemory(long size) {
        baseAddress = UnsafeUtil.getUnsafe().allocateMemory(size);
        if (baseAddress == 0) {
            throw new OutOfMemoryError();
        }
        MemoryManager.totalSize = size;
        LOGGER.debug("memory base address: {}", baseAddress);
    }

    /**
     * 从创建的堆外内存中分配指定大小的地址空间
     *
     * @param length
     * @return
     */
    public static long alloc(long length) {
        if (baseAddress == 0)
            throw new RuntimeException("地址空间未申请");

        long newOffset = offset + length;
        //顺序分配的空间足够
        if (newOffset < totalSize) {
            long result = baseAddress + offset;
            offset = newOffset;
            return result;
        }

        //todo
        Long ceilKey = freeMemory.ceilingKey(length);
        if (ceilKey == null) {
            //碎片空间不够分配
            return 0;
        }

        return freeMemory.get(ceilKey).pollFirst();
    }

    /**
     * 归还分配的内存
     *
     * @param struct
     */
    public static void free(BaseStruct struct) {
        LinkedBlockingDeque<Long> list = freeMemory.get(struct.sizeOf());
        if (list == null)
            list = new LinkedBlockingDeque();

        list.offerLast(struct.getAddress());
    }

    public static int sizeOf(BaseStruct struct) {
        if (struct == null)
            return 0;

        return struct.sizeOf();
    }
}
