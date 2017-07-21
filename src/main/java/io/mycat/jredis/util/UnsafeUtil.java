package io.mycat.jredis.util;

import net.bramp.unsafe.UnsafeHelper;
import sun.misc.Unsafe;

/**
 * Desc:
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class UnsafeUtil {
    public static Unsafe getUnsafe() {
        return UnsafeHelper.getUnsafe();
    }

    public static void copyMemory(Object src, long srcAddress, Object desc, long descAddress,
            long length) {
        getUnsafe().copyMemory(src, srcAddress, desc, descAddress, length);
    }
}
