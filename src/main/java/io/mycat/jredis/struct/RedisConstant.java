package io.mycat.jredis.struct;

/**
 * Desc:
 *
 * @date: 03/06/2017
 * @author: gaozhiwen
 */
public class RedisConstant {
    // 对象类型
    public static final int REDIS_STRING = 0;
    public static final int REDIS_LIST = 1;
    public static final int REDIS_SET = 2;
    public static final int REDIS_ZSET = 3;
    public static final int REDIS_HASH = 4;

    // 对象编码
    public static final int REDIS_ENCODING_RAW = 0;    /* Raw representation */
    public static final int REDIS_ENCODING_INT = 1;     /* Encoded as integer */
    public static final int REDIS_ENCODING_HT = 2;      /* Encoded as hash table */
    public static final int REDIS_ENCODING_ZIPMAP = 3;  /* Encoded as zipmap */
    public static final int REDIS_ENCODING_LINKEDLIST = 4; /* Encoded as regular linked list */
    public static final int REDIS_ENCODING_ZIPLIST = 5; /* Encoded as ziplist */
    public static final int REDIS_ENCODING_INTSET = 6;  /* Encoded as intset */
    public static final int REDIS_ENCODING_SKIPLIST = 7;  /* Encoded as skiplist */
    public static final int REDIS_ENCODING_EMBSTR = 8;
}
