package com.spring.test.bean;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class GreetingClient {
	public void client(){
		try{
			GreetingDo gd;
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
//			String[] names = wac.getBeanDefinitionNames();
//			for(int i=0;i<names.length;i++){
//				System.out.println(names[i]);
//			}
			gd = (GreetingDo)wac.getBean("GreetingDoImpl");
			gd.Do("test1");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
