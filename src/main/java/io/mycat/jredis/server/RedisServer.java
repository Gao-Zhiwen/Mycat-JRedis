package io.mycat.jredis.server;

import io.mycat.jredis.client.RedisClient;
import io.mycat.jredis.command.RedisCommand;
import io.mycat.jredis.config.ClientBufferLimitsConfig;
import io.mycat.jredis.config.SaveParam;
import io.mycat.jredis.operation.RedisOpArray;
import io.mycat.jredis.server.db.RedisDb;
import io.mycat.jredis.struct.Sds;

import java.util.List;
import java.util.Map;

public class RedisServer {
    // 配置文件的绝对路径
    private String configFile;

    // serverCron() calls frequency in hertz 每秒调用的次数
    private int hz;

    // 数据库
    private RedisDb[] db;

    // 命令表（受到 rename 配置选项的作用）
    private Map commands;
    // 命令表（无 rename 配置选项的作用）
    private Map origCommands;

    // 事件状态 todo
    //    private AeEventLoop el;

    // 最近一次使用时钟
    private long lruClock;

    // 关闭服务器的标识
    private int shutDownAsap;

    // 在执行 serverCron() 时进行渐进式 rehash
    private int activeReHashing;

    // 是否设置了密码
    private String requirePass;

    // PID 文件
    private String pidFile;

    // 架构类型
    private int archBits;

    // serverCron() 函数的运行次数计数器
    private int cronLoops;

    // 本服务器的 RUN ID
    private String runId;

    // 服务器是否运行在 SENTINEL 模式
    private int sentinelMode;


    /* Networking */

    // TCP 监听端口
    private int port;

    private int tcpBackLog;

    // 地址
    private String bindAddr;
    // 地址数量
    private int bindAddrCount;

    // UNIX 套接字
    private String unixSocket;
    private int unixSocketPerm;

    // 描述符
    private int[] ipFd;// = new int[REDIS_BINDADDR_MAX];
    // 描述符数量
    private int ipFdCount;

    // UNIX 套接字文件描述符
    private int soFd;

    private int[] cFd;// = new int[REDIS_BINDADDR_MAX];
    private int cFdCount;

    // 一个链表，保存了所有客户端状态结构
    private List clients;
    // 链表，保存了所有待关闭的客户端
    private List clientsToClose;

    // 链表，保存了所有从服务器，以及所有监视器
    private List slaves;
    private List monitors;

    // 服务器的当前客户端，仅用于崩溃报告
    private RedisClient currentClient;

    private int clientsPaused;
    private long clientsPauseEndTime;

    // 网络错误
    private String netErr;

    // MIGRATE 缓存
    private Map migrateCachedSockets;


    /* RDB / AOF loading information */

    // 这个值为真时，表示服务器正在进行载入
    private int loading;

    // 正在载入的数据的大小
    private long loadingTotalBytes;

    // 已载入数据的大小
    private long loadingLoadedBytes;

    // 开始进行载入的时间
    private long loadingStartTime;
    private long loadingProcessEventsIntervalBytes;

    /* Fast pointers to often looked up command */
    // 常用命令的快捷连接
    private RedisCommand delCommand;
    private RedisCommand multiCommand;
    private RedisCommand lpushCommand;
    private RedisCommand lpopCommand;
    private RedisCommand rpopCommand;


    /* Fields used only for stats */

    // 服务器启动时间
    private long statStartTime;

    // 已处理命令的数量
    private long statNumCommands;

    // 服务器接到的连接请求数量
    private long statNumConnections;

    // 已过期的键数量
    private long statExpiredKeys;

    // 因为回收内存而被释放的过期键的数量
    private long statEvictedKeys;

    // 成功查找键的次数
    private long statKeySpaceHits;

    // 查找键失败的次数
    private long statKeySpaceMisses;

    // 已使用内存峰值
    private long statPeakMemory;

    // 最后一次执行 fork() 时消耗的时间
    private long statForkTime;

    // 服务器因为客户端数量过多而拒绝客户端连接的次数
    private long statRejectedConn;

    // 执行 full sync 的次数
    private long statSyncFull;

    // PSYNC 成功执行的次数
    private long statSyncPartialOk;

    // PSYNC 执行失败的次数
    private long statSyncPartialErr;

    /* slowlog */

    // 保存了所有慢查询日志的链表
    private List slowLog;

    // 下一条慢查询日志的 ID
    private long slowLogEntryId;

    // 服务器配置 slowlog-log-slower-than 选项的值
    private long slowLogLogSlowerThan;

    // 服务器配置 slowlog-max-len 选项的值
    private long slowlogMaxLen;
    private long residentSetSize;

