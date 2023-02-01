package com.cqx.common.utils.redis;

import com.cqx.common.utils.redis.client.ClusterRedisClient;
import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.client.SinaleRedisClient;

public class RedisFactory {
    public static final int SINGLE_MODE_TYPE = 0;
    public static final int CLUSTER_MODE_TYPE = 1;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int mode_type = SINGLE_MODE_TYPE;
        private String ip;
        private int port;
        private String ip_ports;
        // 默认不开启管道，必须手工设置
        private boolean isPipeline = false;
        // 6.2.7 支持密码
        private String password;
        // 最大等待时长
        private int max_wait_millis = RedisClient.DEFAULT_MAX_WAIT_MILLIS;

        public Builder setMode(int mode_type) {
            this.mode_type = mode_type;
            return this;
        }

        public String getIp() {
            return ip;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public int getPort() {
            return port;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public String getIp_ports() {
            return ip_ports;
        }

        public Builder setIp_ports(String ip_ports) {
            this.ip_ports = ip_ports;
            return this;
        }

        public RedisClient build() {
            if (mode_type == SINGLE_MODE_TYPE)
                return new SinaleRedisClient(this);
            else if (mode_type == CLUSTER_MODE_TYPE)
                return new ClusterRedisClient(this);
            return new SinaleRedisClient(this);
        }

        public boolean isPipeline() {
            return isPipeline;
        }

        public Builder setPipeline(boolean pipeline) {
            isPipeline = pipeline;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public int getMax_wait_millis() {
            return max_wait_millis;
        }

        public Builder setMax_wait_millis(int max_wait_millis) {
            this.max_wait_millis = max_wait_millis;
            return this;
        }
    }
}
