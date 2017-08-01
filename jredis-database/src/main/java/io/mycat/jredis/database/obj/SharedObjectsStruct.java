package io.mycat.jredis.database.obj;

import io.mycat.jredis.datastruct.UnsafeObject;

/**
 * Desc:
 *
 * @date: 26/07/2017
 * @author: gaozhiwen
 */
public class SharedObjectsStruct {
    public UnsafeObject crlf;
    public UnsafeObject ok;
    public UnsafeObject err;
    public UnsafeObject emptybulk;
    public UnsafeObject czero;
    public UnsafeObject cone;
    public UnsafeObject cnegone;
    public UnsafeObject pong;
    public UnsafeObject space;
    public UnsafeObject colon;
    public UnsafeObject nullbulk;
    public UnsafeObject nullmultibulk;
    public UnsafeObject queued;
    public UnsafeObject emptymultibulk;
    public UnsafeObject wrongtypeerr;
    public UnsafeObject nokeyerr;
    public UnsafeObject syntaxerr;
    public UnsafeObject sameobjecterr;
    public UnsafeObject outofrangeerr;
    public UnsafeObject noscripterr;
    public UnsafeObject loadingerr;
    public UnsafeObject slowscripterr;
    public UnsafeObject bgsaveerr;
    public UnsafeObject masterdownerr;
    public UnsafeObject roslaveerr;
    public UnsafeObject execaborterr;
    public UnsafeObject noautherr;
    public UnsafeObject noreplicaserr;
    public UnsafeObject busykeyerr;
    public UnsafeObject oomerr;
    public UnsafeObject plus;
    public UnsafeObject messagebulk;
    public UnsafeObject pmessagebulk;
    public UnsafeObject subscribebulk;
    public UnsafeObject unsubscribebulk;
    public UnsafeObject psubscribebulk;
    public UnsafeObject punsubscribebulk;
    public UnsafeObject del;
    public UnsafeObject rpop;
    public UnsafeObject lpop;
    public UnsafeObject lpush;
    public UnsafeObject emptyscan;
    public UnsafeObject minstring;
    public UnsafeObject maxstring;
    //    public UnsafeObject select[REDIS_SHARED_SELECT_CMDS];
    //    public UnsafeObject integers[REDIS_SHARED_INTEGERS];
    //    public UnsafeObject mbulkhdr[REDIS_SHARED_BULKHDR_LEN]; /* "*<value>\r\n" */
    //    public UnsafeObject bulkhdr[REDIS_SHARED_BULKHDR_LEN];  /* "$<value>\r\n" */
}