    // 最后一次进行抽样的时间
    private long opsSecLastSampleTime;
    // 最后一次抽样时，服务器已执行命令的数量
    private long opsSecLastSampleOps;
    // 抽样结果
    private long[] opsSecSamples;// = new long[REDIS_OPS_SEC_SAMPLES];
    // 数组索引，用于保存抽样结果，并在需要时回绕到 0
    private int opsSecIdx;


    /* Configuration */

    // 日志可见性
    private int verbosity;

    // 客户端最大空转时间
    private int maxIdleTime;

    // 是否开启 SO_KEEPALIVE 选项
    private int tcpKeepAlive;
    private int activeExpireEnabled;
    private long clientMaxQueryBufLen;
    private int dbNum;
    private int daemonize;
    // 客户端输出缓冲区大小限制
    // 数组的元素有 REDIS_CLIENT_LIMIT_NUM_CLASSES 个
    // 每个代表一类客户端：普通、从服务器、pubsub，诸如此类
    private ClientBufferLimitsConfig[] clientObufLimits;
    // = new ClientBufferLimitsConfig[REDIS_CLIENT_LIMIT_NUM_CLASSES];


    /* AOF persistence */

    // AOF 状态（开启/关闭/可写）
    private int aofState;

    // 所使用的 fsync 策略（每个写入/每秒/从不）
    private int aofFsync;
    private String aofFileName;
    private int aofNoFsyncOnRewrite;
    private int aofRewritePerc;
    private long aofRewriteMinSize;

    // 最后一次执行 BGREWRITEAOF 时， AOF 文件的大小
    private long aofRewriteBaseSize;

    // AOF 文件的当前字节大小
    private long aofCurrentSize;
    private int aofRewriteScheduled;

    // 负责进行 AOF 重写的子进程 ID
    private int aofChildPid;

    // AOF 重写缓存链表，链接着多个缓存块
    private List aofRewriteBufBlocks;

    // AOF 缓冲区
    private Sds aofBuf;

    // AOF 文件的描述符
    private int aofFd;

    // AOF 的当前目标数据库
    private int aofSelectedDb;

    // 推迟 write 操作的时间
    private long aofFlushPostponedStart;

    // 最后一直执行 fsync 的时间
    private long aofLastFsync;
    private long aofRewriteTimeLast;

    // AOF 重写的开始时间
    private long aofRewriteTimeStart;

    // 最后一次执行 BGREWRITEAOF 的结果
    private int aofLastBgreWriteStatus;

    // 记录 AOF 的 write 操作被推迟了多少次
    private long aofDelayedFsync;

    // 指示是否需要每写入一定量的数据，就主动执行一次 fsync()
    private int aofRewriteIncrementalFsync;
    private int aofLastWriteStatus;
    private int aofLastWriteErrNo;
    /* RDB persistence */

    // 自从上次 SAVE 执行以来，数据库被修改的次数
    private long dirty;

    // BGSAVE 执行前的数据库被修改次数
    private long dirtyBeforeBgSave;

    // 负责执行 BGSAVE 的子进程的 ID
    // 没在执行 BGSAVE 时，设为 -1
    private int rdbChildPid;
    private SaveParam saveParams;
    private int saveParamsLen;
    private String rdbFileName;
    private int rdbCompression;
    private int rdbCheckSum;

    // 最后一次完成 SAVE 的时间
    private long lastSave;

    // 最后一次尝试执行 BGSAVE 的时间
    private long lastBgSaveTry;

    // 最近一次 BGSAVE 执行耗费的时间
    private long rdbSaveTimeLast;

    // 数据库最近一次开始执行 BGSAVE 的时间
    private long rdbSaveTimeStart;

    // 最后一次执行 SAVE 的状态
    private int lastBgSaveStatus;
    private int stopWritesOnBgSaveErr;


    /* Propagation of commands in AOF / replication */
    private RedisOpArray alsoPropagate;


    /* Logging */
    private String logFile;
    private int syslogEnabled;
    private String syslogIdent;
    private int syslogFacility;


    /* Replication (master) */
    private int slaveSelDb;
    // 全局复制偏移量（一个累计值）
    private long masterReplOffset;
    // 主服务器发送 PING 的频率
    private int replPingSlavePeriod;

    // backlog 本身
    private String replBacklog;
    // backlog 的长度
    private long replBacklogSize;
    // backlog 中数据的长度
    private long replBacklogHistLen;
    // backlog 的当前索引
    private long replBacklogIdx;
    // backlog 中可以被还原的第一个字节的偏移量
    private long replBacklogOff;
    // backlog 的过期时间
    private long replBacklogTimeLimit;

    // 距离上一次有从服务器的时间
    private long replNoSlavesSince;

