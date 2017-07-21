package io.mycat.jredis.util;

import io.mycat.jredis.constant.RedisConstant;
import io.mycat.jredis.datastruct.SdsHdr;
import io.mycat.jredis.memory.RedisMemory;

/**
 * Desc: 存储结构： free(int) - len(int) - buf(char[])
 *
 * @date: 21/07/2017
 * @author: gaozhiwen
 */
public class SdsUtil {
    /**
     * 根据给定的初始化字符串 init 和字符串长度 initlen
     * 创建一个新的 sds
     *
     * @param init    初始化字符数组
     * @param initLen 初始化字符串的长度
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static SdsHdr sdsNewLen(byte[] init, int initLen) {
        long address = RedisMemory.alloc(RedisMemory.INT_INDEX_SCALE * 2 + initLen);

        // 内存分配失败，返回
        if (address == 0) {
            return null;
        }

        // 新 sds 不预留任何空间 free
        RedisMemory.putInt(address + SdsHdr.getFreeOffset(), 0);
        // 设置初始化长度 len
        RedisMemory.putInt(address + SdsHdr.getLenOffset(), initLen);

        // 如果有指定初始化内容，将它们复制到 sdshdr 的 buf 中
        if (initLen >= 0 && init != null)
            RedisMemory.putBytes(address + SdsHdr.getBufOffset(), init, initLen);

        SdsHdr sdsHdr = new SdsHdr();
        sdsHdr.setAddress(address);

        return sdsHdr;
    }

    /**
     * 创建并返回一个只保存了空字符串 "" 的 sds
     *
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static SdsHdr sdsEmpty() {
        return sdsNewLen(null, 0);
    }

    /**
     * 根据给定字符串 init ，创建一个包含同样字符串的 sds
     *
     * @param init 如果输入为 NULL ，那么创建一个空白 sds，否则，新创建的 sds 中包含和 init 内容相同字符串
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static SdsHdr sdsNew(final byte[] init) {
        int initLen = (init == null) ? 0 : init.length;
        return sdsNewLen(init, initLen);
    }

    /**
     * 复制给定 sds 的副本
     *
     * @param s
     * @return 创建成功返回输入 sds 的副本，创建失败返回 NULL
     */
    public static SdsHdr sdsDup(final SdsHdr s) {
        return sdsNewLen(s.getBuf(), (s == null) ? 0 : s.getLen());
    }

    /**
     * 释放给定的 sds
     *
     * @param s
     */
    public static void sdsFree(SdsHdr s) {
        if (s == null) {
            return;
        }
        RedisMemory.free(s.getAddress(), s.getSize());
    }

    /**
     * 在不释放 SDS 的字符串空间的情况下，
     * 重置 SDS 所保存的字符串为空字符串。
     *
     * @param s
     */
    public static void sdsClear(SdsHdr s) {
        // 重新计算属性 free += len, len = 0;  设置free的值
        RedisMemory.putInt(s.getAddress() + SdsHdr.getFreeOffset(), s.getFree() + s.getLen());

        //设置len的值
        RedisMemory.putInt(s.getAddress() + SdsHdr.getLenOffset(), 0);
    }

    public static SdsHdr sdsMakeRoomFor(SdsHdr s, int addLen) {
        if (s == null)
            return null;

        // s 目前的空余空间已经足够，无须再进行扩展，直接返回
        if (s.getFree() >= addLen)
            return s;

        // 获取 s 目前已占用空间的长度
        int len = s.getLen();
        // s 最少需要的长度
        int newLen = len + addLen;
        int headerLen = RedisMemory.INT_INDEX_SCALE * 2;
        int size = s.getSize();

        // 根据新长度，为 s 分配新空间所需的大小
        if (newLen < RedisConstant.SDS_MAX_PREALLOC) {
            // 如果新长度小于 SDS_MAX_PREALLOC，那么为它分配两倍于所需长度的空间
            newLen *= 2;
        } else {
            // 否则，分配长度为目前长度加上 SDS_MAX_PREALLOC
            newLen += RedisConstant.SDS_MAX_PREALLOC;
        }

        long address = RedisMemory.alloc(headerLen + newLen);

        // 内存不足，分配失败，返回
        if (address == 0)
            return null;

        // 分配成功后拷贝原内容到新地址空间并回收原地址空间
        UnsafeUtil.copyMemory(null, s.getAddress(), null, address, size);
        RedisMemory.free(s.getAddress(), size);
        // 更新 sds 的空余长度 free
        RedisMemory.putInt(address + SdsHdr.getFreeOffset(), newLen - len);

        SdsHdr sdsHdr = new SdsHdr();
        sdsHdr.setAddress(address);
        return sdsHdr;
    }
}
