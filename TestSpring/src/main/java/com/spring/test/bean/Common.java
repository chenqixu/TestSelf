package com.spring.test.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("Common")
public class Common {
	@Value("${DefaultReqSource}")
    private String DefaultReqSource;

	public String getDefaultReqSource() {
		return DefaultReqSource;
	}

	public void setDefaultReqSource(String defaultReqSource) {
		DefaultReqSource = defaultReqSource;
	}
}
