package com.newland.bi.bigdata.config;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


public class ConfigTest {
	private static MyLogger logger = MyLoggerFactory.getLogger(ConfigTest.class);
	
	public void init() {
		BaseConfig.setIntValue(BaseConfig.BATCH_SIZE, 10);
		logger.info(BaseConfig.getStrValue(BaseConfig.BATCH_SIZE));
	}
	
	public static void main(String[] args) {
		new ConfigTest().init();
	}
}
