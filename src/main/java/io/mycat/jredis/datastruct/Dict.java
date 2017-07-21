package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class Dict {
    //类型特定函数
    private DictType type;
    //私有数据
    private Object privData;
    //哈希表
    private DictHt[] ht;
    //rehash索引，当rehash不在进行时值为-1
    private int rehashidx;
    //目前正在运行的安全迭代器的数量
    private int iterators;

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

    public DictHt[] getHt() {
        return ht;
    }

    public void setHt(DictHt[] ht) {
        this.ht = ht;
    }

    public int getRehashidx() {
        return rehashidx;
    }

    public void setRehashidx(int rehashidx) {
        this.rehashidx = rehashidx;
    }

    public int getIterators() {
        return iterators;
    }

    public void setIterators(int iterators) {
        this.iterators = iterators;
    }
}
