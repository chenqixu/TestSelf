package com.cqx.jersey;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * 资源映射
 * */
public class RestApplication extends ResourceConfig {
	public RestApplication() {
		this.packages("com.cqx.jersey");
	}
}
