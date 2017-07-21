package io.mycat.jredis.memory;

import io.mycat.jredis.util.UnsafeUtil;
import net.bramp.unsafe.UnsafeHelper;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * Desc:
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class RedisMemory {
    private static long baseAddress;//记录分配的内存首地址
    private static long totalSize;//分配内存的总大小

    private static long offset;//记录当前分配的地址偏移量
    private static TreeMap<Long, Integer> freeMemory = new TreeMap<Long, Integer>();//记录释放的资源

    public static final int INT_INDEX_SCALE = getIndexScale(int[].class);
    public static final int INT_BASE_OFFSET = getBaseOffset(int[].class);
    public static final int BYTE_INDEX_SCALE = getIndexScale(byte[].class);
    public static final int BYTE_BASE_OFFSET = getBaseOffset(byte[].class);

    /**
     * 分配指定大小的堆外内存
     *
     * @param size
     */
    public static void allocateMemory(long size) {
        baseAddress = UnsafeHelper.getUnsafe().allocateMemory(size);
        if (baseAddress == 0) {
            throw new OutOfMemoryError();
        }
        //        offset = 0;
        RedisMemory.totalSize = size;
    }

    public static long getAddress(long offset) {
        return baseAddress + offset;
    }

    public static TreeMap<Long, Integer> getFreeMemory() {
        return freeMemory;
    }

    /**
     * 从创建的堆外内存中分配指定大小的地址空间
     *
     * @param length
     * @return
     */
    public static long alloc(long length) {
        //        if (length <= 0) {
        //            return 0;
        //        }

        long newOffset = offset + length;
        //顺序分配的空间足够
        if (newOffset < totalSize) {
            offset = newOffset;
            return baseAddress + newOffset;
        }

        Long ceilKey = freeMemory.ceilingKey(length);
        if (ceilKey == null) {
            //碎片空间不够分配
            return 0;
        }
        freeMemory.remove(ceilKey);
        return baseAddress + ceilKey.longValue();
    }

    /**
     * 归还分配的地址空间
     *
     * @param address
     * @param length
     */
    public static void free(long address, int length) {
        freeMemory.put(address, length);
    }

    public static void putInt(long address, int value) {
        UnsafeUtil.getUnsafe().putInt(address, value);
    }

    public static int getInt(long address) {
        return UnsafeUtil.getUnsafe().getInt(address);
    }

    /**
     * 将指定字符数组中的内容拷贝到内存中
     *
     * @param address
     * @param bytes
     */
    public static void putBytes(long address, byte[] bytes) {
        if (bytes == null)
            return;
        int length = bytes.length;
        UnsafeUtil.copyMemory(bytes, BYTE_BASE_OFFSET, null, address, length * BYTE_INDEX_SCALE);
    }

    public static void putBytes(long address, byte[] bytes, int length) {
        if (bytes == null)
            return;

        int byteLen = bytes.length;
        if (byteLen >= length) {
            UnsafeUtil
                    .copyMemory(bytes, BYTE_BASE_OFFSET, null, address, length * BYTE_INDEX_SCALE);
        } else {
            UnsafeUtil.copyMemory(Arrays.copyOf(bytes, length), BYTE_BASE_OFFSET, null, address,
                    length * BYTE_INDEX_SCALE);
        }
    }

    /**
     * 从内存的指定位置获取字符数组
     *
     * @param address
     * @param length
     * @return
     */
    public static byte[] getBytes(long address, int length) {
        byte[] result = new byte[length];
        UnsafeUtil.copyMemory(null, address, result, BYTE_BASE_OFFSET, length * BYTE_INDEX_SCALE);
        return result;
    }

    private static int getBaseOffset(Class clazz) {
        return UnsafeUtil.getUnsafe().arrayBaseOffset(clazz);
    }

    private static int getIndexScale(Class clazz) {
        return UnsafeUtil.getUnsafe().arrayIndexScale(clazz);
    }
}
