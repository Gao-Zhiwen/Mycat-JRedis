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
public class UnsafeFloat extends UnsafeObject {
    // private float value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_FLOAT_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public float getValue() {
        return UnsafeUtil.getUnsafe().getFloat(address);
    }

    public void setValue(float value) {
        UnsafeUtil.getUnsafe().putFloat(address, value);
    }
}
