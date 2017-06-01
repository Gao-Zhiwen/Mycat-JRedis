package io.mycat.jredis.config;

/**
 * Desc: 服务器的保存条件
 *
 * @date: 01/06/2017
 * @author: gaozhiwen
 */
public class SaveParam {
    // 多少秒之内
    private long seconds;

    // 发生多少次修改
    private int changes;

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public int getChanges() {
        return changes;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }
}
