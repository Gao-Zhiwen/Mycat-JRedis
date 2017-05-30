package io.mycat.jredis.struct;

/**
 * Desc:保存字符串对象的类
 * <p/>Date: 30/05/2017
 * <br/>Time: 14:09
 * <br/>User: gaozhiwen
 */
public class SdsHdr {
    private int len;
    private int free;
    private String buf;

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

    public String getBuf() {
        return buf;
    }

    public void setBuf(String buf) {
        this.buf = buf;
    }
}
