package io.mycat.jredis;

import io.mycat.jredis.client.RedisClient;
import io.mycat.jredis.command.RedisParser;
import io.mycat.jredis.struct.Sds;
import org.junit.Test;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 16:49
 * <br/>User: gaozhiwen
 */
public class RedisParserTest {
    @Test
    public void testParser() {
        String str = "*1\r\n$4\r\ntest\r\n";
        RedisClient client = new RedisClient();
        client.setQueryBuf(Sds.sdsNew(str));
        RedisParser.processInputBuffer(client);
    }
}
