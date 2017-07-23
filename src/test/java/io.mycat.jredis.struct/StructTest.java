package io.mycat.jredis.struct;

import io.mycat.jredis.datastruct.Dict;
import io.mycat.jredis.memory.MemoryManager;
import io.mycat.jredis.util.DictUtil;
import org.junit.Before;
import org.junit.Test;
import sun.misc.Unsafe;

/**
 * Desc:
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class StructTest {
    @Before public void init() {
        MemoryManager.allocateMemory(1 * 1024 * 1024);
    }

    //    @Test public void sdsTest() {
    //        SdsHdr sds = SdsUtil.sdsNew("你好".toCharArray());
    //        System.out.println(sds);
    //        System.out.println(SdsUtil.sdsMakeRoomFor(sds, 10));
    //    }

    @Test public void dictTest() {
        Dict dict = DictUtil.dictCreate(null, null);
        System.out.println(dict);
    }

    @Test public void test() {
        System.out.println(Unsafe.ARRAY_LONG_INDEX_SCALE);
        System.out.println(Unsafe.ARRAY_INT_BASE_OFFSET * 3);
        long off = Dict.getHtOffset(1) - Dict.getHtOffset(0);
        System.out.println(off);
    }
}
