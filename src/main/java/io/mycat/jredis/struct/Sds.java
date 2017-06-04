package io.mycat.jredis.struct;

/**
 * Desc: jredis的字符串类
 *
 * @date: 01/06/2017
 * @author: gaozhiwen
 */
public class Sds {
    public static final int SDS_MAX_PREALLOC = 1024 * 1024; //1M

    private Sds() {
    }

    private int len;
    private int free;
    private char[] buf;

    /*
     * 返回 sds 实际保存的字符串的长度
     */
    public static int sdsLen(final Sds sds) {
        return sds.len;
    }

    /*
     * 返回 sds 可用空间的长度
     */
    public static int sdsAvail(final Sds sds) {
        return sds.free;
    }

    /**
     * 根据给定的初始化字符串和字符串长度创建一个新的sds
     *
     * @param init
     * @param initLen
     * @return
     */
    public static Sds sdsNewLen(final char[] init, int initLen) {
        if (initLen < 0) {
            return null;
        }
        Sds sds = new Sds();
        sds.buf = new char[initLen + 1];
        //初始化长度
        sds.len = initLen;
        //新sds不预留长度
        sds.free = 0;
        //如果有初始指定内容，将init的值复制到buf中
        if (initLen > 0 && (init != null && init.length > 0)) {
            System.arraycopy(init, 0, sds.buf, 0, initLen);
        }
        sds.buf[initLen] = '\0';
        return sds;
    }

    /**
     * 根据给定字符串 init ，创建一个包含同样字符串的 sds
     *
     * @param init 如果输入为NULL，那么创建一个空白sds，否则，新创建的sds中包含和init内容相同字符串
     * @return 创建成功返回sdshdr相对应的sds，否则返回NULL
     */
    public static Sds sdsNew(final char[] init) {
        int length = (init == null) ? 0 : init.length;
        return sdsNewLen(init, length);
    }

    /**
     * 创建并返回一个只保存了空字符串 "" 的 sds
     *
     * @return 创建成功返回 sdshdr 相对应的 sds，创建失败返回 NULL
     */
    public static Sds sdsEmpty() {
        return sdsNewLen(null, 0);
    }

    /**
     * 复制给定 sds 的副本
     *
     * @param sds
     * @return
     */
    public static Sds sdsDup(Sds sds) {
        return sdsNewLen(sds.buf, sdsLen(sds));
    }

    /**
     * 释放给定的 sds
     *
     * @param sds
     */
    public static void sdsFree(Sds sds) {
        if (sds == null) {
            return;
        }
        sds.buf = null;
        sds = null;
    }
    //
    //    Sds sdsGrowZero(Sds s, int len);
    //
    //    Sds sdsCatLen(Sds s, const void*t, int len);
    //
    //    Sds sdsCat(Sds s, const char*t);
    //
    //    Sds sdsCatSds(Sds s, const Sds t);
    //
    //    Sds sdsCpyLen(Sds s, const char*t, int len);
    //
    //    Sds sdsCpy(Sds s, const char*t);
    //
    //    Sds sdsCatvPrintf(Sds s, const char*fmt, va_list ap);
    //
    //    Sds sdsCatPrintf(Sds s, const char*fmt, ...)
    //
    //    Sds sdsCatPrintf(Sds s, const char*fmt, ...);
    //
    //    Sds sdsCatFmt(Sds s, char const*fmt, ...);
    //
    //    Sds sdsTrim(Sds s, const char*cset);
    //
    //    void sdsRange(Sds s, int start, int end);
    //

    /**
     * 在不释放 SDS 的字符串空间的情况下，
     * 重置 SDS 所保存的字符串为空字符串。
     */
    public static void sdsClear(Sds sds) {
        sds.free = sds.len;
        sds.len = 0;
        sds.buf[0] = '\0';
    }

    //
    //    int sdscmp(Sds s1, Sds s2);
    //
    //    sdssplitlen(const char*s, int len, const char*sep, int seplen, int*count);
    //
    //    void sdsfreesplitres(sds*tokens, int count);
    //
    //    void sdstolower(Sds s);
    //
    //    void sdstoupper(Sds s);
    //
    //    Sds sdsFromLongLong(long long value);
    //
    //    Sds sdsCatRepr(Sds s, const char*p, size_t len);
    //
    //    sdsSplitArgs(const char*line, int*argc);
    //
    //    Sds sdsMapChars(sds s, const char*from, const char*to, size_t setlen);
    //
    //    Sds sdsJoin(char**argv, int argc, char*sep) {
    //    }

    /**
     * 对 sds 中 buf 的长度进行扩展，确保在函数执行之后，buf 至少会有 addlen + 1 长度的空余空间
     * （额外的 1 字节是为 \0 准备的）
     *
     * @param sds
     * @param addLen
     * @return
     */
    public static Sds sdsMakeRoomFor(Sds sds, int addLen) {
        int free = sdsAvail(sds);
        // s 目前的空余空间已经足够，无须再进行扩展，直接返回
        if (free >= addLen) {
            return sds;
        }

        int len = sdsLen(sds);
        int newLen = len + addLen;

        // 根据新长度，为 s 分配新空间所需的大小
        if (newLen < SDS_MAX_PREALLOC) {
            // 如果新长度小于 SDS_MAX_PREALLOC，那么为它分配两倍于所需长度的空间
            newLen *= 2;
        } else {
            // 否则，分配长度为目前长度加上 SDS_MAX_PREALLOC
            newLen += SDS_MAX_PREALLOC;
        }

        Sds newSds = sdsNewLen(sds.buf, newLen + 1);

        // 更新 sds 的空余长度
        newSds.free = newLen - len;

        return newSds;
    }

    /**
     * 根据 incr 参数，增加 sds 的长度，缩减空余空间，并将 \0 放到新字符串的尾端
     * 这个函数是在调用 sdsMakeRoomFor() 对字符串进行扩展，然后用户在字符串尾部写入了某些内容之后，用来正确更新 free 和 len 属性的。
     *
     * @param sds
     * @param incr
     */
    void sdsIncrLen(Sds sds, int incr) {
        // 确保 sds 空间足够
        assert (sds.free >= incr);

        // 更新属性
        sds.len += incr;
        sds.free -= incr;

        // 这个 assert 其实可以忽略，因为前一个 assert 已经确保 sh->free - incr >= 0 了
        //        assert (sds.free >= 0);

        // 放置新的结尾符号
        sds.buf[sds.len] = '\0';
    }

    /**
     * 回收 sds 中的空闲空间，回收不会对 sds 中保存的字符串内容做任何修改。
     *
     * @param sds
     * @return
     */
    public static Sds sdsRemoveFreeSpace(Sds sds) {
        return sdsNewLen(sds.buf, sds.len + 1);
    }

    /**
     * 返回给定 sds 分配的内存字节数
     *
     * @param sds
     * @return
     */
    public static int sdsAllocSize(Sds sds) {
        return sds.len + sds.free + 1;//todo need check
    }
}
