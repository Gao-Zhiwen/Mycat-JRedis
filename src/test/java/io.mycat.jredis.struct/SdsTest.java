package io.mycat.jredis.struct;

import io.mycat.jredis.datastruct.SdsHdr;
import io.mycat.jredis.memory.RedisMemory;
import io.mycat.jredis.util.SdsUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Desc:
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class SdsTest {
    @Before public void init() {
        RedisMemory.allocateMemory(1 * 1024 * 1024);
    }

    @Test public void test() {
        SdsHdr sds = SdsUtil.sdsNew("hello".getBytes());
        sds.trace();
    }
}
