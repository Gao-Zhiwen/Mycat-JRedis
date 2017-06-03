package io.mycat.jredis.command;

import io.mycat.jredis.client.RedisClient;
import io.mycat.jredis.client.ReqType;
import io.mycat.jredis.message.RedisMessage;
import io.mycat.jredis.message.RedisState;
import io.mycat.jredis.struct.Sds;

public class RedisParser {
    public static void processInputBuffer(RedisClient redisClient) {
        Sds queryBuf = redisClient.getQueryBuf();
        while (queryBuf.getLength() > 0) {
            //todo check if blocked or closed

            //获取请求的类型
            if (redisClient.getReqType() == 0) {
                if (queryBuf.getBuf().charAt(0) == RedisMessage.REDIS_STAR) {
                    redisClient.setReqType(ReqType.REDIS_REQ_MULTIBULK);
                } else {
                    redisClient.setReqType(ReqType.REDIS_REQ_INLINE);
                }
            }

            //根据不同类型调用不同函数解析参数
            if (redisClient.getReqType() == ReqType.REDIS_REQ_INLINE) {
                if (processInlineBuffer(redisClient) != RedisState.REDIS_OK) {
                    break;
                }
            } else if (redisClient.getReqType() == ReqType.REDIS_REQ_MULTIBULK) {
                if (processMultibulkBuffer(redisClient) != RedisState.REDIS_OK) {
                    break;
                }
            } else {

            }
        }
    }

    private static int processInlineBuffer(RedisClient redisClient) {
        Sds queryBuf = redisClient.getQueryBuf();
        queryBuf.setBuf("");

        //todo handle \r\n

        //取出\r\n前的内容

        return RedisState.REDIS_OK;
    }


    //按照协议的格式从querybuf中读出参数的值
    private static int processMultibulkBuffer(RedisClient redisClient) {
        String newLine = null;
        int pos = 0;
        long ll = 0;

        String buf = redisClient.getQueryBuf().getBuf();
        if (redisClient.getMultiBulkLen() == 0) {
            newLine = buf.substring(0, buf.indexOf(RedisMessage.CRLF));//todo trim?

            if (newLine == null) {
                //todo
                return RedisState.REDIS_ERR;
            }

            ll = Long.parseLong(newLine.substring(1));
            pos = newLine.length() + 2;
            redisClient.setMultiBulkLen(ll);
        }

        for (long i = 0, len = redisClient.getMultiBulkLen(); i < len; i++) {
            if (redisClient.getMultiBulkLen() == -1) {
                newLine = buf.substring(pos, buf.indexOf(RedisMessage.CRLF, pos));

                ll = Long.parseLong(newLine.substring(1));

                pos += newLine.length() + 2;
                redisClient.setBulkLen(ll);
            }

            {
                if (pos == 0
                        && redisClient.getQueryBuf().getLength() == redisClient.getBulkLen() + 2) {

                } else {
                    //                    redisClient.getArgc()
                    pos += redisClient.getBulkLen() + 2;
                }
                redisClient.setBulkLen(-1);
                redisClient.setMultiBulkLen(redisClient.getMultiBulkLen() - 1);
            }
        }

        redisClient.getQueryBuf().setBuf(buf.substring(pos));
        return RedisState.REDIS_OK;
    }
}
