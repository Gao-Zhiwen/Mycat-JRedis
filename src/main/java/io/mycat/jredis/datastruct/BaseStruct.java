package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public abstract class BaseStruct {
    protected long address;//获取首地址

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    /**
     * 用于释放内存空间时指定内存大小
     *
     * @return
     */
    public abstract int sizeOf();
}
