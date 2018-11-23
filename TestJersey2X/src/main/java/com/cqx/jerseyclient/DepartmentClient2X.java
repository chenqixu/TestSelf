package com.cqx.jerseyclient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

/**
 * Client客户端，2.x方式
 * */
public class DepartmentClient2X {
	public static void main(String[] args) {
		// 初始化客户端，2.x方式
		Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
		WebTarget webTarget = client.target("http://localhost:8082/dept").path("save");		
		
		// 返回类型
//		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_ATOM_XML);
		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
		// 请求值和请求类型
		Response response = invocationBuilder.post(Entity.entity("name=newland", MediaType.APPLICATION_FORM_URLENCODED));
		
		// 打印输出状态和结果
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}
}
