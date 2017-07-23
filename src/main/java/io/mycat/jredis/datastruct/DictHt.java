package io.mycat.jredis.datastruct;

import io.mycat.jredis.memory.MemoryManager;
import io.mycat.jredis.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.Arrays;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class DictHt extends BaseStruct {
    //哈希表数组
    private DictEntry[] table;
    //哈希表大小
    private int size;
    //哈希表大小掩码，用于计算索引值
    private int sizeMask;
    //哈希表已有节点的数量
    private int used;

    public static long getTableOffset() {
        return 0;
    }

    public static long getSizeOffset() {
        return getTableOffset() + Unsafe.ARRAY_LONG_INDEX_SCALE;
    }

    public static long getSizeMaskOffset() {
        return getSizeOffset() + Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    public static long getUsedOffset() {
        return getSizeMaskOffset() + Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    @Override public int sizeOf() {
        return Unsafe.ARRAY_LONG_INDEX_SCALE + Unsafe.ARRAY_INT_INDEX_SCALE * 3
                + getSize() * MemoryManager.sizeOf(new DictEntry());
    }

    public DictEntry[] getTable() {
        if (address == 0)
            return null;

        long add = UnsafeUtil.getUnsafe().getLong(address + getTableOffset());
        int size = getSize();
        DictEntry[] tables = new DictEntry[size];
        for (int i = 0; i < size; i++) {
            DictEntry entry = new DictEntry();
            entry.setAddress(add + (MemoryManager.sizeOf(entry)) * i);
            tables[i] = entry;
        }
        return tables;
    }

    public void setTable(long value) {
        UnsafeUtil.getUnsafe().putLong(address + getTableOffset(), value);
    }

    public int getSize() {
        if (address == 0)
            return size;

        return UnsafeUtil.getUnsafe().getInt(address + getSizeOffset());
    }

    public void setSize(int size) {
        UnsafeUtil.getUnsafe().putInt(address + getSizeOffset(), size);
    }

    public int getSizeMask() {
        if (address == 0)
            return sizeMask;

        return UnsafeUtil.getUnsafe().getInt(address + getSizeMaskOffset());
    }

    public void setSizeMask(int sizeMask) {
        UnsafeUtil.getUnsafe().putInt(address + getSizeMaskOffset(), sizeMask);
    }

    public int getUsed() {
        if (address == 0)
            return used;

        return UnsafeUtil.getUnsafe().getInt(address + getUsedOffset());
    }

    public void setUsed(int used) {
        UnsafeUtil.getUnsafe().putInt(address + getUsedOffset(), used);
    }

    @Override public String toString() {
        if (address == 0) {
            return "DictHt{table=" + Arrays.toString(table) + ", size=" + size + ", sizeMask="
                    + sizeMask + ", used=" + used + '}';
        }

        return "DictHt{address=" + address + ", table=" + Arrays.toString(table) + ", size="
                + getSize() + ", sizeMask=" + getSizeMask() + ", used=" + getUsed() + '}';
    }
}
