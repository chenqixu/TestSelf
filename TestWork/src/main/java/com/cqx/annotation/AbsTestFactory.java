package com.cqx.annotation;

import com.cqx.process.LogInfoFactory;

public abstract class AbsTestFactory {
	
	/**
	 * 日志类
	 */
	protected static LogInfoFactory logger = LogInfoFactory.getInstance(AbsTestFactory.class);

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
