package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public abstract class BaseStruct {
    private long address;//获取首地址

    public long getAddress() {
        checkAddress();
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public void checkAddress() {
        if (getAddress() == 0)
            throw new RuntimeException("address未加载");
    }

    /**
     * 用于获取数据结构在内存占用的大小
     *
     * @return
     */
    public abstract int getSize();

    /**
     * 用于测试跟踪内存数据
     *
     * @return
     */
    public abstract String trace();
}
