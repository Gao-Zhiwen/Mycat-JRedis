package io.mycat.jredis.config;

/**
 * Desc: 客户端缓冲区限制
 *
 * @date: 01/06/2017
 * @author: gaozhiwen
 */
public class ClientBufferLimitsConfig {
    // 硬限制
    private long hardLimitBytes;
    // 软限制
    private long softLimitBytes;
    // 软限制时限
    private long softLimitSeconds;

    public long getHardLimitBytes() {
        return hardLimitBytes;
    }

    public void setHardLimitBytes(long hardLimitBytes) {
        this.hardLimitBytes = hardLimitBytes;
    }

    public long getSoftLimitBytes() {
        return softLimitBytes;
    }

    public void setSoftLimitBytes(long softLimitBytes) {
        this.softLimitBytes = softLimitBytes;
    }

    public long getSoftLimitSeconds() {
        return softLimitSeconds;
    }

    public void setSoftLimitSeconds(long softLimitSeconds) {
        this.softLimitSeconds = softLimitSeconds;
    }
}
