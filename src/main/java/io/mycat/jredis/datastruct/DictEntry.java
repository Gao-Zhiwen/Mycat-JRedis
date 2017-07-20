package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class DictEntry {
    //键
    private Object key;
    //值
    private Object value;
    //指向下一个哈希表节点，形成链表
    private DictEntry next;
}
