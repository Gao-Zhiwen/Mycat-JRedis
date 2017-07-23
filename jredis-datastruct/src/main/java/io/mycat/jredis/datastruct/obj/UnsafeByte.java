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
public class UnsafeByte extends UnsafeObject {
    // private byte value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_BYTE_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public byte getValue() {
        return UnsafeUtil.getUnsafe().getByte(address);
    }

    public void setValue(byte value) {
        UnsafeUtil.getUnsafe().putByte(address, value);
    }
}
