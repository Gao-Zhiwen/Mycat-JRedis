package io.mycat.jredis.command;

import io.mycat.jredis.client.RedisClient;

public interface Command {
    void doCommand(RedisClient redisClient);
}
