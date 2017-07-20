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
    private int tReHashIdx;
}
