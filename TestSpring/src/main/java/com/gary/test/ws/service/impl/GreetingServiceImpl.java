package com.gary.test.ws.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.gary.test.deal.DownloadThread;
import com.gary.test.ws.service.GreetingService;
import com.spring.test.bean.Common;
import com.spring.test.bean.GreetingClient;
import com.spring.test.bean.GreetingRequest;
import com.spring.test.bean.GreetingResponse;

@WebService(endpointInterface = "com.gary.test.ws.service.GreetingService",serviceName="GreetingService",targetNamespace="http://service.ws.test.gary.com/") 
public class GreetingServiceImpl implements GreetingService {
	
	@Autowired
	private Common configInfo;

	public String greeting(String userName) {
		System.out.println("com.gary.test.ws.service.GreetingService:"+userName);
		GreetingClient gc = new GreetingClient();
		gc.client();
		// 使用注入获取配置文件，需要Spring-beans 3.0.5以上版本
		System.out.println("[Common]"+configInfo.getDefaultReqSource());
		
		// 使用springframework的Resource和PropertiesLoaderUtils来加载配置文件，Spring-beans 2.5.6就可以使用
		Resource resource = new ClassPathResource("/resources/config/common.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			System.out.println("[props.getProperty]"+props.getProperty("DefaultReqSource"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("异步调用线程 begin");
		// 异步调用线程
		new DownloadThread().start();
		System.out.println("异步调用线程 end");
		
		return "Hello " + userName + ", currentTime is " + Calendar.getInstance().getTime(); 
	}
	
	public String next(String str) {
		return "str";
	}

	public GreetingResponse qry(GreetingRequest requestObj) {
		GreetingResponse gr = new GreetingResponse();
		gr.setHeader("success");
		gr.setBody(requestObj.getId()+"-"+requestObj.getName());		
		return gr;
	}
}
