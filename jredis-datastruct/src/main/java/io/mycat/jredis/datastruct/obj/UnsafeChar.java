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
public class UnsafeChar extends UnsafeObject {
    // private char value;

    @Override public int sizeOf() {
        return Unsafe.ARRAY_CHAR_INDEX_SCALE;
    }

    @Override public int freeSize() {
        return sizeOf();
    }

    public char getValue() {
        return UnsafeUtil.getUnsafe().getChar(address);
    }

    public void setValue(char value) {
        UnsafeUtil.getUnsafe().putChar(address, value);
    }
}
