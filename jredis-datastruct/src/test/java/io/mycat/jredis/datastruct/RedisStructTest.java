package io.mycat.jredis.datastruct;

import io.mycat.jredis.datastruct.redis.UnsafeDict;
import io.mycat.jredis.datastruct.redis.UnsafeSds;
import io.mycat.jredis.datastruct.util.MemoryManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Desc:
 *
 * @date: 23/07/2017
 * @author: gaozhiwen
 */
public class RedisStructTest {
    @Before public void init() {
        MemoryManager.allocateMemory(1024 * 1024);
    }

    @Test public void stringTest() {
        UnsafeSds unsafeSds = new UnsafeSds();
        long address = MemoryManager.malloc(unsafeSds.sizeOf());
        unsafeSds.setAddress(address);
        unsafeSds.setValue("hello".toCharArray());
        System.out.println(unsafeSds.getValue());
    }

    @Test public void mapCreateTest() {
        UnsafeDict map = UnsafeDict.dictCreate(null, null);
        map.setIterators(1);
        System.out.println(map.getIterators());
        System.out.println(map.getRehashIdx());

        UnsafeDict.UnsafeHashTable table1 = map.getHashTable(0);
        UnsafeDict.UnsafeHashTable table2 = map.getHashTable(1);
        System.out.println(table1.getSize());
        System.out.println(table1.getTable());
        System.out.println(table2.getSizeMask());
        System.out.println(table2.getTable());

        System.out.println("map address: " + map.getAddress());
        System.out.println("hash table address: " + table1.getAddress());
        System.out.println("hash table address: " + table2.getAddress());
    }

    @Test public void mapAddTest() {
        UnsafeDict map = UnsafeDict.dictCreate(null, null);
        map.add(UnsafeSds.sdsNew("hello1".toCharArray()), UnsafeSds.sdsNew("world1".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello2".toCharArray()), UnsafeSds.sdsNew("world2".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello3".toCharArray()), UnsafeSds.sdsNew("world3".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello4".toCharArray()), UnsafeSds.sdsNew("world4".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello5".toCharArray()), UnsafeSds.sdsNew("world5".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello6".toCharArray()), UnsafeSds.sdsNew("world6".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello7".toCharArray()), UnsafeSds.sdsNew("world7".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello8".toCharArray()), UnsafeSds.sdsNew("world8".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello9".toCharArray()), UnsafeSds.sdsNew("world9".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello10".toCharArray()),
                UnsafeSds.sdsNew("world10".toCharArray()));
        map.add(UnsafeSds.sdsNew("hello11".toCharArray()),
                UnsafeSds.sdsNew("world11".toCharArray()));
        System.out.println(map);
    }
}
