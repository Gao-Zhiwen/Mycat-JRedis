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
        return RedisMemory.INT_INDEX_SCALE * 2 + RedisMemory.BYTE_INDEX_SCALE * (getLen()
                + getFree());
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

    public int getFree() {
        checkAddress();
        return RedisMemory.getInt(getAddress() + getFreeOffset());
    }

    public int getLen() {
        checkAddress();
        return RedisMemory.getInt(getAddress() + getLenOffset());
    }

    public byte[] getBuf() {
        checkAddress();
        return RedisMemory.getBytes(getAddress() + getBufOffset(), getLen());
    }

    @Override protected String traceInfo() {
        long address = getAddress();
        if (address == 0) {
            return null;
        }

        SdsHdr sds = new SdsHdr();
        sds.free = getFree();
        sds.len = getLen();
        sds.buf = getBuf();

        return "SdsHdr{len=" + sds.len + ", free=" + sds.free + ", buf=" + Arrays.toString(sds.buf)
                + "}";
    }
}
