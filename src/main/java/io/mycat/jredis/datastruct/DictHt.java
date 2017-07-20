package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class DictHt {
    //哈希表数组
    private DictEntry[] table;
    //哈希表大小
    private int size;
    //哈希表大小掩码，用于计算索引值
    private int sizeMask;
    //哈希表已有节点的数量
    private int used;
}
