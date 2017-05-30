package io.mycat.jredis.operation;

import io.mycat.jredis.command.RedisCommand;
import io.mycat.jredis.struct.RedisObject;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 23:15
 * <br/>User: gaozhiwen
 */
public class RedisOp {
    // 参数
    private RedisObject argv;

    // 参数数量、数据库 ID 、传播目标
    private int argc;
    private int dbId;
    private int target;

    // 被执行命令的指针
    private RedisCommand cmd;
}
