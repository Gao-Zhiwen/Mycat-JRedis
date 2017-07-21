package io.mycat.jredis.datastruct;

import io.mycat.jredis.memory.RedisMemory;

import java.util.Arrays;

/**
 * Desc: 保存字符串对象的结构
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class SdsHdr extends BaseStruct {
    private int free;// buf 中剩余可用空间的长度
    private int len;// buf 中已占用空间的长度
    private byte[] buf;// 数据空间

    @Override public int getSize() {
        return RedisMemory.INT_INDEX_SCALE * 2 + RedisMemory.BYTE_INDEX_SCALE * (getLenMem()
                + getFreeMem());
    }

    @Override public String trace() {
        long address = getAddress();
        if (address == 0) {
            return null;
        }

        SdsHdr sds = new SdsHdr();
        sds.setFree(getFreeMem());
        sds.setLen(getLenMem());
        sds.setBuf(getBufMem());
        return sds.toString();
    }

    public static long getFreeOffset() {
        return 0;
    }

    public static long getLenOffset() {
        return RedisMemory.INT_INDEX_SCALE;
    }

    public static long getBufOffset() {
        return RedisMemory.INT_INDEX_SCALE * 2;
    }

    public int getFreeMem() {
        checkAddress();
        return RedisMemory.getInt(getAddress() + getFreeOffset());
    }

    public int getLenMem() {
        checkAddress();
        return RedisMemory.getInt(getAddress() + getLenOffset());
    }

    public byte[] getBufMem() {
        checkAddress();
        return RedisMemory.getBytes(getAddress() + getBufOffset(), getLenMem());
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    @Override public String toString() {
        return "SdsHdr{" +
                "len=" + len +
                ", free=" + free +
                ", buf=" + Arrays.toString(buf) +
                '}';
    }
}
