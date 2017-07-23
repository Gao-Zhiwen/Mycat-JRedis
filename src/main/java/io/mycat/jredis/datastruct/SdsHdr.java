package io.mycat.jredis.datastruct;

import io.mycat.jredis.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.Arrays;

/**
 * Desc: 保存字符串对象的结构
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class SdsHdr extends BaseStruct {
    private int free;// buf 中剩余可用空间的长度
    private int len;// buf 中已占用空间的长度
    private char[] buf;// 数据空间

    public static long getFreeOffset() {
        return 0;
    }

    public static long getLenOffset() {
        return getFreeOffset() + Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    public static long getBufOffset() {
        return getLenOffset() + Unsafe.ARRAY_INT_INDEX_SCALE;
    }

    @Override public int sizeOf() {
        return Unsafe.ARRAY_INT_INDEX_SCALE * 2 + Unsafe.ARRAY_CHAR_INDEX_SCALE * (getLen()
                + getFree());
    }

    public int getFree() {
        if (address == 0)
            return free;

        return UnsafeUtil.getUnsafe().getInt(address + getFreeOffset());
    }

    public void setFree(int free) {
        UnsafeUtil.getUnsafe().putInt(address + getFreeOffset(), free);
    }

    public int getLen() {
        if (address == 0)
            return len;

        return UnsafeUtil.getUnsafe().getInt(address + getLenOffset());
    }

    public void setLen(int len) {
        UnsafeUtil.getUnsafe().putInt(address + getLenOffset(), len);
    }

    public char[] getBuf() {
        if (address == 0)
            return buf;

        int length = getLen();
        char[] result = new char[length];
        UnsafeUtil.getUnsafe()
                .copyMemory(null, address + getBufOffset(), result, Unsafe.ARRAY_CHAR_BASE_OFFSET,
                        Unsafe.ARRAY_CHAR_INDEX_SCALE * length);
        return result;
    }

    public void setBuf(char[] buf, int length) {
        if (buf == null)
            return;

        if (buf.length >= length) {
            UnsafeUtil.getUnsafe()
                    .copyMemory(buf, Unsafe.ARRAY_CHAR_BASE_OFFSET, null, address + getBufOffset(),
                            length * Unsafe.ARRAY_CHAR_INDEX_SCALE);
        } else {
            UnsafeUtil.getUnsafe()
                    .copyMemory(Arrays.copyOf(buf, length), Unsafe.ARRAY_CHAR_BASE_OFFSET, null,
                            address + getBufOffset(), length * Unsafe.ARRAY_CHAR_INDEX_SCALE);
        }
    }

    @Override public String toString() {
        if (address == 0) {
            return "SdsHdr{len=" + len + ", free=" + free + ", buf=" + Arrays.toString(buf) + '}';
        }

        return "SdsHdr{address=" + address + ", len=" + getLen() + ", free=" + getFree() + ", buf="
                + Arrays.toString(getBuf()) + '}';
    }
}
