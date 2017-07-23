package io.mycat.jredis.util;

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

//    public static long getAddress(Object target) {
//        Object objArray[] = new Object[1];
//        objArray[0] = target;
//
//        long arrayOffset = Unsafe.ARRAY_OBJECT_BASE_OFFSET;
//        long arrayIndex = Unsafe.ARRAY_OBJECT_INDEX_SCALE;
//        long value = 0L;
//
//        if (8 == arrayIndex) {
//            value = unsafe.getLong(objArray, arrayOffset);
//        } else {
//            value = unsafe.getInt(objArray, arrayOffset);
//            //            value = normalize(unsafe.getInt(objArray, arrayOffset));
//            //            if (8 == unsafe.addressSize()) {
//            //                value = value << 3;
//            //            }
//        }
//        return value;
//    }
//
//    private static long normalize(int value) {
//        if (value >= 0)
//            return value;
//        return value & 0xFFFFFFFFL;
//    }
}
