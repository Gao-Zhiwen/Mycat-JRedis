package io.mycat.jredis.datastruct;

import io.mycat.jredis.datastruct.redis.UnsafeMap;
import io.mycat.jredis.datastruct.redis.UnsafeString;
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
        UnsafeString unsafeString = new UnsafeString();
        long address = MemoryManager.malloc(unsafeString.sizeOf());
        unsafeString.setAddress(address);
        unsafeString.setValue("hello".toCharArray());
        System.out.println(unsafeString.getValue());
    }

    @Test public void mapCreateTest() {
        UnsafeMap map = UnsafeMap.dictCreate(null, null);
        map.setIterators(1);
        System.out.println(map.getIterators());
        System.out.println(map.getRehashIdx());

        UnsafeMap.UnsafeHashTable table1 = map.getHashTable(0);
        UnsafeMap.UnsafeHashTable table2 = map.getHashTable(1);
        System.out.println(table1.getSize());
        System.out.println(table1.getTable());
        System.out.println(table2.getSizeMask());
        System.out.println(table2.getTable());

        System.out.println("map address: " + map.getAddress());
        System.out.println("hash table address: " + table1.getAddress());
        System.out.println("hash table address: " + table2.getAddress());
    }

    @Test public void mapAddTest() {
        UnsafeMap map = UnsafeMap.dictCreate(null, null);
        UnsafeObject key = UnsafeString.strNew("hello".toCharArray());
        UnsafeObject value = UnsafeString.strNew("world".toCharArray());
        map.add(key, value);
        System.out.println(map);
    }
}
