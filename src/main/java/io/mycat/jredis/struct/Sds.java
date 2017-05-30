package io.mycat.jredis.struct;

/**
 * Desc:jredis的字符串类
 * <p/>Date: 28/05/2017
 * <br/>Time: 20:46
 * <br/>User: gaozhiwen
 */
public class Sds {
    public static final int SDS_MAX_PREALLOC = 1024 * 1024;

    private Sds() {
    }

    private SdsHdr sdsHdr;

    /*
     * 返回 sds 实际保存的字符串的长度
     */
    //    public int getLength() {
    //        return this.len;
    //    }

        /*
         * 返回 sds 可用空间的长度
         */
    //    public int getFree() {
    //        return this.free;
    //    }

    //    public String getBuf() {
    //        return this.buf;
    //    }

    /**
     * 根据给定的初始化字符串和字符串长度创建一个新的sds
     *
     * @param initStr
     * @param initLen
     * @return
     */

    public static Sds sdsNewLen(final String initStr, int initLen) {
        Sds sds = new Sds();
        //        sds.buf = initStr;
        //        sds.len = initLen;
        //        sds.free = (initStr == null) ? initLen : (initLen - initStr.length());
        return sds;
    }

    /**
     * 根据给定字符串 init ，创建一个包含同样字符串的 sds
     *
     * @param initStr 如果输入为NULL，那么创建一个空白sds，否则，新创建的sds中包含和init内容相同字符串
     * @return 创建成功返回sdshdr相对应的sds，否则返回NULL
     */
    public static Sds sdsNew(final String initStr) {
        int length = (initStr == null) ? 0 : initStr.length();
        return sdsNewLen(initStr, length);
    }

    public Sds sdsEmpty() {
        return sdsNewLen("", 0);
    }

    //    Sds sdsDup(Sds s) {
    //    }
    //
    //    void sdsfree(Sds s);
    //
    //    Sds sdsgrowzero(Sds s, int len);
    //
    //    Sds sdscatlen(Sds s, const void*t, int len);
    //
    //    Sds sdscat(Sds s, const char*t);
    //
    //    Sds sdscatsds(Sds s, const Sds t);
    //
    //    Sds sdscpylen(Sds s, const char*t, int len);
    //
    //    Sds sdscpy(Sds s, const char*t);
    //
    //    Sds sdscatvprintf(Sds s, const char*fmt, va_list ap);
    //
    //    Sds sdscatprintf(Sds s, const char*fmt, ...)
    //
    //    Sds sdscatprintf(Sds s, const char*fmt, ...);
    //
    //    Sds sdscatfmt(Sds s, char const*fmt, ...);
    //
    //    Sds sdstrim(Sds s, const char*cset);
    //
    //    void sdsrange(Sds s, int start, int end);
    //
    //    void sdsupdatelen(Sds s);
    //
    //    void sdsclear(Sds s);
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
    //    Sds sdsfromlonglong(long long value);
    //
    //    Sds sdscatrepr(Sds s, const char*p, size_t len);
    //
    //    sdssplitargs(const char*line, int*argc);
    //
    //    Sds sdsmapchars(sds s, const char*from, const char*to, size_t setlen);
    //
    //    Sds sdsjoin(char**argv, int argc, char*sep) {
    //    }
    //
    //    /* Low level functions exposed to the user API */
    //    Sds sdsMakeRoomFor(Sds s, int addlen) {
    //    }
    //
    //    void sdsIncrLen(Sds s, int incr) {
    //    }
    //
    //    Sds sdsRemoveFreeSpace(Sds s) {
    //    }
    //
    //    int sdsAllocSize(Sds s) {
    //        return 0;
    //    }
}
