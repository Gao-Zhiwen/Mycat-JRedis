package io.mycat.jredis.client;

import io.mycat.jredis.command.RedisCommand;
import io.mycat.jredis.server.db.RedisDb;
import io.mycat.jredis.struct.RedisObject;
import io.mycat.jredis.struct.Sds;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

public class RedisClient {
    // 套接字描述符
    private SocketChannel socketChannel;

    // 当前正在使用的数据库
    private RedisDb db;

    // 当前正在使用的数据库的 id （号码）
    private int dictId;

    // 客户端的名字
    private RedisObject name;

    // 查询缓冲区
    private Sds queryBuf;

    // 查询缓冲区长度峰值
    private long queryBufPeak;

    // 参数数量
    private int argc;

    // 参数对象数组
    private RedisObject[] argv;

    // 记录被客户端执行的命令
    private RedisCommand cmd;
    private RedisCommand lastCmd;

    // 请求的类型：内联命令还是多条命令
    private int reqType;

    // 剩余未读取的命令内容数量
    private long multiBulkLen;

    // 命令内容的长度
    private long bulkLen;

    // 回复链表
    private List reply;

    // 回复链表中对象的总大小
    private long replyBytes;

    // 已发送字节，处理 short write 用
    private int sentLen;

    // 创建客户端的时间
    private long cTime;

    // 客户端最后一次和服务器互动的时间
    private long lastInteraction;

    // 客户端的输出缓冲区超过软性限制的时间
    private long obufSoftLimitReachedTime;

    // 客户端状态标志
    private int flags;

    // 当 server.requirepass 不为 NULL 时
    // 代表认证的状态
    // 0 代表未认证， 1 代表已认证
    private int authenticated;

    // 复制状态
    private int replState;
    // 用于保存主服务器传来的 RDB 文件的文件描述符
    private int replDBFd;

    // 读取主服务器传来的 RDB 文件的偏移量
    private long repldBoff;
    // 主服务器传来的 RDB 文件的大小
    private long replDBSize;

    private Sds replPreamble;

    // 主服务器的复制偏移量
    private long replOff;
    // 从服务器最后一次发送 REPLCONF ACK 时的偏移量
    private long replAckOff;
    // 从服务器最后一次发送 REPLCONF ACK 的时间
    private long replAckTime;
    // 主服务器的 master run ID
    // 保存在客户端，用于执行部分重同步
    //        private char replRunId[REDIS_RUN_ID_SIZE+1];
    // 从服务器的监听端口号
    private int slaveListeningPort;

    // 事务状态
    //        private multiState mState;

    // 阻塞类型
    private int bType;
    // 阻塞状态
    //        private blockingState bPop;

    // 最后被写入的全局复制偏移量
    private long woff;

    // 被监视的键
    private List watchedKeys;

    // 这个字典记录了客户端所有订阅的频道
    // 键为频道名字，值为 NULL
    // 也即是，一个频道的集合
    private Map pubsubChannels;

    // 链表，包含多个 pubsubPattern 结构
    // 记录了所有订阅频道的客户端的信息
    // 新 pubsubPattern 结构总是被添加到表尾
    private List pubsubPatterns;
    private Sds peerId;

    /* Response buffer */
    // 回复偏移量
    private int bufPos;
    // 回复缓冲区
    //        private char buf[REDIS_REPLY_CHUNK_BYTES];


    public Sds getQueryBuf() {
        return queryBuf;
    }

    public void setQueryBuf(Sds queryBuf) {
        this.queryBuf = queryBuf;
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public long getMultiBulkLen() {
        return multiBulkLen;
    }

    public void setMultiBulkLen(long multiBulkLen) {
        this.multiBulkLen = multiBulkLen;
    }

    public long getBulkLen() {
        return bulkLen;
    }

    public void setBulkLen(long bulkLen) {
        this.bulkLen = bulkLen;
    }

    public int getArgc() {
        return argc;
    }

    public void setArgc(int argc) {
        this.argc = argc;
    }

    public RedisObject[] getArgv() {
        return argv;
    }

    public void setArgv(RedisObject[] argv) {
        this.argv = argv;
    }

    public int getAndIncreaseArgs() {
        return argc++;
    }

    public long getAndDescreaseMultiBulkLen() {
        return multiBulkLen--;
    }
}
