package com.cqx.jersey;

import java.net.URI;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

/**
 * 启动restful服务
 * */
public class App {
	public static void main(String[] args) {
		JettyHttpContainerFactory.createServer(URI.create("http://localhost:18061/"), new RestApplication());
	}
}
