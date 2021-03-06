package io.mycat.jredis.datastruct.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Desc:
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class UnsafeUtil {
    private static final Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}