    // 是否开启最小数量从服务器写入功能
    private int replMinSlavesToWrite;
    // 定义最小数量从服务器的最大延迟值
    private int replMinSlavesMaxLag;
    // 延迟良好的从服务器的数量
    private int replGoodSlavesCount;


    /* Replication (slave) */
    // 主服务器的验证密码
    private String masterAuth;
    // 主服务器的地址
    private String masterHost;
    // 主服务器的端口
    private int masterPort;
    // 超时时间
    private int replTimeout;
    // 主服务器所对应的客户端
    private RedisClient master;
    // 被缓存的主服务器，PSYNC 时使用
    private RedisClient cachedMaster;
    private int repl_syncio_timeout;
    // 复制的状态（服务器是从服务器时使用）
    private int replState;
    // RDB 文件的大小
    private long replTransferSize;
    // 已读 RDB 文件内容的字节数
    private long replTransferRead;
    // 最近一次执行 fsync 时的偏移量
    // 用于 sync_file_range 函数
    private long replTransferLastFsyncOff;
    // 主服务器的套接字
    private int replTransferS;
    // 保存 RDB 文件的临时文件的描述符
    private int replTransferFd;
    // 保存 RDB 文件的临时文件名字
    private String replTransferTmpFile;
    // 最近一次读入 RDB 内容的时间
    private long replTransferLastIO;
    private int replServeStaleData;
    // 是否只读从服务器？
    private int replSlaveRO;
    // 连接断开的时长
    private long replDownSince;
    // 是否要在 SYNC 之后关闭 NODELAY ？
    private int replDisableTcpNoDelay;
    // 从服务器优先级
    private int slavePriority;
    // 本服务器（从服务器）当前主服务器的 RUN ID
    private String replMasterRunId;
    // 初始化偏移量
    private long replMasterInitialOffset;


    /* Replication script cache. */
    // 复制脚本缓存
    // 字典
    private Map replScriptCacheDict;
    // FIFO 队列
    private List replScriptCacheFifo;
    // 缓存的大小
    private int replScriptCacheSize;

    /* Synchronous replication. */
    private List clientsWaitingAcks;
    private int getAckFromSlaves;
    /* Limits */
    private int maxClients;
    private long maxMemory;
    private int maxMemoryPolicy;
    private int maxMemorySamples;


    /* Blocked clients */
    private int bpopBlockedClients;
    private List unblockedClients;
    private List readyKeys;


    /* Sort parameters - qsort_r() is only available under BSD so we
     * have to take this state global, in order to pass it to sortCompare() */
    private int sortDesc;
    private int sortAlpha;
    private int sortByPattern;
    private int sortStore;


    /* Zip structure config, see redis.conf for more information  */
    private long hashMaxZiplistEntries;
    private long hashMaxZiplistValue;
    private long listMaxZiplistEntries;
    private long listMaxZiplistValue;
    private long setMaxIntsetEntries;
    private long zsetMaxZiplistEntries;
    private long zsetMaxZiplistValue;
    private long hllSparseMaxBytes;
    private long unixTime;
    private long msTime;

    /* Pubsub */
    // 字典，键为频道，值为链表
    // 链表中保存了所有订阅某个频道的客户端
    // 新客户端总是被添加到链表的表尾
    private Map pubsubChannels;

    // 这个链表记录了客户端订阅的所有模式的名字
    private List pubsubPatterns;

    private int notifyKeyspaceEvents;


    /* Cluster */

    private int clusterEnabled;
    private long clusterNodeTimeout;
    private String clusterConfigFile;
    //todo
    //    private ClusterState cluster;

    private int clusterMigrationBarrier;
    /* Scripting */

    // Lua 环境 todo
    //    private LuaState lua;

    // 复制执行 Lua 脚本中的 Redis 命令的伪客户端
    private RedisClient luaClient;

    // 当前正在执行 EVAL 命令的客户端，如果没有就是 NULL
    private RedisClient luaCaller;

    // 一个字典，值为 Lua 脚本，键为脚本的 SHA1 校验和
    private Map luaScripts;
    // Lua 脚本的执行时限
    private long luaTimeLimit;
    // 脚本开始执行的时间
    private long luaTimeStart;

    // 脚本是否执行过写命令
    private int luaWriteDirty;

    // 脚本是否执行过带有随机性质的命令
    private int luaRandomDirty;

    // 脚本是否超时
    private int luaTimedOut;

    // 是否要杀死脚本
    private int luaKill;


    /* Assert & bug reporting */

    private String assertFailed;
    private String assertFile;
    private int assertLine;
    private int bugReportStart;
    private int watchDogPeriod;
}
