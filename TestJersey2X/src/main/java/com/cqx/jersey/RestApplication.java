package com.cqx.jersey;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * ×ÊÔ´Ó³Éä
 * */
public class RestApplication extends ResourceConfig {
	public RestApplication() {
		this.packages("com.cqx.jersey");
	}
}
