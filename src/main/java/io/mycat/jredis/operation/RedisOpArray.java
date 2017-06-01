package io.mycat.jredis.operation;

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
