package io.mycat.jredis.server.db;

import io.mycat.jredis.struct.Sds;

public class EvictionPoolEntry {
    private long idle;
    private Sds key;

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        this.idle = idle;
    }

    public Sds getKey() {
        return key;
    }

    public void setKey(Sds key) {
        this.key = key;
    }
}
