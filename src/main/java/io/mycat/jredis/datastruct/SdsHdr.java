package io.mycat.jredis.datastruct;

import io.mycat.jredis.constant.RedisConstant;
import io.mycat.jredis.memory.RedisMemory;
import io.mycat.jredis.util.UnsafeUtil;

/**
 * Desc: 保存字符串对象的结构
 * 存储关系： free - len - buf
 *
 * @date: 19/07/2017
 * @author: gaozhiwen
 */
public class SdsHdr {
    private int len;// buf 中已占用空间的长度
    private int free;// buf 中剩余可用空间的长度
    private char[] buf;// 数据空间

    private long offset;

    private int getMemorySize() {
        return RedisMemory.INT_INDEX_SCALE * 2 + RedisMemory.CHAR_INDEX_SCALE * (len + free);
    }

    public int sdsLen() {
        return len;
    }

    public int sdsAvail() {
        return free;
    }

    /**
     * 根据给定的初始化字符串 init 和字符串长度 initlen
     * 创建一个新的 sds
     *
     * @param init    初始化字符数组
     * @param initLen 初始化字符串的长度
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static char[] sdsNewLen(char[] init, int initLen) {
        // 根据是否有初始化内容，选择适当的内存分配方式
        //        if (init != null) {
        //            // zmalloc 不初始化所分配的内存 todo
        //            sh = zmalloc(sizeof(struct sdshdr) + initLen);
        //        } else {
        //            // zcalloc 将分配的内存全部初始化为 0 todo
        //            sh = zcalloc(sizeof(struct sdshdr) + initLen);
        //        }
        long add = RedisMemory.alloc(RedisMemory.INT_INDEX_SCALE * 2 + initLen);

        // 内存分配失败，返回
        if (add == -1) {
            return null;
        }

        // 设置初始化长度
        //        sh.len = initLen;
        UnsafeUtil.getUnsafe().putInt(add, initLen);
        // 新 sds 不预留任何空间
        //        sh.free = 0;
        UnsafeUtil.getUnsafe().putInt(add + RedisMemory.INT_INDEX_SCALE, 0);

        // 如果有指定初始化内容，将它们复制到 sdshdr 的 buf 中
        if (initLen != 0 && init != null) {
            //            memcpy(sh.buf, init, initLen);
            RedisMemory.putChars(add + RedisMemory.INT_INDEX_SCALE * 2, init);
        }
        // 以 \0 结尾
        //        sh.buf = new char[initLen];

        // 返回 buf 部分，而不是整个 sdshdr
        return RedisMemory.getChars(add + RedisMemory.INT_INDEX_SCALE * 2, initLen);
    }

    /**
     * 创建并返回一个只保存了空字符串 "" 的 sds
     *
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static char[] sdsEmpty() {
        return sdsNewLen(null, 0);
    }

    /**
     * 根据给定字符串 init ，创建一个包含同样字符串的 sds
     *
     * @param init 如果输入为 NULL ，那么创建一个空白 sds，否则，新创建的 sds 中包含和 init 内容相同字符串
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static char[] sdsNew(final char[] init) {
        int initLen = (init == null) ? 0 : init.length;
        return sdsNewLen(init, initLen);
    }

    /**
     * 复制给定 sds 的副本
     *
     * @param s
     * @return 创建成功返回输入 sds 的副本，创建失败返回 NULL
     */
    public static char[] sdsDup(final SdsHdr s) {
        return sdsNewLen(s.buf, (s == null) ? 0 : s.len);
    }

    /**
     * 释放给定的 sds
     *
     * @param s
     */
    public static void sdsFree(SdsHdr s) {
        if (s == null)
            return;
        //        zfree(s - sizeof(struct sdshdr));
        RedisMemory.free(s.offset, s.getMemorySize());
    }

    /**
     * 在不释放 SDS 的字符串空间的情况下，
     * 重置 SDS 所保存的字符串为空字符串。
     *
     * @param s
     */
    public static void sdsClear(SdsHdr s) {
        // 取出 sdshdr
        //        struct sdshdr*sh = (void*)(s - (sizeof(struct sdshdr)));

        // 重新计算属性
        //        sh -> free += sh -> len;
        //        sh -> len = 0;
        RedisMemory.putInt(s.offset, s.free + s.len);//设置free的值
        RedisMemory.putInt(s.offset + RedisMemory.INT_INDEX_SCALE, 0);//设置len的值

        // 将结束符放到最前面（相当于惰性地删除 buf 中的内容）
        //        sh -> buf[0] = '\0';
    }

    public static char[] sdsMakeRoomFor(SdsHdr s, int addLen) {
        //        struct sdshdr*sh,*newsh;
        // 获取 s 目前的空余空间长度
        //        size_t free = sdsavail(s);
        //        size_t len, newlen;
        int free = s.sdsAvail();
        int len, newLen;

        // s 目前的空余空间已经足够，无须再进行扩展，直接返回
        if (free >= addLen) {
            return s.buf;
        }

        // 获取 s 目前已占用空间的长度
        len = s.sdsLen();
        //        sh = (void*)(s - (sizeof(struct sdshdr)));

        // s 最少需要的长度
        newLen = (len + addLen);

        // 根据新长度，为 s 分配新空间所需的大小
        if (newLen < RedisConstant.SDS_MAX_PREALLOC) {
            // 如果新长度小于 SDS_MAX_PREALLOC
            // 那么为它分配两倍于所需长度的空间
            newLen *= 2;
        } else {
            // 否则，分配长度为目前长度加上 SDS_MAX_PREALLOC
            newLen += RedisConstant.SDS_MAX_PREALLOC;
        }
        
        //        newsh = zrealloc(sh, sizeof(struct sdshdr) + newlen + 1);
        long newOffset = RedisMemory.alloc(s.getMemorySize() + newLen);

        // 内存不足，分配失败，返回
        if (newOffset == -1) {
            return null;
        }

        //分配成功后拷贝原内容到新地址空间并回收原地址空间
        UnsafeUtil.getUnsafe().copyMemory(null, s.getMemorySize(), null, newOffset, s.len);
        RedisMemory.free(s.offset, s.getMemorySize());

        // 更新 sds 的空余长度
        //        newsh -> free = newlen - len;
        RedisMemory.putInt(newOffset, newLen - len);

        return RedisMemory.getChars(RedisMemory.INT_INDEX_SCALE * 2, newLen);
    }
}
