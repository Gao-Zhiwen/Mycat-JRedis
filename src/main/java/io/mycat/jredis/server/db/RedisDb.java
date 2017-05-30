package io.mycat.jredis.server.db;

import java.util.Map;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 21:59
 * <br/>User: gaozhiwen
 */
public class RedisDb {
    // 数据库键空间，保存着数据库中的所有键值对
    private Map dict;

    // 键的过期时间，字典的键为键，字典的值为过期事件 UNIX 时间戳
    private Map<String, Long> expires;

    // 正处于阻塞状态的键
    private Map blockingKeys;

    // 可以解除阻塞的键
    private Map readyKeys;

    // 正在被 WATCH 命令监视的键
    private Map watchedKeys;

    private EvictionPoolEntry evictionPool;

    // 数据库号码
    private int id;

    // 数据库的键的平均 TTL ，统计信息
    private long avgTtl;
}
