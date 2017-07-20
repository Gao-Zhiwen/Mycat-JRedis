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
    public void keyDup(Object priviData, final String key);

    //复制值的函数
    public void valDup();

    //对比键的函数
    public int keyCompare();

    //销毁键的函数
    public void keyDestructor();

    //销毁值的函数
    public void valDestructor();
}
