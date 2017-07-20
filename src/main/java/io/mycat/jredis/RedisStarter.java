package io.mycat.jredis;

import io.mycat.jredis.memory.RedisMemory;
import io.mycat.jredis.util.SdsUtil;

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

        char[] c = "hello wolrd".toCharArray();
        char[] d = "nihao".toCharArray();

        char[] res1 = SdsUtil.sdsNewLen(c, 20);
        char[] res2 = SdsUtil.sdsNewLen(d, 10);

        System.out.println(Arrays.toString(res1));
        System.out.println(res2[5]);
    }
}
