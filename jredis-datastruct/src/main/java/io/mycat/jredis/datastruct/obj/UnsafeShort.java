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
public class UnsafeShort extends UnsafeObject {
    // private short value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_SHORT_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public short getValue() {
        return UnsafeUtil.getUnsafe().getShort(address);
    }

    public void setValue(short value) {
        UnsafeUtil.getUnsafe().putShort(address, value);
    }
}
