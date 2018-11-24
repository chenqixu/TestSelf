package com.newland.bi.bigdata.config;

import com.cqx.process.LogInfoFactory;

public class ConfigTest {
	private LogInfoFactory logger = LogInfoFactory.getInstance(ConfigTest.class);
	
	public void init() {
		BaseConfig.setIntValue(BaseConfig.BATCH_SIZE, 10);
		logger.info(BaseConfig.getStrValue(BaseConfig.BATCH_SIZE));
	}
	
	public static void main(String[] args) {
		new ConfigTest().init();
	}
}
