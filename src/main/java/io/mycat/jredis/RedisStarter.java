package io.mycat.jredis;

import io.mycat.jredis.datastruct.SdsHdr;
import io.mycat.jredis.memory.RedisMemory;

import java.util.Arrays;

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

        char[] res = SdsHdr.sdsNew("hello".toCharArray());
        System.out.println(Arrays.toString(res));
    }
}
