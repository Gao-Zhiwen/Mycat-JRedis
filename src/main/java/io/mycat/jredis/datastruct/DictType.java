package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public interface DictType {
    //计算哈希值的函数
    public int hashFunction(final String key);

    //复制键的函数
    public void keyDup(Object privData, final String key);

    //复制值的函数
    public void valDup(Object privdata, Object obj);

    //对比键的函数
    public int keyCompare(Object privdata, final Object key1, final Object key2);

    //销毁键的函数
    public void keyDestructor(Object privdata, Object key);

    //销毁值的函数
    public void valDestructor(Object privdata, Object obj);
}
