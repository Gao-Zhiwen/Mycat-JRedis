package io.mycat.jredis.util;

import io.mycat.jredis.constant.RedisConstant;
import io.mycat.jredis.datastruct.Dict;
import io.mycat.jredis.datastruct.DictType;

/**
 * Desc:支持插入、删除、替换、查找和获取随机元素等操作
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class DictUtil {
    /**
     * 创建一个新的字典
     *
     * @param type
     * @param privData
     * @return
     */
    public static Dict dictCreate(DictType type, Object privData) {


        //        dict * d = zmalloc(sizeof( * d));
        //
        //        _dictInit(d, type, privDataPtr);
        //
        return null;
    }

    /**
     * 初始化哈希表
     *
     * @return
     */
    private static int _dictInit(Dict d, DictType type, Object privData) {
        // 初始化两个哈希表的各项属性值
        // 但暂时还不分配内存给哈希表数组
        //        _dictReset( & d -> ht[0]);
        //        _dictReset( & d -> ht[1]);

        // 设置类型特定函数
        d.setType(type);
        // 设置私有数据
        d.setPrivData(privData);
        // 设置哈希表 rehash 状态
        d.setRehashidx(-1);
        // 设置字典的安全迭代器数量
        d.setIterators(0);

        return RedisConstant.DICT_OK;
    }



    //----

    // 私有协议
    //    static int _dictExpandIfNeeded(dict *ht);
    //    static unsigned long _dictNextPower(unsigned long size);
    //    static int _dictKeyIndex(dict *ht, const void *key);
    //    static int _dictInit(dict *ht, dictType *type, void *privDataPtr);

    public static int dictIntHashFunction(int key) {
        key += ~(key << 15);
        key ^= (key >> 10);
        key += (key << 3);
        key ^= (key >> 6);
        key += ~(key << 11);
        key ^= (key >> 16);
        return key;
    }

    public static int dictIdentityHashFunction(int key) {
        return key;
    }
}
