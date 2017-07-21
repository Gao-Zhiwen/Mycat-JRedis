package io.mycat.jredis.constant;

/**
 * Desc:
 *
 * @date: 20/07/2017
 * @author: gaozhiwen
 */
public class RedisConstant {
    /* SDS */
    public static final int SDS_MAX_PREALLOC = 1024 * 1024;// SDS分配内存的边界值

    /* DICT */
    public static final int DICT_HT_INITIAL_SIZE = 4;// 哈希表的初始化大小
    public static final int DICT_OK = 0;// 操作成功
    public static final int DICT_ERR = 1;// 操作失败（或出错）
    public static final int DICT_CAN_RESIZE = 1;// 指示字典是否启用 rehash 的标识
    public static final int DICT_FORCE_RESIZE_RATIO = 5;// 强制 rehash 的比率
    public static final int DICT_HASH_FUNCTION_SEED = 5381;
}
