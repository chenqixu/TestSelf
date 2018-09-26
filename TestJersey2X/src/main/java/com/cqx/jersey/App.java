package com.cqx.jersey;

import java.net.URI;

import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

/**
 * ����restful����
 * */
public class App {
	public static void main(String[] args) {
		JettyHttpContainerFactory.createServer(URI.create("http://localhost:8082/"), new RestApplication());
	}
}
