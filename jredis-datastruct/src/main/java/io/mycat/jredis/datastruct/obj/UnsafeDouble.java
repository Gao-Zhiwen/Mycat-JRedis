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
public class UnsafeDouble extends UnsafeObject {
    // private double value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_DOUBLE_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public double getValue() {
        return UnsafeUtil.getUnsafe().getDouble(address);
    }

    public void setValue(double value) {
        UnsafeUtil.getUnsafe().putDouble(address, value);
    }
}
