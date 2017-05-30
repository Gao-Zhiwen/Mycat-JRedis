package io.mycat.jredis.client;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 14:55
 * <br/>User: gaozhiwen
 */
public class RedisClient {
    //    // 套接字描述符
    //    private int fd;
    //
    //    // 当前正在使用的数据库
    //    private RedisDb db;
    //
    //    // 当前正在使用的数据库的 id （号码）
    //    private int dictId;
    //
    //    // 客户端的名字
    //    private robj name;             /* As set by CLIENT SETNAME */
    //
    //    // 查询缓冲区
    //    private sds queryBuf;
    //
    //    // 查询缓冲区长度峰值
    //    private long queryBufPeak;   /* Recent (100ms or more) peak of querybuf size */
    //
    //    // 参数数量
    //    private int argc;
    //
    //    // 参数对象数组
    //    private robj argv;
    //
    //    // 记录被客户端执行的命令
    //    private RedisCommand cmd;
    //    private RedisCommand lastCmd;
    //
    //    // 请求的类型：内联命令还是多条命令
    //    private int reqType;
    //
    //    // 剩余未读取的命令内容数量
    //    private int multiBulkLen;       /* number of multi bulk arguments left to read */
    //
    //    // 命令内容的长度
    //    private long bulkLen;           /* length of bulk argument in multi bulk request */
    //
    //    // 回复链表
    //    private list reply;
    //
    //    // 回复链表中对象的总大小
    //    private long replyBytes; /* Tot bytes of objects in reply list */
    //
    //    // 已发送字节，处理 short write 用
    //    private int sentLen;            /* Amount of bytes already sent in the current
    //                               buffer or object being sent. */
    //
    //    // 创建客户端的时间
    //    private long cTime;           /* Client creation time */
    //
    //    // 客户端最后一次和服务器互动的时间
    //    private long lastInteraction; /* time of the last interaction, used for timeout */
    //
    //    // 客户端的输出缓冲区超过软性限制的时间
    //    private long obufSoftLimitReachedTime;
    //
    //    // 客户端状态标志
    //    private int flags;              /* REDIS_SLAVE | REDIS_MONITOR | REDIS_MULTI ... */
    //
    //    // 当 server.requirepass 不为 NULL 时
    //    // 代表认证的状态
    //    // 0 代表未认证， 1 代表已认证
    //    private int authenticated;      /* when requirepass is non-NULL */
    //
    //    // 复制状态
    //    private int replState;          /* replication state if this is a slave */
    //    // 用于保存主服务器传来的 RDB 文件的文件描述符
    //    private int replDBFd;           /* replication DB file descriptor */
    //
    //    // 读取主服务器传来的 RDB 文件的偏移量
    //    private off_t repldBoff;        /* replication DB file offset */
    //    // 主服务器传来的 RDB 文件的大小
    //    private off_t replDBSize;       /* replication DB file size */
    //
    //    private sds replPreamble;       /* replication DB preamble. */
    //
    //    // 主服务器的复制偏移量
    //    private long replOff;      /* replication offset if this is our master */
    //    // 从服务器最后一次发送 REPLCONF ACK 时的偏移量
    //    private long replAckOff; /* replication ack offset, if this is a slave */
    //    // 从服务器最后一次发送 REPLCONF ACK 的时间
    //    private long replAckTime;/* replication ack time, if this is a slave */
    //    // 主服务器的 master run ID
    //    // 保存在客户端，用于执行部分重同步
    //    private char replRunId[REDIS_RUN_ID_SIZE+1]; /* master run id if this is a master */
    //    // 从服务器的监听端口号
    //    private int slaveListeningPort; /* As configured with: SLAVECONF listening-port */
    //
    //    // 事务状态
    //    private multiState mState;      /* MULTI/EXEC state */
    //
    //    // 阻塞类型
    //    private int bType;              /* Type of blocking op if REDIS_BLOCKED. */
    //    // 阻塞状态
    //    private blockingState bPop;     /* blocking state */
    //
    //    // 最后被写入的全局复制偏移量
    //    private long woff;         /* Last write global replication offset. */
    //
    //    // 被监视的键
    //    private list watchedKeys;     /* Keys WATCHED for MULTI/EXEC CAS */
    //
    //    // 这个字典记录了客户端所有订阅的频道
    //    // 键为频道名字，值为 NULL
    //    // 也即是，一个频道的集合
    //    private dict pubsubChannels;  /* channels a client is interested in (SUBSCRIBE) */
    //
    //    // 链表，包含多个 pubsubPattern 结构
    //    // 记录了所有订阅频道的客户端的信息
    //    // 新 pubsubPattern 结构总是被添加到表尾
    //    private list pubsubPatterns;  /* patterns a client is interested in (SUBSCRIBE) */
    //    private sds peerId;             /* Cached peer ID. */
    //
    //    /* Response buffer */
    //    // 回复偏移量
    //    private int bufPos;
    //    // 回复缓冲区
    //    private char buf[REDIS_REPLY_CHUNK_BYTES];
}
