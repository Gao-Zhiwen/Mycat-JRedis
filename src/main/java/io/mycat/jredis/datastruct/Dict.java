package io.mycat.jredis.datastruct;

import io.mycat.jredis.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.Arrays;

/**
 * Desc: 保存rehashidx iterators DictHt[0] DictHt[1]到堆外内存中
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class Dict extends BaseStruct {
    //类型特定函数
    private DictType type;
    //私有数据
    private Object privData;

    //哈希表
    private DictHt[] ht;
    //rehash索引，当rehash不在进行时值为-1
    private int reHashIdx;
    //目前正在运行的安全迭代器的数量
    private int iterators;

    public static long getReHashIdxOffset() {
        return 0;
    }

    public static long getIteratorsOffset() {
        return getReHashIdxOffset() + Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    public static long getHtOffset(int index) {
        return getIteratorsOffset() + Unsafe.ARRAY_INT_INDEX_SCALE
                + (Unsafe.ARRAY_LONG_INDEX_SCALE + Unsafe.ARRAY_INT_INDEX_SCALE * 3) * index;
    }

    @Override public int sizeOf() {
        return Unsafe.ARRAY_LONG_INDEX_SCALE * 4 + Unsafe.ARRAY_INT_INDEX_SCALE * 8;
    }

    public DictType getType() {
        return type;
    }

    public void setType(DictType type) {
        this.type = type;
    }

    public Object getPrivData() {
        return privData;
    }

    public void setPrivData(Object privData) {
        this.privData = privData;
    }

    public DictHt getHt(int index) {
        if (address == 0)
            return null;

        long htAddress = UnsafeUtil.getUnsafe().getLong(address + getHtOffset(index));
        DictHt dictHt = new DictHt();
        dictHt.setAddress(htAddress);
        return dictHt;
    }

    public void setHt(int index, long value) {
        UnsafeUtil.getUnsafe().putLong(address + getHtOffset(index), value);
    }

    public int getRehashidx() {
        if (address == 0)
            return reHashIdx;

        return UnsafeUtil.getUnsafe().getInt(address + getReHashIdxOffset());
    }

    public void setRehashidx(int rehashidx) {
        UnsafeUtil.getUnsafe().putInt(address + getReHashIdxOffset(), rehashidx);
    }

    public int getIterators() {
        if (address == 0)
            return iterators;

        return UnsafeUtil.getUnsafe().getInt(address + getIteratorsOffset());
    }

    public void setIterators(int iterators) {
        UnsafeUtil.getUnsafe().putInt(address + getIteratorsOffset(), iterators);
    }

    @Override public String toString() {
        if (address == 0) {
            return "Dict{type=" + type + ", privData=" + privData + ", ht=" + Arrays.toString(ht)
                    + ", reHashIdx=" + reHashIdx + ", iterators=" + iterators + '}';
        }

        return "Dict{address=" + address + ", type=" + getType() + ", privData=" + getPrivData() +
                ", ht=[" + getHt(0) + "," + getHt(1) + "], rehashidx=" + getRehashidx() +
                ", iterators=" + getIterators() + '}';
    }
}
