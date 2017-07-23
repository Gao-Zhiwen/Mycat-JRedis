package io.mycat.jredis.datastruct.util;

import io.mycat.jredis.datastruct.UnsafeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
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

    private MemoryManager() {
    }

    private static long baseAddress;//记录分配的内存首地址
    private static long totalSize;//分配内存的总大小

    private static long offset;//记录当前分配的地址偏移量
    private static TreeMap<Integer, LinkedBlockingDeque<Long>> fragments = new TreeMap();//释放的内存碎片

    /**
     * 分配指定大小的堆外内存
     *
     * @param size
     */
    public static void allocateMemory(long size) {
        if (size <= 0)
            throw new InvalidParameterException("size must be positive");

        baseAddress = UnsafeUtil.getUnsafe().allocateMemory(size);
        totalSize = size;
        offset = 0;
        LOGGER.info("allocate off heap memory, size: {}, address: {}", size, baseAddress);
    }

    /**
     * 从创建的堆外内存中分配指定大小的地址空间
     *
     * @param length
     * @return
     */
    public static long malloc(int length) {
        long newOffset = offset + length;
        //顺序分配的空间足够
        if (newOffset < totalSize) {
            long result = baseAddress + offset;
            offset = newOffset;
            return result;
        }

        Integer key = fragments.ceilingKey(length);
        if (key == null) {
            LOGGER.error("memory is used up, can not malloc for size: {}", length);
            return 0;
        }

        long address = fragments.get(key).pollLast();
        int keyIntValue = key.intValue();
        if (keyIntValue > length) {
            LOGGER.debug("memory from fragment is large, return left memory, size: {}, address: {}",
                    keyIntValue - length, address + length);
            free(address + length, keyIntValue - length);
        }
        return address;
    }

    /**
     * 归还分配的内存
     *
     * @param obj
     */
    public static void free(UnsafeObject obj) {
        free(obj.getAddress(), obj.freeSize());
    }

    private static void free(long address, int length) {
        LinkedBlockingDeque<Long> deque = fragments.get(length);
        if (deque == null) {
            deque = new LinkedBlockingDeque();
        }
        deque.offerFirst(address);
        fragments.put(length, deque);
    }
}
