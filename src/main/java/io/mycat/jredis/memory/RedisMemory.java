package io.mycat.jredis.memory;

import io.mycat.jredis.util.UnsafeUtil;
import net.bramp.unsafe.UnsafeHelper;

import java.util.Map;
import java.util.TreeMap;

/**
 * Desc:
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class RedisMemory {
    private static long address;//记录分配的内存首地址

    private static long offset;//记录当前分配的地址偏移量
    private static Map<Long, Integer> freeMemory = new TreeMap<Long, Integer>();//记录释放的资源

    public static final int INT_INDEX_SCALE = getIndexScale(int[].class);
    public static final int INT_BASE_OFFSET = getBaseOffset(int[].class);
    public static final int CHAR_INDEX_SCALE = getIndexScale(char[].class);
    public static final int CHAR_BASE_OFFSET = getBaseOffset(char[].class);

    /**
     * 分配指定大小的堆外内存
     *
     * @param size
     */
    public static void allocateMemory(long size) {
        address = UnsafeHelper.getUnsafe().allocateMemory(size);
        if (address == 0) {
            throw new RuntimeException("Out of Memory");
        }
    }

    /**
     * 从创建的堆外内存中分配指定大小的地址空间
     *
     * @param length
     * @return
     */
    public static long alloc(long length) {
        offset += length;
        return address + offset;
    }

    /**
     * 归还分配的地址空间
     *
     * @param offset
     * @param length
     */
    public static void free(long offset, int length) {
        freeMemory.put(offset, length);
    }

    public static void putInt(long offset, int value) {
        UnsafeUtil.getUnsafe().putInt(address + offset, value);
    }

    public static void getInt(long offset) {
        UnsafeUtil.getUnsafe().getInt(address + offset);
    }

    /**
     * 将指定字符数组中的内容拷贝到内存中
     *
     * @param offset
     * @param chars
     */
    public static void putChars(long offset, char[] chars) {
        if (chars == null) {
            return;
        }
        int length = chars.length;
        UnsafeUtil.copyMemory(chars, CHAR_BASE_OFFSET, null, offset, length * CHAR_INDEX_SCALE);
    }

    /**
     * 从内存的指定位置获取字符数组
     *
     * @param offset
     * @param length
     * @return
     */
    public static char[] getChars(long offset, int length) {
        char[] result = new char[length];
        UnsafeUtil.copyMemory(null, offset, result, CHAR_BASE_OFFSET, length * CHAR_INDEX_SCALE);
        return result;
    }

    private static int getBaseOffset(Class clazz) {
        return UnsafeUtil.getUnsafe().arrayBaseOffset(clazz);
    }

    private static int getIndexScale(Class clazz) {
        return UnsafeUtil.getUnsafe().arrayIndexScale(clazz);
    }
}
