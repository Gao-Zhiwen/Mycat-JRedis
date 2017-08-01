//package io.mycat.jredis.database.obj;
//
//import io.mycat.jredis.database.bean.RedisClient;
//import io.mycat.jredis.datastruct.UnsafeObject;
//import io.mycat.jredis.datastruct.redis.UnsafeSds;
//import io.mycat.jredis.datastruct.redis.constant.RedisConstant;
//
///**
// * Desc:Redis 对象
// *
// * @date: 25/07/2017
// * @author: gaozhiwen
// */
//public class RedisObject {
//    // 类型
//    private int type;
//    // 编码
//    private int encoding;
//    // 对象最后一次被访问的时间
//    private long lru;
//    // 引用计数
//    private int refcount;
//    // 指向实际值的指针
//    private UnsafeObject ptr;
//
//    /*
//     * 创建一个新 RedisObject 对象
//     */
//    public RedisObject createObject(int type, UnsafeObject ptr) {
//
//        RedisObject o = null;//todo zmalloc(sizeof(*o));
//
//        o.type = type;
//        o.encoding = RedisConstant.REDIS_ENCODING_RAW;
//        o.ptr = ptr;
//        o.refcount = 1;
//
//        o.lru = LRU_CLOCK();
//        return o;
//    }
//
//    // 创建一个 REDIS_ENCODING_RAW 编码的字符对象
//    // 对象的指针指向一个 sds 结构
//    public RedisObject createRawStringObject(UnsafeObject ptr, int len) {
//        return createObject(RedisConstant.REDIS_STRING, UnsafeSds.sdsNewLen(ptr, len));
//    }
//
//    // 创建一个 REDIS_ENCODING_EMBSTR 编码的字符对象
//    // 这个字符串对象中的 sds 会和字符串对象的 redisObject 结构一起分配
//    // 因此这个字符也是不可修改的
//    public RedisObject createEmbeddedStringObject(char[] ptr, int len) {
//        RedisObject o = null;// zmalloc(sizeof(RedisObject) + sizeof(struct sdshdr) + len + 1);
//        UnsafeSds sh = (void*)(o + 1);
//
//        o.type = RedisConstant.REDIS_STRING;
//        o.encoding = RedisConstant.REDIS_ENCODING_EMBSTR;
//        o.ptr = sh + 1;
//        o.refcount = 1;
//        o.lru = LRU_CLOCK();
//
//        sh -> len = len;
//        sh -> free = 0;
//        if (ptr) {
//            memcpy(sh -> buf, ptr, len);
//            sh -> buf[len] = '\0';
//        } else {
//            memset(sh -> buf, 0, len + 1);
//        }
//        return o;
//    }
//
//    public RedisObject createStringObject(char[] ptr, int len) {
//        if (len <= RedisConstant.REDIS_ENCODING_EMBSTR_SIZE_LIMIT)
//            return createEmbeddedStringObject(ptr, len);
//        else
//            return createRawStringObject(ptr, len);
//    }
//
//    /*
//     * 根据传入的整数值，创建一个字符串对象
//     *
//     * 这个字符串的对象保存的可以是 INT 编码的 long 值，
//     * 也可以是 RAW 编码的、被转换成字符串的 long long 值。
//     */
//    public RedisObject createStringObjectFromLong(long value) {
//
//        RedisObject o;
//
//        // value 的大小符合 REDIS 共享整数的范围
//        // 那么返回一个共享对象
//        if (value >= 0 && value < REDIS_SHARED_INTEGERS) {
//            incrRefCount(shared.integers[value]);
//            o = shared.integers[value];
//
//            // 不符合共享范围，创建一个新的整数对象
//        } else {
//            // 值可以用 long 类型保存，
//            // 创建一个 REDIS_ENCODING_INT 编码的字符串对象
//            if (value >= LONG_MIN && value <= LONG_MAX) {
//                o = createObject(REDIS_STRING, null);
//                o -> encoding = REDIS_ENCODING_INT;
//                o -> ptr = (void*)((long) value);
//
//                // 值不能用 long 类型保存（long long 类型），将值转换为字符串，
//                // 并创建一个 REDIS_ENCODING_RAW 的字符串对象来保存值
//            } else {
//                o = createObject(REDIS_STRING, sdsfromlonglong(value));
//            }
//        }
//
//        return o;
//    }
//
//    /*
//     * 根据传入的 long double 值，为它创建一个字符串对象
//     *
//     * 对象将 long double 转换为字符串来保存
//     */
//    public RedisObject createStringObjectFromDouble(double value) {
//        char buf[ 256];
//        int len;
//
//        // 使用 17 位小数精度，这种精度可以在大部分机器上被 rounding 而不改变
//        len = snprintf(buf, sizeof(buf), "%.17Lf", value);
//
//        // 移除尾部的 0
//        // 比如 3.1400000 将变成 3.14
//        // 而 3.00000 将变成 3
//        if (strchr(buf, '.') != null) {
//            char p = buf + len - 1;
//            while (p == '0') {
//                p--;
//                len--;
//            }
//            // 如果不需要小数点，那么移除它
//            if (p == '.')
//                len--;
//        }
//
//        // 创建对象
//        return createStringObject(buf, len);
//    }
//
//    /**
//     * 复制一个字符串对象，复制出的对象和输入对象拥有相同编码。
//     * 这个函数在复制一个包含整数值的字符串对象时，总是产生一个非共享的对象。
//     * 输出对象的 refcount 总为 1 。
//     */
//    public RedisObject dupStringObject(RedisObject o) {
//        RedisObject d;
//
//        redisAssert(o -> type == RedisConstant.REDIS_STRING);
//
//        switch (o.encoding) {
//            case RedisConstant.REDIS_ENCODING_RAW:
//                return createRawStringObject(o -> ptr, sdslen(o -> ptr));
//            case RedisConstant.REDIS_ENCODING_EMBSTR:
//                return createEmbeddedStringObject(o -> ptr, sdslen(o -> ptr));
//            case RedisConstant.REDIS_ENCODING_INT:
//                d = createObject(RedisConstant.REDIS_STRING, null);
//                d -> encoding = RedisConstant.REDIS_ENCODING_INT;
//                d -> ptr = o -> ptr;
//                return d;
//            default:
//                redisPanic("Wrong encoding.");
//                break;
//        }
//    }
//
//    /*
//     * 创建一个 LINKEDLIST 编码的列表对象
//     */
//    public RedisObject createListObject() {
//        list l = listCreate();
//
//        RedisObject o = createObject(RedisConstant.REDIS_LIST, l);
//
//        listSetFreeMethod(l, decrRefCountVoid);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_LINKEDLIST;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 ZIPLIST 编码的列表对象
//     */
//    public RedisObject createZiplistObject() {
//
//        char zl = ziplistNew();
//
//        RedisObject o = createObject(RedisConstant.REDIS_LIST, zl);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_ZIPLIST;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 SET 编码的集合对象
//     */
//    public RedisObject createSetObject() {
//
//        dict d = dictCreate(setDictType, null);
//
//        RedisObject o = createObject(RedisConstant.REDIS_SET, d);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_HT;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 INTSET 编码的集合对象
//     */
//    public RedisObject createIntsetObject() {
//
//        intset is = intsetNew();
//
//        RedisObject o = createObject(RedisConstant.REDIS_SET, is);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_INTSET;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 ZIPLIST 编码的哈希对象
//     */
//    public RedisObject createHashObject() {
//
//        char zl = ziplistNew();
//
//        RedisObject o = createObject(RedisConstant.REDIS_HASH, zl);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_ZIPLIST;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 SKIPLIST 编码的有序集合
//     */
//    public RedisObject createZsetObject() {
//
//        zset zs = zmalloc(sizeof( * zs));
//
//        RedisObject o;
//
//        zs -> dict = dictCreate(zsetDictType, null);
//        zs -> zsl = zslCreate();
//
//        o = createObject(RedisConstant.REDIS_ZSET, zs);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_SKIPLIST;
//
//        return o;
//    }
//
//    /*
//     * 创建一个 ZIPLIST 编码的有序集合
//     */
//    public RedisObject createZsetZiplistObject() {
//
//        char zl = ziplistNew();
//
//        RedisObject o = createObject(RedisConstant.REDIS_ZSET, zl);
//
//        o -> encoding = RedisConstant.REDIS_ENCODING_ZIPLIST;
//
//        return o;
//    }
//
//    /*
//     * 释放字符串对象
//     */
//    public void freeStringObject(RedisObject o) {
//        if (o -> encoding == RedisConstant.REDIS_ENCODING_RAW) {
//            sdsfree(o -> ptr);
//        }
//    }
//
//    /*
//     * 释放列表对象
//     */
//    public void freeListObject(RedisObject o) {
//
//        switch (o.encoding) {
//
//            case RedisConstant.REDIS_ENCODING_LINKEDLIST:
//                listRelease((list *)o -> ptr); break;
//
//            case RedisConstant.REDIS_ENCODING_ZIPLIST:
//                zfree(o -> ptr);
//                break;
//
//            default:
//                redisPanic("Unknown list encoding type");
//        }
//    }
//
//    /*
//     * 释放集合对象
//     */
//    public void freeSetObject(RedisObject o) {
//
//        switch (o.encoding) {
//
//            case RedisConstant.REDIS_ENCODING_HT:
//                dictRelease((dict *)o -> ptr); break;
//
//            case RedisConstant.REDIS_ENCODING_INTSET:
//                zfree(o -> ptr);
//                break;
//
//            default:
//                redisPanic("Unknown set encoding type");
//        }
//    }
//
//    /*
//     * 释放有序集合对象
//     */
//    public void freeZsetObject(RedisObject o) {
//
//        zset zs;
//
//        switch (o.encoding) {
//
//            case RedisConstant.REDIS_ENCODING_SKIPLIST:
//                zs = o -> ptr;
//                dictRelease(zs -> dict);
//                zslFree(zs -> zsl);
//                zfree(zs);
//                break;
//
//            case RedisConstant.REDIS_ENCODING_ZIPLIST:
//                zfree(o -> ptr);
//                break;
//
//            default:
//                redisPanic("Unknown sorted set encoding");
//        }
//    }
//
//    /*
//     * 释放哈希对象
//     */
//    public void freeHashObject(RedisObject o) {
//
//        switch (o.encoding) {
//
//            case RedisConstant.REDIS_ENCODING_HT:
//                dictRelease((dict *)o -> ptr); break;
//
//            case RedisConstant.REDIS_ENCODING_ZIPLIST:
//                zfree(o -> ptr);
//                break;
//
//            default:
//                redisPanic("Unknown hash encoding type");
//                break;
//        }
//    }
//
//    /*
//     * 为对象的引用计数增一
//     */
//    public void incrRefCount(RedisObject o) {
//        o -> refcount++;
//    }
//
//    /*
//     * 为对象的引用计数减一
//     *
//     * 当对象的引用计数降为 0 时，释放对象。
//     */
//    public void decrRefCount(RedisObject o) {
//
//        if (o -> refcount <= 0)
//            redisPanic("decrRefCount against refcount <= 0");
//
//        // 释放对象
//        if (o -> refcount == 1) {
//            switch (o.type) {
//                case RedisConstant.REDIS_STRING:
//                    freeStringObject(o);
//                    break;
//                case RedisConstant.REDIS_LIST:
//                    freeListObject(o);
//                    break;
//                case RedisConstant.REDIS_SET:
//                    freeSetObject(o);
//                    break;
//                case RedisConstant.REDIS_ZSET:
//                    freeZsetObject(o);
//                    break;
//                case RedisConstant.REDIS_HASH:
//                    freeHashObject(o);
//                    break;
//                default:
//                    redisPanic("Unknown object type");
//                    break;
//            }
//            zfree(o);
//
//            // 减少计数
//        } else {
//            o -> refcount--;
//        }
//    }
//
//    /*
//     * 作用于特定数据结构的释放函数包装
//     */
//    public void decrRefCountVoid(Object o) {
//        decrRefCount(o);
//    }
//
//    /*
//     * 这个函数将对象的引用计数设为 0 ，但并不释放对象。
//     * 这个函数在将一个对象传入一个会增加引用计数的函数中时，非常有用。
//     * 就像这样：
//     * 没有这个函数的话，事情就会比较麻烦了：
//     *    *obj = createObject(...);
//     *    functionThatWillIncrementRefCount(obj);
//     *    decrRefCount(obj);
//     */
//    public RedisObject resetRefCount(RedisObject obj) {
//        obj -> refcount = 0;
//        return obj;
//    }
//
//    /*
//     * 检查对象 o 的类型是否和 type 相同：
//     *
//     *  - 相同返回 0
//     *
//     *  - 不相同返回 1 ，并向客户端回复一个错误
//     */
//    public int checkType(RedisClient c, RedisObject o, int type) {
//        if (o -> type != type) {
//            addReply(c, shared.wrongtypeerr);
//            return 1;
//        }
//
//        return 0;
//    }
//
//    /*
//     * 检查对象 o 中的值能否表示为 long long 类型：
//     *  - 可以则返回 REDIS_OK ，并将 long long 值保存到 *llval 中。
//     *  - 不可以则返回 REDIS_ERR
//     */
//    public int isObjectRepresentableAsLong(RedisObject o, long llval) {
//
//        redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//
//        // INT 编码的 long 值总是能保存为 long long
//        if (o -> encoding == RedisConstant.REDIS_ENCODING_INT) {
//            if (llval != 0)
//                llval = (long) o -> ptr;
//            return RedisConstant.REDIS_OK;
//
//            // 如果是字符串的话，那么尝试将它转换为 long long
//        } else {
//            return string2ll(o -> ptr, sdslen(o -> ptr), llval) ?
//                    RedisConstant.REDIS_OK :
//                    RedisConstant.REDIS_ERR;
//        }
//    }
//
//    // 尝试对字符串对象进行编码，以节约内存。
//    public RedisObject tryObjectEncoding(RedisObject o) {
//        long value;
//
//        sds s = o -> ptr;
//        int len;
//
//        redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//
//        // 只在字符串的编码为 RAW 或者 EMBSTR 时尝试进行编码
//        if (!sdsEncodedObject(o))
//            return o;
//
//        // 不对共享对象进行编码
//        if (o -> refcount > 1)
//            return o;
//
//        // 对字符串进行检查
//        // 只对长度小于或等于 21 字节，并且可以被解释为整数的字符串进行编码
//        len = sdslen(s);
//        if (len <= 21 && string2l(s, len, value)) {
//            if (server.maxmemory == 0 &&
//                    value >= 0 &&
//                    value < RedisConstant.REDIS_SHARED_INTEGERS) {
//                decrRefCount(o);
//                incrRefCount(shared.integers[value]);
//                return shared.integers[value];
//            } else {
//                if (o -> encoding == RedisConstant.REDIS_ENCODING_RAW)
//                    sdsfree(o -> ptr);
//                o -> encoding = RedisConstant.REDIS_ENCODING_INT;
//                o -> ptr = value;
//                return o;
//            }
//        }
//
//        // 尝试将 RAW 编码的字符串编码为 EMBSTR 编码
//        if (len <= RedisConstant.REDIS_ENCODING_EMBSTR_SIZE_LIMIT) {
//            RedisObject emb;
//
//            if (o -> encoding == RedisConstant.REDIS_ENCODING_EMBSTR)
//                return o;
//            emb = createEmbeddedStringObject(s, sdslen(s));
//            decrRefCount(o);
//            return emb;
//        }
//
//        // 这个对象没办法进行编码，尝试从 SDS 中移除所有空余空间
//        if (o -> encoding == RedisConstant.REDIS_ENCODING_RAW && sdsavail(s) > len / 10) {
//            o -> ptr = sdsRemoveFreeSpace(o -> ptr);
//        }
//
//        return o;
//    }
//
//    /*
//     * 以新对象的形式，返回一个输入对象的解码版本（RAW 编码）。
//     * 如果对象已经是 RAW 编码的，那么对输入对象的引用计数增一，
//     * 然后返回输入对象。
//     */
//    public RedisObject getDecodedObject(RedisObject o) {
//        RedisObject dec;
//
//        if (sdsEncodedObject(o)) {
//            incrRefCount(o);
//            return o;
//        }
//
//        // 解码对象，将对象的值从整数转换为字符串
//        if (o -> type == RedisConstant.REDIS_STRING && o -> encoding
//                == RedisConstant.REDIS_ENCODING_INT) {
//            char buf[ 32];
//
//            ll2string(buf, 32, (long) o -> ptr);
//            dec = createStringObject(buf, strlen(buf));
//            return dec;
//
//        } else {
//            redisPanic("Unknown encoding type");
//        }
//    }
//
//    /*
//     * 根据 flags 的值，决定是使用 strcmp() 或者 strcoll() 来对比字符串对象。
//     * 注意，因为字符串对象可能实际上保存的是整数值，
//     * 如果出现这种情况，那么函数先将整数转换为字符串，
//     * 然后再对比两个字符串，
//     * 这种做法比调用 getDecodedObject() 更快
//     * 当 flags 为 REDIS_COMPARE_BINARY 时，
//     * 对比以二进制安全的方式进行。
//     */
//    public int compareStringObjectsWithFlags(RedisObject a, RedisObject b, int flags) {
//        redisAssertWithInfo(null, a,
//                a -> type == RedisConstant.REDIS_STRING && b -> type == RedisConstant.REDIS_STRING);
//
//        char bufa[ 128],bufb[128], astr, bstr;
//        int alen, blen, minlen;
//
//        if (a == b)
//            return 0;
//
//        // 指向字符串值，并在有需要时，将整数转换为字符串 a
//        if (sdsEncodedObject(a)) {
//            astr = a -> ptr;
//            alen = sdslen(astr);
//        } else {
//            alen = ll2string(bufa, sizeof(bufa), (long) a -> ptr);
//            astr = bufa;
//        }
//
//        // 同样处理字符串 b
//        if (sdsEncodedObject(b)) {
//            bstr = b -> ptr;
//            blen = sdslen(bstr);
//        } else {
//            blen = ll2string(bufb, sizeof(bufb), (long) b -> ptr);
//            bstr = bufb;
//        }
//
//
//        // 对比
//        if (flags & RedisConstant.REDIS_COMPARE_COLL) {
//            return strcoll(astr, bstr);
//        } else {
//            int cmp;
//
//            minlen = (alen < blen) ? alen : blen;
//            cmp = memcmp(astr, bstr, minlen);
//            if (cmp == 0)
//                return alen - blen;
//            return cmp;
//        }
//    }
//
//    public int compareStringObjects(RedisObject a, RedisObject b) {
//        return compareStringObjectsWithFlags(a, b, RedisConstant.REDIS_COMPARE_BINARY);
//    }
//
//    public int collateStringObjects(RedisObject a, RedisObject b) {
//        return compareStringObjectsWithFlags(a, b, RedisConstant.REDIS_COMPARE_COLL);
//    }
//
//    /*
//     * 如果两个对象的值在字符串的形式上相等，那么返回 1 ， 否则返回 0 。
//     * 这个函数做了相应的优化，所以比 (compareStringObject(a, b) == 0) 更快一些。
//     */
//    int equalStringObjects(RedisObject a, RedisObject b) {
//        // 对象的编码为 INT ，直接对比值
//        // 这里避免了将整数值转换为字符串，所以效率更高
//        if (a -> encoding == RedisConstant.REDIS_ENCODING_INT && b -> encoding
//                == RedisConstant.REDIS_ENCODING_INT) {
//            return a -> ptr == b -> ptr;
//
//            // 进行字符串对象
//        } else {
//            return compareStringObjects(a, b) == 0;
//        }
//    }
//
//    /*
//     * 返回字符串对象中字符串值的长度
//     */
//    public int stringObjectLen(RedisObject o) {
//
//        redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//
//        if (sdsEncodedObject(o)) {
//            return sdslen(o -> ptr);
//
//            // INT 编码，计算将这个值转换为字符串要多少字节
//            // 相当于返回它的长度
//        } else {
//            char buf[ 32];
//            return ll2string(buf, 32, (long) o -> ptr);
//        }
//    }
//
//    /*
//     * 尝试从对象中取出 double 值
//     *  - 转换成功则将值保存在 *target 中，函数返回 REDIS_OK
//     *  - 否则，函数返回 REDIS_ERR
//     */
//    public int getDoubleFromObject(RedisObject o, double target) {
//        double value;
//        char eptr;
//
//        if (o == null) {
//            value = 0;
//
//        } else {
//            redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//
//            // 尝试从字符串中转换 double 值
//            if (sdsEncodedObject(o)) {
//                errno = 0;
//                value = strtod(o -> ptr, eptr);
//                if (isspace((o -> ptr)[0]) ||
//                        eptr[0] != '\0' ||
//                        (errno == ERANGE && (value == HUGE_VAL || value == -HUGE_VAL || value == 0))
//                        ||
//                        errno == EINVAL ||
//                        isnan(value))
//                    return RedisConstant.REDIS_ERR;
//
//                // INT 编码
//            } else if (o -> encoding == RedisConstant.REDIS_ENCODING_INT) {
//                value = (long) o -> ptr;
//
//            } else {
//                redisPanic("Unknown string encoding");
//            }
//        }
//
//        // 返回值
//        target = value;
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象 o 中取出 double 值：
//     *  - 如果尝试失败的话，就返回指定的回复 msg 给客户端，函数返回 REDIS_ERR 。
//     *  - 取出成功的话，将值保存在 *target 中，函数返回 REDIS_OK 。
//     */
//    public int getDoubleFromObjectOrReply(RedisClient c, RedisObject o, double target,
//            final char msg) {
//
//        double value;
//
//        if (getDoubleFromObject(o, & value)!=RedisConstant.REDIS_OK){
//            if (msg != null) {
//                addReplyError(c, (char*)msg);
//            } else {
//                addReplyError(c, "value is not a valid float");
//            } return RedisConstant.REDIS_ERR;
//        }
//
//        target = value;
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象中取出 long double 值
//     *  - 转换成功则将值保存在 *target 中，函数返回 REDIS_OK
//     *  - 否则，函数返回 REDIS_ERR
//     */
//    int getLongDoubleFromObject(RedisObject o, double target) {
//        double value;
//        char eptr;
//
//        if (o == null) {
//            value = 0;
//        } else {
//
//            redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//
//            // RAW 编码，尝试从字符串中转换 long double
//            if (sdsEncodedObject(o)) {
//                errno = 0;
//                value = strtold(o -> ptr, & eptr);
//                if (isspace(((char*)o -> ptr)[0])||eptr[0] != '\0' ||
//                        errno == ERANGE || isnan(value))
//                return RedisConstant.REDIS_ERR;
//
//                // INT 编码，直接保存
//            } else if (o -> encoding == RedisConstant.REDIS_ENCODING_INT) {
//                value = (long) o -> ptr;
//
//            } else {
//                redisPanic("Unknown string encoding");
//            }
//        }
//
//        target = value;
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象 o 中取出 long double 值：
//     *  - 如果尝试失败的话，就返回指定的回复 msg 给客户端，函数返回 REDIS_ERR 。
//     *  - 取出成功的话，将值保存在 *target 中，函数返回 REDIS_OK 。
//     */
//    public int getLongDoubleFromObjectOrReply(RedisClient c, RedisObject o, double target,
//            final char msg) {
//
//        double value;
//
//        if (getLongDoubleFromObject(o, & value)!=RedisConstant.REDIS_OK){
//            if (msg != null) {
//                addReplyError(c, (char*)msg);
//            } else {
//                addReplyError(c, "value is not a valid float");
//            } return RedisConstant.REDIS_ERR;
//        }
//
//        *target = value;
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象 o 中取出整数值，
//     * 或者尝试将对象 o 所保存的值转换为整数值，
//     * 并将这个整数值保存到 *target 中。
//     *
//     * 如果 o 为 null ，那么将 *target 设为 0 。
//     *
//     * 如果对象 o 中的值不是整数，并且不能转换为整数，那么函数返回 REDIS_ERR 。
//     *
//     * 成功取出或者成功进行转换时，返回 REDIS_OK 。
//     */
//    public int getLongLongFromObject(RedisObject o, long target) {
//        long value;
//        char eptr;
//
//        if (o == null) {
//            // o 为 null 时，将值设为 0 。
//            value = 0;
//        } else {
//
//            // 确保对象为 REDIS_STRING 类型
//            redisAssertWithInfo(null, o, o -> type == RedisConstant.REDIS_STRING);
//            if (sdsEncodedObject(o)) {
//                errno = 0;
//                // T = O(N)
//                value = strtoll(o -> ptr, & eptr, 10);
//                if (isspace(((char*)o -> ptr)[0])||eptr[0] != '\0' || errno == ERANGE)
//                return RedisConstant.REDIS_ERR;
//            } else if (o -> encoding == RedisConstant.REDIS_ENCODING_INT) {
//                // 对于 REDIS_ENCODING_INT 编码的整数值
//                // 直接将它的值保存到 value 中
//                value = (long) o -> ptr;
//            } else {
//                redisPanic("Unknown string encoding");
//            }
//        }
//
//        // 保存值到指针
//        if (target)*target = value;
//
//        // 返回结果标识符
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象 o 中取出整数值，
//     * 或者尝试将对象 o 中的值转换为整数值，
//     * 并将这个得出的整数值保存到 *target 。
//     *
//     * 如果取出/转换成功的话，返回 REDIS_OK 。
//     * 否则，返回 REDIS_ERR ，并向客户端发送一条出错回复。
//     */
//    public int getLongLongFromObjectOrReply(RedisClient c, RedisObject o, long target,
//            final char msg) {
//
//        long value;
//
//        if (getLongLongFromObject(o, & value)!=RedisConstant.REDIS_OK){
//            if (msg != null) {
//                addReplyError(c, msg);
//            } else {
//                addReplyError(c, "value is not an integer or out of range");
//            }
//            return RedisConstant.REDIS_ERR;
//        }
//
//        target = value;
//
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 尝试从对象 o 中取出 long 类型值，
//     * 或者尝试将对象 o 中的值转换为 long 类型值，
//     * 并将这个得出的整数值保存到 *target 。
//     *
//     * 如果取出/转换成功的话，返回 REDIS_OK 。
//     * 否则，返回 REDIS_ERR ，并向客户端发送一条 msg 出错回复。
//     */
//    public int getLongFromObjectOrReply(RedisClient c, RedisObject o, long target,
//            final char[] msg) {
//        long value;
//
//        // 先尝试以 long long 类型取出值
//        if (getLongLongFromObjectOrReply(c, o, value, msg) != RedisConstant.REDIS_OK)
//            return RedisConstant.REDIS_ERR;
//
//        // 然后检查值是否在 long 类型的范围之内
//        if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
//            if (msg != null) {
//                addReplyError(c, msg);
//            } else {
//                addReplyError(c, "value is out of range");
//            }
//            return RedisConstant.REDIS_ERR;
//        }
//
//        *target = value;
//        return RedisConstant.REDIS_OK;
//    }
//
//    /*
//     * 返回编码的字符串表示
//     */
//    public String strEncoding(int encoding) {
//        switch (encoding) {
//            case RedisConstant.REDIS_ENCODING_RAW:
//                return "raw";
//            case RedisConstant.REDIS_ENCODING_INT:
//                return "int";
//            case RedisConstant.REDIS_ENCODING_HT:
//                return "hashtable";
//            case RedisConstant.REDIS_ENCODING_LINKEDLIST:
//                return "linkedlist";
//            case RedisConstant.REDIS_ENCODING_ZIPLIST:
//                return "ziplist";
//            case RedisConstant.REDIS_ENCODING_INTSET:
//                return "intset";
//            case RedisConstant.REDIS_ENCODING_SKIPLIST:
//                return "skiplist";
//            case RedisConstant.REDIS_ENCODING_EMBSTR:
//                return "embstr";
//            default:
//                return "unknown";
//        }
//    }
//
//    // 使用近似 LRU 算法，计算出给定对象的闲置时长
//    public long estimateObjectIdleTime(RedisObject o) {
//        long lruclock = LRU_CLOCK();
//        if (lruclock >= o -> lru) {
//            return (lruclock-o -> lru)REDIS_LRU_CLOCK_RESOLUTION;
//        } else {
//            return (lruclock + (REDIS_LRU_CLOCK_MAX-o -> lru))
//            REDIS_LRU_CLOCK_RESOLUTION;
//        }
//    }
//
//    /*
//     * OBJECT 命令的辅助函数，用于在不修改 LRU 时间的情况下，尝试获取 key 对象
//     */
//    public RedisObject objectCommandLookup(RedisClient c, RedisObject key) {
//        dictEntry de;
//
//        if ((de = dictFind(c -> db -> dict, key -> ptr)) == null)
//            return null;
//        return (RedisObject) dictGetVal(de);
//    }
//
//    /*
//     * 在不修改 LRU 时间的情况下，获取 key 对应的对象。
//     *
//     * 如果对象不存在，那么向客户端发送回复 reply 。
//     */
//    public RedisObject objectCommandLookupOrReply(RedisClient c, RedisObject key,
//            RedisObject reply) {
//        RedisObject o = objectCommandLookup(c, key);
//
//        if (o == null)
//            addReply(c, reply);
//        return o;
//    }
//
//    public void objectCommand(RedisClient c) {
//        RedisObject o;
//
//        // 返回对戏哪个的引用计数
//        if (!strcasecmp(c -> argv[1].ptr, "refcount") && c -> argc == 3) {
//            if ((o = objectCommandLookupOrReply(c, c -> argv[2], shared.nullbulk)) == null)
//                return;
//            addReplyLong(c, o -> refcount);
//            // 返回对象的编码
//        } else if (!strcasecmp(c -> argv[1].ptr, "encoding") && c -> argc == 3) {
//            if ((o = objectCommandLookupOrReply(c, c -> argv[2], shared.nullbulk)) == null)
//                return;
//            addReplyBulkCString(c, strEncoding(o.encoding));
//            // 返回对象的空闲时间
//        } else if (!strcasecmp(c -> argv[1]->ptr, "idletime")&&c -> argc == 3){
//            if ((o = objectCommandLookupOrReply(c, c -> argv[2], shared.nullbulk)) == null)
//                return;
//            addReplyLong(c, estimateObjectIdleTime(o) / 1000);
//        }else{
//            addReplyError(c, "Syntax error. Try OBJECT (refcount|encoding|idletime)");
//        }
//    }
//}
