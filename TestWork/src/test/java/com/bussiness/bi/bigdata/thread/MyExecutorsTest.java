package com.bussiness.bi.bigdata.thread;

import java.util.concurrent.TimeUnit;

import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTestFactory;
import com.cqx.annotation.MyTest.Test;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


public class MyExecutorsTest {
	
	private static MyLogger logger = MyLoggerFactory.getLogger(MyExecutorsTest.class);
	private MyExecutors et;
	
	@Before
	public void build() {
		et = new MyExecutors(5);
	}

	@Test
	public void testFixedThreadPool() {
		et.exec();
		et.exec();
		et.shutdown();
		et.exec();
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		et.stoptask();
	}
	
	@Test
	public void testScheduledThreadPool() {
		et.scheduleWithFixedDelay();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void testMoreThread() {
		et.exec();
		et.exec();
		et.exec();
		et.exec();
		et.exec();		
	}

	@Test
	public void testSingleExec() {
		et.init();
		et.execSingle();
		et.execSingle();
		et.execSingle();
	}

	@Test(status="start")
	public void testCallable() {
		et.init();
		et.futureGetSubmit();
		et.futureGetSubmit();
		et.futureGetSubmit();
	}
	
	public static void main(String[] args) throws Exception {
		new MyTestFactory().trackTests(MyExecutorsTest.class);
	}
}
