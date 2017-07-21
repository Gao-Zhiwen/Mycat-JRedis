package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class DictIterator {
    private Dict d;//被迭代的字典
    private int table;//正在被迭代的哈希表号码，值可以是 0 或 1
    private int index;//迭代器当前所指向的哈希表索引位置
    private int safe;//标识这个迭代器是否安全

    private DictEntry entry;//当前迭代到的节点的指针
    //当前迭代节点的下一个节点(在安全迭代器运作时， entry 所指向的节点可能会被修改，所以需要一个额外的指针来保存下一节点的位置，从而防止指针丢失)
    private DictEntry nextEntry;

    private long fingerPrint;
}
