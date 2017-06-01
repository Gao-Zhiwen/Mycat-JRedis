package io.mycat.jredis.command;

import io.mycat.jredis.command.impl.GetCommand;

/**
 * Desc: redis 命令的枚举类
 *
 * @date: 01/06/2017
 * @author: gaozhiwen
 */
public enum RedisCommand {
    GET("get", new GetCommand(), 2, "r", 0, null, 1, 1, 1, 0, 0);

    /*
     * 命令表
     *
     * 表中的每个项都由以下域组成：
     * name: 命令的名字
     * function: 一个指向命令的实现函数的指针
     * arity: 参数的数量。可以用 -N 表示 >= N
     * sflags: 字符串形式的 FLAG ，用来计算以下的真实 FLAG
     * flags: 位掩码形式的 FLAG ，根据 sflags 的字符串计算得出
     * get_keys_proc: 一个可选的函数，用于从命令中取出 key 参数，仅在以下三个参数都不足以表示 key 参数时使用
     * first_key_index: 第一个 key 参数的位置
     * last_key_index: 最后一个 key 参数的位置
     * key_step: 从 first 参数和 last 参数之间，所有 key 的步数（step）
     *           比如说， MSET 命令的格式为 MSET key value [key value ...]
     *           它的 step 就为 2
     * microseconds: 执行这个命令耗费的总微秒数
     * calls: 命令被执行的总次数
     *
     * microseconds 和 call 由 Redis 计算，总是初始化为 0 。
     *
     * 命令的 FLAG 首先由 SFLAG 域设置，之后 populateCommandTable() 函数从 sflags 属性中计算出真正的 FLAG 到 flags 属性中。
     *
     * 以下是各个 FLAG 的意义：
     * w: 写入命令，可能会修改 key space
     * r: 读命令，不修改 key space
     * m: 可能会占用大量内存的命令，调用时对内存占用进行检查
     * a: 管理用途的命令，比如 SAVE 和 SHUTDOWN
     * p: 发布/订阅相关的命令
     * f: 无视 server.dirty ，强制复制这个命令。
     * s: 不允许在脚本中使用的命令
     * R: 随机命令。
     *    命令是非确定性的：对于同样的命令，同样的参数，同样的键，结果可能不同。
     *    比如 SPOP 和 RANDOMKEY 就是这样的例子。
     * S: 如果命令在 Lua 脚本中执行，那么对输出进行排序，从而得出确定性的输出。
     * l: 允许在载入数据库时使用的命令。
     * t: 允许在附属节点带有过期数据时执行的命令。
     *    这类命令很少有，只有几个。
     * M: 不要在 MONITOR 模式下自动广播的命令。
     * k: 为这个命令执行一个显式的 ASKING ，
     *    使得在集群模式下，一个被标示为 importing 的槽可以接收这命令。
     */

    // 命令名字
    private String name;
    // 实现函数
    private Command function;
    // 参数个数
    private int arity;
    // 字符串表示的 FLAG
    private String sFlags;
    // 实际 FLAG
    private int flags;
    // 从命令中判断命令的键参数。在 Redis 集群转向时使用。
    private RedisGetKeys getKeysProc;
    // 指定哪些参数是 key
    private int firstKey;
    private int lastKey;
    private int keyStep;
    // 统计信息,microseconds 记录了命令执行耗费的总毫微秒数
    private long calls;
    // 统计信息,calls 是命令被执行的总次数
    private long microSeconds;

    RedisCommand(String name, Command function, int arity, String sFlags, int flags,
            RedisGetKeys getKeysProc, int firstKey, int lastKey, int keyStep, long calls,
            long microSeconds) {
        this.name = name;
        this.function = function;
        this.arity = arity;
        this.sFlags = sFlags;
        this.flags = flags;
        this.getKeysProc = getKeysProc;
        this.firstKey = firstKey;
        this.lastKey = lastKey;
        this.keyStep = keyStep;
        this.calls = calls;
        this.microSeconds = microSeconds;
    }
}
