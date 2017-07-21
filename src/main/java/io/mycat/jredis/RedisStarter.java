package io.mycat.jredis;

import io.mycat.jredis.datastruct.SdsHdr;
import io.mycat.jredis.memory.RedisMemory;
import io.mycat.jredis.util.SdsUtil;

/**
 * Desc:
 *
 * @date: 10/06/2017
 * @author: gaozhiwen
 */
public class RedisStarter {
    public static void main(String[] args) {
        long size = 1 * 1024 * 1024;
        RedisMemory.allocateMemory(size);

        SdsHdr sdsHdr = SdsUtil.sdsNew("hello".getBytes());
        SdsHdr newSds = SdsUtil.sdsMakeRoomFor(sdsHdr, 20);
        System.out.println(sdsHdr.trace());
        System.out.println(newSds.trace());
        System.out.println(RedisMemory.getFreeMemory().get(sdsHdr.getAddress()));
    }
}
