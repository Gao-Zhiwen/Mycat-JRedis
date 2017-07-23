package io.mycat.jredis;

import io.mycat.jredis.datastruct.Dict;
import io.mycat.jredis.memory.MemoryManager;
import io.mycat.jredis.util.DictUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc:
 *
 * @date: 10/06/2017
 * @author: gaozhiwen
 */
public class RedisStarter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStarter.class);

    public static void main(String[] args) {
        MemoryManager.allocateMemory(1 * 1024 * 1024);
        Dict dict = DictUtil.dictCreate(null, null);
        System.out.println(dict);
    }
}
