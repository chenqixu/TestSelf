package com.spring.test.bean;

import org.springframework.stereotype.Component;

@Component("GreetingDoImpl")
public class GreetingDoImpl implements GreetingDo{

	public void Do(String str) {
		System.out.println("[GreetingDoImpl]"+str);
	}
	
}
