package io.mycat.jredis.datastruct;

import io.mycat.jredis.memory.RedisMemory;

/**
 * Desc: 保存字符串对象的结构
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class SdsHdr extends BaseStruct {
    private int len;// buf 中已占用空间的长度
    private int free;// buf 中剩余可用空间的长度
    private char[] buf;// 数据空间
    
    @Override public int getSize() {
        return RedisMemory.INT_INDEX_SCALE * 2 + RedisMemory.CHAR_INDEX_SCALE * (len + free);
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public char[] getBuf() {
        return buf;
    }

    public void setBuf(char[] buf) {
        this.buf = buf;
    }
}
