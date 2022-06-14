package com.bussiness.bi.bigdata.thread;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;

public class MyRunableTest extends AbsTestFactory {

	private MyRunable myRunable;

	@Before
	public void build() {
		myRunable = new MyRunable();
	}	

	@Test(status="start")
	public void testRun() {
		new Thread(myRunable).start();
	}

	public static void main(String[] args) throws Exception {
		new MyRunableTest().test();
	}
}
