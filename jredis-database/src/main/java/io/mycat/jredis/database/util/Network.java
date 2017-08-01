package io.mycat.jredis.database.util;

import io.mycat.jredis.database.bean.RedisClient;

/**
 * Desc:
 *
 * @date: 26/07/2017
 * @author: gaozhiwen
 */
public class Network {
    /**
     * 返回一个错误回复
     * <p>
     * 例子 -ERR unknown command 'foobar'
     *
     * @param c
     * @param err
     */
    public static void addReplyError(RedisClient c, String err) {
        //        addReplyErrorLength(c, err, strlen(err));
    }
}
