package com.cqx.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


/**
 * 自定义注解工厂
 * 
 * @author chenqixu
 *
 */
public class MyTestFactory {

	private static MyLogger logger = MyLoggerFactory.getLogger(MyTestFactory.class);
	private List<String> useTests;
	public static final String START_STATUS = "start";
	public static final String STOP_STATUS = "stop";

	public MyTestFactory() {
		useTests = new ArrayList<String>();
		Collections.addAll(useTests, "start", "stop");
	}
	
	/**
	 * 在Test前进行初始化操作
	 * @param cl
	 * @param obj
	 * @throws Exception
	 */
	private void build(Class<?> cl, Object obj) throws Exception {
		for (Method m : cl.getDeclaredMethods()) {
			logger.debug("method：" + m.getName());
			Before bf = m.getAnnotation(Before.class);
			if (bf != null) {
				m.invoke(obj);
				break;
			}
		}
	}
	
	/**
	 * 在build操作后进行测试
	 * @param cl
	 * @param obj
	 * @throws Exception
	 */
	private void test(Class<?> cl, Object obj) throws Exception {
		for (Method m : cl.getDeclaredMethods()) {
			logger.debug("method：" + m.getName());
			Test mt = m.getAnnotation(Test.class);	
			if (mt != null) {
				logger.debug("Test status：" + mt.status());
				switch (mt.status()) {
				case START_STATUS:
					logger.info("Test Function {} start", m.getName());
					// 创建类并测试方法
					m.invoke(obj);
					logger.info("Test Function {} ok", m.getName());
					break;
				case STOP_STATUS:
					break;
				default:
					break;
				}
			}
		}
	}
	
	/**
	 * 先build，再test
	 * @param cl
	 * @throws Exception
	 */
	public void trackTests(Class<?> cl) throws Exception {
		// 构造
		Object obj = cl.newInstance();
		// 初始化
		build(cl, obj);
		// 测试
		test(cl, obj);
	}
}
