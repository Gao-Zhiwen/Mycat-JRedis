package io.mycat.jredis.datastruct;

/**
 * Desc: 操作堆外内存的基类
 *
 * @date: 23/07/2017
 * @author: gaozhiwen
 */
public abstract class UnsafeObject {
    protected long address;

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    /**
     * 指定创建对象时需要分配的内存大小
     *
     * @return
     */
    public abstract int sizeOf();

    /**
     * 指定释放内存空间时需要释放的内存大小
     *
     * @return
     */
    public abstract int freeSize();
}
