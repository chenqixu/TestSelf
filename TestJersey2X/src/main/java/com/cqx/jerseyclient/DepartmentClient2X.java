package com.cqx.jerseyclient;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Client客户端，2.x方式
 */
public class DepartmentClient2X {
    public static void main(String[] args) {
        // 初始化客户端，2.x方式
        Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));

        //Depart POST
//		WebTarget webTarget = client.target("http://localhost:18061/dept").path("save");
//		// 返回类型
//		Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
//		// 请求值和请求类型
//		Response response = invocationBuilder.post(Entity.entity("name=newland", MediaType.APPLICATION_FORM_URLENCODED));
//		// 打印输出状态和结果
//		System.out.println(response.getStatus());
//		System.out.println(response.readEntity(String.class));

//        //Depart GET -PathParam
//        WebTarget webTarget = client.target("http://localhost:18061/").path("dept/get/123");
//        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_ATOM_XML);
//        Response response = builder.get();
//        System.out.println("request url=" + webTarget.getUri().toString());
//        if (response.getStatus() == 200) {
//            System.out.println("status=" + response.getStatus() + ", statusInfo=" + response.getStatusInfo());
//            System.out.println("result=" + response.readEntity(String.class));
//        }

        //Conn GET -QueryParam
        WebTarget webTarget = client.target("http://localhost:18061/")
                .path("services/env/conn/detail/name").queryParam("connName", "redis_185");
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_ATOM_XML);
        Response response = builder.get();
        System.out.println("request url=" + webTarget.getUri().toString());
        if (response.getStatus() == 200) {
            System.out.println("status=" + response.getStatus() + ", statusInfo=" + response.getStatusInfo());
            System.out.println("result=" + response.readEntity(String.class));
        }
    }
}
