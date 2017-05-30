package io.mycat.jredis.server;

import io.mycat.jredis.client.RedisClient;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 14:54
 * <br/>User: gaozhiwen
 */
public interface Command {
    void doCommand(RedisClient redisClient);
}
