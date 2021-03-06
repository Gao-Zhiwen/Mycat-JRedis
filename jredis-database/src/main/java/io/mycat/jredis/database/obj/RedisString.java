//package io.mycat.jredis.database.obj;
//
//import io.mycat.jredis.database.bean.RedisClient;
//import io.mycat.jredis.database.util.Network;
//import io.mycat.jredis.datastruct.UnsafeObject;
//import io.mycat.jredis.datastruct.redis.constant.RedisConstant;
//
///**
// * Desc:
// *
// * @date: 26/07/2017
// * @author: gaozhiwen
// */
//public class RedisString {
//    private static SharedObjectsStruct shared = new SharedObjectsStruct();
//
//    public static boolean checkStringLength(RedisClient c, long size) {
//        if (size > 512 * 1024 * 1024) {
//            Network.addReplyError(c, "string exceeds maximum allowed size (512MB)");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * setGenericCommand() 函数实现了 SET 、 SETEX 、 PSETEX 和 SETNX 命令。
//     * flags 参数的值可以是 NX 或 XX ，它们的意义请见下文。
//     * expire 定义了 Redis 对象的过期时间。
//     * 而这个过期时间的格式由 unit 参数指定。
//     * ok_reply 和 abort_reply 决定了命令回复的内容，
//     * NX 参数和 XX 参数也会改变回复。
//     * 如果 ok_reply 为 null ，那么 "+OK" 被返回。
//     * 如果 abort_reply 为 null ，那么 "$-1" 被返回。
//     *
//     * @param c
//     * @param flags
//     * @param key
//     * @param val
//     * @param expire
//     * @param unit
//     * @param okReply
//     * @param abortReply
//     */
//
//    public void setGenericCommand(RedisClient c, int flags, UnsafeObject key, UnsafeObject val,
//            UnsafeObject expire, int unit, UnsafeObject okReply, UnsafeObject abortReply) {
//        long milliseconds = 0;
//
//        // 取出过期时间
//        if (expire != null) {
//            // 取出 expire 参数的值
//            if (getLongLongFromObjectOrReply(c, expire, milliseconds, null)
//                    != RedisConstant.REDIS_OK)
//                return;
//
//            // expire 参数的值不正确时报错
//            if (milliseconds <= 0) {
//                Network.addReplyError(c, "invalid expire time in SETEX");
//                return;
//            }
//
//            // 不论输入的过期时间是秒还是毫秒
//            // Redis 实际都以毫秒的形式保存过期时间
//            // 如果输入的过期时间为秒，那么将它转换为毫秒
//            if (unit == RedisConstant.UNIT_SECONDS)
//                milliseconds *= 1000;
//        }
//
//        // 如果设置了 NX 或者 XX 参数，那么检查条件是否不符合这两个设置
//        // 在条件不符合时报错，报错的内容由 abort_reply 参数决定
//        if ((flags RedisConstant.REDIS_SET_NX & lookupKeyWrite(c.getDb(), key) != null)||(flags
//        RedisConstant.REDIS_SET_XX & lookupKeyWrite(c.getDb(), key) == null)){
//            Network.addReply(c, abortReply != null ? abortReply : shared.nullbulk);
//            return;
//        }
//
//        // 将键值关联到数据库
//        setKey(c.getDb(), key, val);
//
//        // 将数据库设为脏
//        server.dirty++;
//
//        // 为键设置过期时间
//        if (expire != null)
//            setExpire(c.getDb(), key, mstime() + milliseconds);
//
//        // 发送事件通知
//        notifyKeyspaceEvent(RedisConstant.REDIS_NOTIFY_STRING, "set", key, c.getDb().getId());
//
//        // 发送事件通知
//        if (expire != null)
//            notifyKeyspaceEvent(REDIS_NOTIFY_GENERIC, "expire", key, c.getDb().getId());
//
//        // 设置成功，向客户端发送回复，回复的内容由 ok_reply 决定
//        Network.addReply(c, okReply != null ? okReply : shared.ok);
//    }
//
//    /* SET key value [NX] [XX] [EX <seconds>] [PX <milliseconds>] */
//    public void setCommand(RedisClient c) {
//        int j;
//        UnsafeObject expire = null;
//        int unit = RedisConstant.UNIT_SECONDS;
//        int flags = RedisConstant.REDIS_SET_NO_FLAGS;
//
//        // 设置选项参数
//        for (j = 3; j < c -> argc; j++) {
//            char[] a = c -> argv[j]->ptr;
//            UnsafeObject next = (j==c -> argc - 1)?null:c -> argv[j + 1];
//
//            if ((a[0] == 'n' || a[0] == 'N') && (a[1] == 'x' || a[1] == 'X') & a[2] == '\0') {
//                flags |= RedisConstant.REDIS_SET_NX;
//            } else if ((a[0] == 'x' || a[0] == 'X')
//                    && (a[1] == 'x' || a[1] == 'X') & a[2] == '\0') {
//                flags |= RedisConstant.REDIS_SET_XX;
//            } else if ((a[0] == 'e' || a[0] == 'E')
//                    && (a[1] == 'x' || a[1] == 'X') & a[2] == '\0' & next != null) {
//                unit = RedisConstant.UNIT_SECONDS;
//                expire = next;
//                j++;
//            } else if ((a[0] == 'p' || a[0] == 'P')
//                    && (a[1] == 'x' || a[1] == 'X') & a[2] == '\0' & next != null) {
//                unit = RedisConstant.UNIT_MILLISECONDS;
//                expire = next;
//                j++;
//            } else {
//                addReply(c, shared.syntaxerr);
//                return;
//            }
//        }
//
//        // 尝试对值对象进行编码
//        c -> argv[2] = tryObjectEncoding(c -> argv[2]);
//
//        setGenericCommand(c, flags, c -> argv[1], c -> argv[2], expire, unit, null, null);
//    }
//
//    public void setnxCommand(RedisClient c) {
//        c -> argv[2] = tryObjectEncoding(c -> argv[2]);
//        setGenericCommand(c, RedisConstant.REDIS_SET_NX, c -> argv[1], c -> argv[2], null, 0,
//                shared.cone, shared.czero);
//    }
//
//    public void setexCommand(RedisClient c) {
//        c -> argv[3] = tryObjectEncoding(c -> argv[3]);
//        setGenericCommand(c, RedisConstant.REDIS_SET_NO_FLAGS, c -> argv[1], c -> argv[3],
//                c -> argv[2], RedisConstant.UNIT_SECONDS, null, null);
//    }
//
//    public void psetexCommand(RedisClient c) {
//        c -> argv[3] = tryObjectEncoding(c -> argv[3]);
//        setGenericCommand(c, RedisConstant.REDIS_SET_NO_FLAGS, c -> argv[1], c -> argv[3],
//                c -> argv[2], RedisConstant.UNIT_MILLISECONDS, null, null);
//    }
//
//    public int getGenericCommand(RedisClient c) {
//        UnsafeObject o;
//
//        // 尝试从数据库中取出键 c->argv[1] 对应的值对象
//        // 如果键不存在时，向客户端发送回复信息，并返回 null
//        if ((o = lookupKeyReadOrReply(c, c -> argv[1], shared.nullbulk)) == null)
//            return RedisConstant.REDIS_OK;
//
//        // 值对象存在，检查它的类型
//        if (o -> type != RedisConstant.REDIS_STRING) {
//            // 类型错误
//            addReply(c, shared.wrongtypeerr);
//            return RedisConstant.REDIS_ERR;
//        } else {
//            // 类型正确，向客户端返回对象的值
//            addReplyBulk(c, o);
//            return RedisConstant.REDIS_OK;
//        }
//    }
//
//    public void getCommand(RedisClient c) {
//        getGenericCommand(c);
//    }
//
//    public void getsetCommand(RedisClient c) {
//
//        // 取出并返回键的值对象
//        if (getGenericCommand(c) == RedisConstant.REDIS_ERR)
//            return;
//
//        // 编码键的新值 c->argv[2]
//        c -> argv[2] = tryObjectEncoding(c -> argv[2]);
//
//        // 将数据库中关联键 c->argv[1] 和新值对象 c->argv[2]
//        setKey(c -> db, c -> argv[1], c -> argv[2]);
//
//        // 发送事件通知
//        notifyKeyspaceEvent(REDIS_NOTIFY_STRING, "set", c -> argv[1], c -> db -> id);
//
//        // 将服务器设为脏
//        server.dirty++;
//    }
//
//    public void setrangeCommand(RedisClient c) {
//        UnsafeObject o;
//        long offset;
//
//        sds value = c -> argv[3]->ptr;
//
//        // 取出 offset 参数
//        if (getLongFromObjectOrReply(c, c -> argv[2], offset, null) != RedisConstant.REDIS_OK)
//            return;
//
//        // 检查 offset 参数
//        if (offset < 0) {
//            addReplyError(c, "offset is out of range");
//            return;
//        }
//
//        // 取出键现在的值对象
//        o = lookupKeyWrite(c -> db, c -> argv[1]);
//        if (o == null) {
//
//            // 键不存在于数据库中。。。
//
//        /* Return 0 when setting nothing on a non-existing string */
//            // value 为空，没有什么可设置的，向客户端返回 0
//            if (sdslen(value) == 0) {
//                addReply(c, shared.czero);
//                return;
//            }
//
//        /* Return when the resulting string exceeds allowed size */
//            // 如果设置后的长度会超过 Redis 的限制的话
//            // 那么放弃设置，向客户端发送一个出错回复
//            if (checkStringLength(c, offset + sdslen(value)) != RedisConstant.REDIS_OK)
//                return;
//
//            // 如果 value 没有问题，可以设置，那么创建一个空字符串值对象
//            // 并在数据库中关联键 c->argv[1] 和这个空字符串对象
//            o = createObject(REDIS_STRING, sdsempty());
//            dbAdd(c -> db, c -> argv[1], o);
//        } else {
//            size_t olen;
//
//            // 值对象存在。。。
//            // 检查值对象的类型
//            if (checkType(c, o, REDIS_STRING))
//                return;
//
//            // 取出原有字符串的长度
//            olen = stringObjectLen(o);
//
//            // value 为空，没有什么可设置的，向客户端返回 0
//            if (sdslen(value) == 0) {
//                addReplyLongLong(c, olen);
//                return;
//            }
//
//            // 如果设置后的长度会超过 Redis 的限制的话，那么放弃设置，向客户端发送一个出错回复
//            if (checkStringLength(c, offset + sdslen(value)) != RedisConstant.REDIS_OK)
//                return;
//
//            o = dbUnshareStringValue(c -> db, c -> argv[1], o);
//        }
//
//        // 这里的 sdslen(value) > 0 其实可以去掉，前面已经做了检测了
//        if (sdslen(value) > 0) {
//            // 扩展字符串值对象
//            o -> ptr = sdsgrowzero(o -> ptr, offset + sdslen(value));
//            // 将 value 复制到字符串中的指定的位置
//            memcpy((char*)o -> ptr + offset, value, sdslen(value));
//
//            // 向数据库发送键被修改的信号
//            signalModifiedKey(c -> db, c -> argv[1]);
//
//            // 发送事件通知
//            notifyKeyspaceEvent(REDIS_NOTIFY_STRING, "setrange", c -> argv[1], c -> db -> id);
//
//            // 将服务器设为脏
//            server.dirty++;
//        }
//
//        // 设置成功，返回新的字符串值给客户端
//        addReplyLongLong(c, sdslen(o -> ptr));
//    }
//
//    public void getrangeCommand(RedisClient c) {
//        UnsafeObject o;
//        long start, end;
//        char[] str, llbuf[ 32];
//        int strlen;
//
//        // 取出 start 参数
//        if (getLongFromObjectOrReply(c, c -> argv[2], start, null) != RedisConstant.REDIS_OK)
//            return;
//
//        // 取出 end 参数
//        if (getLongFromObjectOrReply(c, c -> argv[3], end, null) != RedisConstant.REDIS_OK)
//            return;
//
//        // 从数据库中查找键 c->argv[1]
//        if ((o = lookupKeyReadOrReply(c, c -> argv[1], shared.emptybulk)) == null || checkType(c, o,
//                RedisConstant.REDIS_STRING))
//            return;
//
//        // 根据编码，对对象的值进行处理
//        if (o -> encoding == RedisConstant.REDIS_ENCODING_INT) {
//            str = llbuf;
//            strlen = ll2string(llbuf, sizeof(llbuf), (long) o -> ptr);
//        } else {
//            str = o -> ptr;
//            strlen = sdslen(str);
//        }
//
//    /* Convert negative indexes */
//        // 将负数索引转换为整数索引
//        if (start < 0)
//            start = strlen + start;
//        if (end < 0)
//            end = strlen + end;
//        if (start < 0)
//            start = 0;
//        if (end < 0)
//            end = 0;
//        if ((int) end >= strlen)
//            end = strlen - 1;
//
//        if (start > end) {
//            // 处理索引范围为空的情况
//            addReply(c, shared.emptybulk);
//        } else {
//            // 向客户端返回给定范围内的字符串内容
//            addReplyBulkCBuffer(c, (char[]) str + start, end - start + 1);
//        }
//    }
//
//    public void mgetCommand(RedisClient c) {
//        int j;
//
//        addReplyMultiBulkLen(c, c -> argc - 1);
//        // 查找并返回所有输入键的值
//        for (j = 1; j < c -> argc; j++) {
//            // 查找键 c->argc[j] 的值
//            UnsafeObject * o = lookupKeyRead(c -> db, c -> argv[j]);
//            if (o == null) {
//                // 值不存在，向客户端发送空回复
//                addReply(c, shared.nullbulk);
//            } else {
//                if (o -> type != RedisConstant.REDIS_STRING) {
//                    // 值存在，但不是字符串类型
//                    addReply(c, shared.nullbulk);
//                } else {
//                    // 值存在，并且是字符串
//                    addReplyBulk(c, o);
//                }
//            }
//        }
//    }
//
//    public void msetGenericCommand(RedisClient c, int nx) {
//        int j, busykeys = 0;
//
//        // 键值参数不是成相成对出现的，格式不正确
//        if ((c -> argc % 2) == 0) {
//            addReplyError(c, "wrong number of arguments for MSET");
//            return;
//        }
//    /* Handle the NX flag. The MSETNX semantic is to return zero and don't
//     * set nothing at all if at least one already key exists. */
//        // 如果 nx 参数为真，那么检查所有输入键在数据库中是否存在
//        // 只要有一个键是存在的，那么就向客户端发送空回复
//        // 并放弃执行接下来的设置操作
//        if (nx) {
//            for (j = 1; j < c -> argc; j += 2) {
//                if (lookupKeyWrite(c -> db, c -> argv[j]) != null) {
//                    busykeys++;
//                }
//            }
//            // 键存在
//            // 发送空白回复，并放弃执行接下来的设置操作
//            if (busykeys != 0) {
//                addReply(c, shared.czero);
//                return;
//            }
//        }
//
//        // 设置所有键值对
//        for (j = 1; j < c -> argc; j += 2) {
//
//            // 对值对象进行解码
//            c -> argv[j + 1] = tryObjectEncoding(c -> argv[j + 1]);
//
//            // 将键值对关联到数据库
//            // c->argc[j] 为键
//            // c->argc[j+1] 为值
//            setKey(c -> db, c -> argv[j], c -> argv[j + 1]);
//
//            // 发送事件通知
//            notifyKeyspaceEvent(RedisConstant.REDIS_NOTIFY_STRING, "set", c -> argv[j],
//                    c -> db -> id);
//        }
//
//        // 将服务器设为脏
//        server.dirty += (c -> argc - 1) / 2;
//
//        // 设置成功
//        // MSET 返回 OK ，而 MSETNX 返回 1
//        addReply(c, nx ? shared.cone : shared.ok);
//    }
//
//    public void msetCommand(RedisClient c) {
//        msetGenericCommand(c, 0);
//    }
//
//    public void msetnxCommand(RedisClient c) {
//        msetGenericCommand(c, 1);
//    }
//
//    public void incrDecrCommand(RedisClient c, long incr) {
//        long value, oldvalue;
//        UnsafeObject o, newValue;
//
//        // 取出值对象
//        o = lookupKeyWrite(c -> db, c -> argv[1]);
//
//        // 检查对象是否存在，以及类型是否正确
//        if (o != null & checkType(c, o, RedisConstant.REDIS_STRING))
//            return;
//
//        // 取出对象的整数值，并保存到 value 参数中
//        if (getLongLongFromObjectOrReply(c, o, value, null) != RedisConstant.REDIS_OK)
//            return;
//
//        // 检查加法操作执行之后值释放会溢出
//        // 如果是的话，就向客户端发送一个出错回复，并放弃设置操作
//        oldvalue = value;
//        if ((incr < 0 & oldvalue < 0 & incr < (Long.MIN_VALUE - oldvalue)) || (incr > 0
//                & oldvalue > 0 & incr > (Long.MAX_VALUE - oldvalue))) {
//            addReplyError(c, "increment or decrement would overflow");
//            return;
//        }
//
//        // 进行加法计算，并将值保存到新的值对象中
//        // 然后用新的值对象替换原来的值对象
//        value += incr;
//        newValue = createStringObjectFromLongLong(value);
//        if (o != null)
//            dbOverwrite(c -> db, c -> argv[1], newValue);
//        else
//            dbAdd(c -> db, c -> argv[1], newValue);
//
//        // 向数据库发送键被修改的信号
//        signalModifiedKey(c -> db, c -> argv[1]);
//
//        // 发送事件通知
//        notifyKeyspaceEvent(RedisConstant.REDIS_NOTIFY_STRING, "incrby", c -> argv[1],
//                c -> db -> id);
//
//        // 将服务器设为脏
//        server.dirty++;
//
//        // 返回回复
//        addReply(c, shared.colon);
//        addReply(c, newValue);
//        addReply(c, shared.crlf);
//    }
//
//    public void incrCommand(RedisClient c) {
//        incrDecrCommand(c, 1);
//    }
//
//    public void decrCommand(RedisClient c) {
//        incrDecrCommand(c, -1);
//    }
//
//    public void incrbyCommand(RedisClient c) {
//        long incr;
//
//        if (getLongLongFromObjectOrReply(c, c -> argv[2], incr, null) != RedisConstant.REDIS_OK)
//            return;
//        incrDecrCommand(c, incr);
//    }
//
//    public void decrbyCommand(RedisClient c) {
//        long incr;
//
//        if (getLongLongFromObjectOrReply(c, c -> argv[2], incr, null) != RedisConstant.REDIS_OK)
//            return;
//        incrDecrCommand(c, -incr);
//    }
//
//    public void incrbyfloatCommand(RedisClient c) {
//        double incr, value;
//        UnsafeObject o, newValue, aux;
//
//        // 取出值对象
//        o = lookupKeyWrite(c -> db, c -> argv[1]);
//
//        // 检查对象是否存在，以及类型是否正确
//        if (o != null & checkType(c, o, REDIS_STRING))
//            return;
//
//        // 将对象的整数值保存到 value 参数中
//        // 并取出 incr 参数的值
//        if (getLongDoubleFromObjectOrReply(c, o, value, null) != RedisConstant.REDIS_OK
//                || getLongDoubleFromObjectOrReply(c, c -> argv[2], incr, null)
//                != RedisConstant.REDIS_OK)
//            return;
//
//        // 进行加法计算，并检查是否溢出
//        value += incr;
//        if (isnan(value) || isinf(value)) {
//            addReplyError(c, "increment would produce NaN or Infinity");
//            return;
//        }
//
//        // 用一个包含新值的新对象替换现有的值对象
//        newValue = createStringObjectFromLongDouble(value);
//        if (o != null)
//            dbOverwrite(c -> db, c -> argv[1], newValue);
//        else
//            dbAdd(c -> db, c -> argv[1], newValue);
//
//        // 向数据库发送键被修改的信号
//        signalModifiedKey(c -> db, c -> argv[1]);
//
//        // 发送事件通知
//        notifyKeyspaceEvent(RedisConstant.REDIS_NOTIFY_STRING, "incrbyfloat", c -> argv[1],
//                c -> db -> id);
//
//        // 将服务器设为脏
//        server.dirty++;
//
//        // 回复
//        addReplyBulk(c, newValue);
//
//    /* Always replicate INCRBYFLOAT as a SET command with the final value
//     * in order to make sure that differences in float precision or formatting
//     * will not create differences in replicas or after an AOF restart. */
//        // 在传播 INCRBYFLOAT 命令时，总是用 SET 命令来替换 INCRBYFLOAT 命令
//        // 从而防止因为不同的浮点精度和格式化造成 AOF 重启时的数据不一致
//        aux = createStringObject("SET", 3);
//        rewriteClientCommandArgument(c, 0, aux);
//        decrRefCount(aux);
//        rewriteClientCommandArgument(c, 2, newValue);
//    }
//
//    public void appendCommand(RedisClient c) {
//        int totlen;
//        UnsafeObject o, append;
//
//        // 取出键相应的值对象
//        o = lookupKeyWrite(c -> db, c -> argv[1]);
//        if (o == null) {
//
//            // 键值对不存在。。。
//
//        /* Create the key */
//            // 键值对不存在，创建一个新的
//            c -> argv[2] = tryObjectEncoding(c -> argv[2]);
//            dbAdd(c -> db, c -> argv[1], c -> argv[2]);
//            incrRefCount(c -> argv[2]);
//            totlen = stringObjectLen(c -> argv[2]);
//        } else {
//
//            // 键值对存在。。。
//
//            // 检查类型
//            if (checkType(c, o, RedisConstant.REDIS_STRING))
//                return;
//
//            // 检查追加操作之后，字符串的长度是否符合 Redis 的限制
//            append = c -> argv[2];
//            totlen = stringObjectLen(o) + sdslen(append -> ptr);
//            if (checkStringLength(c, totlen))
//                return;
//
//            // 执行追加操作
//            o = dbUnshareStringValue(c -> db, c -> argv[1], o);
//            o -> ptr = sdscatlen(o -> ptr, append -> ptr, sdslen(append -> ptr));
//            totlen = sdslen(o -> ptr);
//        }
//
//        // 向数据库发送键被修改的信号
//        signalModifiedKey(c -> db, c -> argv[1]);
//
//        // 发送事件通知
//        notifyKeyspaceEvent(RedisConstant.REDIS_NOTIFY_STRING, "append", c -> argv[1],
//                c -> db -> id);
//
//        // 将服务器设为脏
//        server.dirty++;
//
//        // 发送回复
//        addReplyLongLong(c, totlen);
//    }
//
//    public void strlenCommand(RedisClient c) {
//        UnsafeObject o;
//
//        // 取出值对象，并进行类型检查
//        if ((o = lookupKeyReadOrReply(c, c -> argv[1], shared.czero)) == null || checkType(c, o,
//                RedisConstant.REDIS_STRING))
//            return;
//
//        // 返回字符串值的长度
//        addReplyLongLong(c, stringObjectLen(o));
//    }
//}
