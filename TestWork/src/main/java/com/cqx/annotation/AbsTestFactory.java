package com.cqx.annotation;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


public abstract class AbsTestFactory {
	
	/**
	 * 日志类
	 */
	private static MyLogger logger = MyLoggerFactory.getLogger(AbsTestFactory.class);

	/**
	 * 通过自定义注解进行测试
	 */
	public void test() {
		try {
			new MyTestFactory().trackTests(getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
