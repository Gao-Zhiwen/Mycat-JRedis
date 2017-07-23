package io.mycat.jredis.datastruct;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public interface DictType {
    //计算哈希值的函数
    public int hashFunction(final BaseStruct key);

    //复制键的函数
    public BaseStruct keyDup(Object privData, final BaseStruct key);

    //复制值的函数
    public BaseStruct valDup(Object privdata, BaseStruct obj);

    //对比键的函数
    public boolean keyCompare(Object privdata, final BaseStruct key1, final BaseStruct key2);

    //销毁键的函数
    public void keyDestructor(Object privdata, BaseStruct key);

    //销毁值的函数
    public void valDestructor(Object privdata, BaseStruct obj);
}
