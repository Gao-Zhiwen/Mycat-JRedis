package io.mycat.jredis.operation;

/**
 * Desc:
 * <p/>Date: 30/05/2017
 * <br/>Time: 23:18
 * <br/>User: gaozhiwen
 */
public class RedisOpArray {
    private RedisOp ops;
    private int numops;

    public RedisOp getOps() {
        return ops;
    }

    public void setOps(RedisOp ops) {
        this.ops = ops;
    }

    public int getNumops() {
        return numops;
    }

    public void setNumops(int numops) {
        this.numops = numops;
    }
}
