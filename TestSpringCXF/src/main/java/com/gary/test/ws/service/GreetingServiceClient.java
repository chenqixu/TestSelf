package com.gary.test.ws.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.spring.test.bean.GreetingRequest;
import com.spring.test.bean.GreetingResponse;

public class GreetingServiceClient {
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ClientBeans.xml");
		GreetingService gs = (GreetingService) ctx.getBean("GreetingService");
//		System.out.println(gs.greeting("abc"));
		GreetingRequest requestObj = new GreetingRequest();
		requestObj.setId("123");
		requestObj.setName("abc");
		GreetingResponse responseObj = gs.qry(requestObj);
		System.out.println(responseObj.getHeader());
		System.out.println(responseObj.getBody());
	}
}
