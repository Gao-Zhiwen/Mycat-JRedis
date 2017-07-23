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
public class UnsafeInteger extends UnsafeObject {
    // private int value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public int getValue() {
        return UnsafeUtil.getUnsafe().getInt(address);
    }

    public void setValue(int value) {
        UnsafeUtil.getUnsafe().putInt(address, value);
    }
}
