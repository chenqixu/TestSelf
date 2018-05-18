package com.spring.test.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Common {
	@Value("#{configProperties['ttuid']}")
	private String ttuid;
	public String getTtuid() {
		return ttuid;
	}
}
