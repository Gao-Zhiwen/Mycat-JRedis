package io.mycat.jredis.datastruct.redis;

import io.mycat.jredis.datastruct.UnsafeObject;

/**
 * Desc:字典类型特定函数
 *
 * @date: 25/07/2017
 * @author: gaozhiwen
 */
public interface Type {
    // 计算哈希值的函数
    int hashFunction(final UnsafeObject key);

    // 复制键的函数
    UnsafeObject keyDup(UnsafeObject privData, final UnsafeObject key);

    // 复制值的函数
    UnsafeObject valDup(UnsafeObject privData, final UnsafeObject obj);

    // 对比键的函数
    boolean keyCompare(UnsafeObject privData, final UnsafeObject key1, final UnsafeObject key2);

    // 销毁键的函数
    void keyDestructor(UnsafeObject privData, UnsafeObject key);

    // 销毁值的函数
    void valDestructor(UnsafeObject privData, UnsafeObject obj);
}
