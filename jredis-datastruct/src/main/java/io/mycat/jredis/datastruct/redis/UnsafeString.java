package io.mycat.jredis.datastruct.redis;

import io.mycat.jredis.datastruct.UnsafeObject;
import io.mycat.jredis.datastruct.redis.constant.RedisConstant;
import io.mycat.jredis.datastruct.util.MemoryManager;
import io.mycat.jredis.datastruct.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.util.Arrays;

/**
 * Desc:
 *
 * @date: 23/07/2017
 * @author: gaozhiwen
 */
public class UnsafeString extends UnsafeObject {
    // private int length;//已经使用的char长度 offset:0
    // private int free;//剩余空间 offset:int
    // private char[] value;//数据段 offset:int*2

    @Override public int sizeOf() {
        return Unsafe.ARRAY_SHORT_INDEX_SCALE * 2;
    }

    public int sizeOf(int charInitLen) {
        return Unsafe.ARRAY_SHORT_INDEX_SCALE * 2 + Unsafe.ARRAY_CHAR_INDEX_SCALE * charInitLen;
    }

    @Override public int freeSize() {
        return sizeOf(getLength() + getFree());
    }

    public int getLength() {
        return UnsafeUtil.getUnsafe().getInt(address);
    }

    public void setLength(int length) {
        UnsafeUtil.getUnsafe().putInt(address, length);
    }

    public int getFree() {
        return UnsafeUtil.getUnsafe().getInt(address + Unsafe.ARRAY_INT_INDEX_SCALE);
    }

    public void setFree(int free) {
        UnsafeUtil.getUnsafe().putInt(address, free);
    }

    public char[] getValue() {
        int length = getLength();
        char[] result = new char[length];
        UnsafeUtil.getUnsafe().copyMemory(null, address + Unsafe.ARRAY_INT_INDEX_SCALE * 2, result,
                Unsafe.ARRAY_CHAR_BASE_OFFSET, Unsafe.ARRAY_CHAR_INDEX_SCALE * length);
        return result;
    }

    public void setValue(char[] value) {
        setValue(value, value.length);
    }

    public void setValue(char[] value, int length) {
        if (value.length >= length) {
            UnsafeUtil.getUnsafe().copyMemory(value, Unsafe.ARRAY_CHAR_BASE_OFFSET, null,
                    address + Unsafe.ARRAY_INT_INDEX_SCALE * 2,
                    length * Unsafe.ARRAY_CHAR_INDEX_SCALE);
        } else {
            UnsafeUtil.getUnsafe()
                    .copyMemory(Arrays.copyOf(value, length), Unsafe.ARRAY_CHAR_BASE_OFFSET, null,
                            address + Unsafe.ARRAY_INT_INDEX_SCALE * 2,
                            length * Unsafe.ARRAY_CHAR_INDEX_SCALE);
        }
    }



    /**
     * 根据给定的初始化字符串 init 和字符串长度 initlen
     * 创建一个新的 sds
     *
     * @param init    初始化字符数组
     * @param initLen 初始化字符串的长度
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static UnsafeString strNewLen(char[] init, int initLen) {
        UnsafeString unsafeString = new UnsafeString();
        long address = MemoryManager.malloc(unsafeString.sizeOf(initLen));

        // 内存分配失败，返回
        if (address == 0) {
            return null;
        }

        unsafeString.setAddress(address);

        unsafeString.setFree(0);// 新 sds 不预留任何空间 free
        unsafeString.setLength(initLen);// 设置初始化长度 len
        if (initLen >= 0 && init != null) {
            // 如果有指定初始化内容，将它们复制到 sdshdr 的 buf 中
            unsafeString.setValue(init, initLen);
        }

        return unsafeString;
    }

    /**
     * 创建并返回一个只保存了空字符串 "" 的 sds
     *
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static UnsafeString strEmp() {
        return strNewLen(null, 0);
    }

    /**
     * 根据给定字符串 init ，创建一个包含同样字符串的 sds
     *
     * @param init 如果输入为 NULL ，那么创建一个空白 sds，否则，新创建的 sds 中包含和 init 内容相同字符串
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static UnsafeString strNew(final char[] init) {
        int initLen = (init == null) ? 0 : init.length;
        return strNewLen(init, initLen);
    }

    /**
     * 复制给定 sds 的副本
     *
     * @param s
     * @return 创建成功返回输入 sds 的副本，创建失败返回 NULL
     */
    public static UnsafeString strDup(final UnsafeString s) {
        return strNewLen(s.getValue(), (s == null) ? 0 : s.getLength());
    }

    /**
     * 释放给定的 sds
     */
    public void strFree() {
        MemoryManager.free(this);
    }

    /**
     * 在不释放 SDS 的字符串空间的情况下，
     * 重置 SDS 所保存的字符串为空字符串。
     */
    public void strClear() {
        // 重新计算属性 free += len, len = 0;  设置free的值
        this.setFree(this.getFree() + this.getLength());
        //设置len的值
        this.setLength(0);
    }

    public UnsafeString makeRoomFor(int addLen) {
        // 目前的空余空间已经足够，无须再进行扩展，直接返回
        if (this.getFree() >= addLen)
            return this;

        // 获取 目前已占用空间的长度
        int len = this.getLength();
        // 最少需要的长度
        int newLen = len + addLen;
        int headerLen = Unsafe.ARRAY_INT_INDEX_SCALE * 2;

        // 根据新长度，分配新空间所需的大小
        if (newLen < RedisConstant.SDS_MAX_PREALLOC) {
            // 如果新长度小于 SDS_MAX_PREALLOC，那么为它分配两倍于所需长度的空间
            newLen *= 2;
        } else {
            // 否则，分配长度为目前长度加上 SDS_MAX_PREALLOC
            newLen += RedisConstant.SDS_MAX_PREALLOC;
        }

        long newAdd = MemoryManager.malloc(headerLen + newLen);

        // 内存不足，分配失败，返回
        if (newAdd == 0)
            return null;

        // 分配成功后拷贝原内容到新地址空间并回收原地址空间
        UnsafeUtil.getUnsafe().copyMemory(null, this.getAddress(), null, newAdd, sizeOf(len));
        MemoryManager.free(this);

        UnsafeString str = new UnsafeString();
        str.setAddress(newAdd);

        // 更新 sds 的空余长度 free
        str.setFree(newLen - len);

        return str;
    }
}
