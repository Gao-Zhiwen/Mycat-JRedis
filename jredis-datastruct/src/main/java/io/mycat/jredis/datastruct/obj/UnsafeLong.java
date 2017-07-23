package io.mycat.jredis.datastruct.obj;

import io.mycat.jredis.datastruct.UnsafeObject;
import io.mycat.jredis.datastruct.util.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * Desc:
 *
 * @date: 23/07/2017
 * @author: gaozhiwen
 */
public class UnsafeLong extends UnsafeObject {
    // private long value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_LONG_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public long getValue() {
        return UnsafeUtil.getUnsafe().getLong(address);
    }

    public void setValue(long value) {
        UnsafeUtil.getUnsafe().putLong(address, value);
    }
}
