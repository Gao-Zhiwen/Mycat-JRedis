package io.mycat.jredis.datastruct;

import sun.misc.Unsafe;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class DictEntry extends BaseStruct {
    //键
    private BaseStruct key;
    //值
    private BaseStruct value;
    //指向下一个哈希表节点，形成链表
    private DictEntry next;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_LONG_INDEX_SCALE * 3;
    }

    @Override public String toString() {
        if (address == 0) {
            return "DictEntry{" + "key=" + key + ", value=" + value + ", next=" + next + '}';
        }

        return "DictEntry{" + "key=" + key + ", value=" + value + ", next=" + next + '}';
    }

    public BaseStruct getKey() {
        return key;
    }

    public void setKey(BaseStruct key) {
        this.key = key;
    }

    public BaseStruct getValue() {
        return value;
    }

    public void setValue(BaseStruct value) {
        this.value = value;
    }

    public DictEntry getNext() {
        return next;
    }

    public void setNext(DictEntry next) {
        this.next = next;
    }
}
