package io.mycat.jredis;

import io.mycat.jredis.util.SdsUtil;

/**
 * Desc:
 *
 * @date: 10/06/2017
 * @author: gaozhiwen
 */
public class RedisStarter {
    public static void main(String[] args) {
        //        RedisMemory.allocateMemory(1 * 1024 * 1024);
        SdsUtil.sdsNew("he".getBytes());
    }
}
