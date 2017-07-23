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
public class UnsafeBoolean extends UnsafeObject {
    // private boolean value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_BOOLEAN_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public boolean getValue() {
        return UnsafeUtil.getUnsafe().getBoolean(null, address);
    }

    public void setValue(boolean value) {
        UnsafeUtil.getUnsafe().putBoolean(null, address, value);
    }
}
