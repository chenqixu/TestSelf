package com.cqx.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqx.jstorm.netty.bean.DiscardBean;
import com.cqx.jstorm.netty.client.DiscardClient;
import com.cqx.jstorm.util.Utils;

public class Heart {
	
	private static final Logger logger = LoggerFactory.getLogger(Heart.class);
			
	public static void hear(String threadName) {
		DiscardBean discardBean = new DiscardBean(1, 1234567l, DiscardBean.buildDataBean(threadName, 1, Utils.getNow()));
		try {
			logger.info("###start heart###");
			new DiscardClient("127.0.0.1", 18888).heart(discardBean);
			logger.info("###end heart###");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
