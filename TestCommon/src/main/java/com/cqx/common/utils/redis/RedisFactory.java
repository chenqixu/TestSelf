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
		
		public Builder setMode(int mode_type){
			this.mode_type = mode_type;
			return this;
		}
		
		public Builder setIp(String ip){
			this.ip = ip;
			return this;
		}
		
		public Builder setPort(int port){
			this.port = port;
			return this;
		}

		public Builder setIp_ports(String ip_ports) {
			this.ip_ports = ip_ports;
			return this;
		}
	    
	    public String getIp() {
			return ip;
		}

		public int getPort() {
			return port;
		}

		public String getIp_ports() {
			return ip_ports;
		}

		public RedisClient build() {
	    	if(mode_type==SINGLE_MODE_TYPE)
	    		return new SinaleRedisClient(this);
	    	else if(mode_type==CLUSTER_MODE_TYPE)
		    	return new ClusterRedisClient(this);
			return new SinaleRedisClient(this);
	    }
	}
}
