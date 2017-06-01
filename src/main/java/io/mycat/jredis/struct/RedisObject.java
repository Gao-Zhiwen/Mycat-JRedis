package io.mycat.jredis.struct;

/**
 * Desc: Redis 对象
 *
 * @date: 01/06/2017
 * @author: gaozhiwen
 */
public class RedisObject {
    // 类型
    private int type;

    // 编码
    private int encoding;

    // 对象最后一次被访问的时间
    private long lru;

    // 引用计数
    private int refcount;

    // 指向实际值的指针
    private RedisObject ptr;
}
