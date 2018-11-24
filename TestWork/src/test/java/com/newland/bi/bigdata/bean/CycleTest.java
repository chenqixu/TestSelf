package com.newland.bi.bigdata.bean;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;

public class CycleTest extends AbsTestFactory {	
	
	CycleUtils cycleUtils;
	
	@Before
	public void testBuild() {
		cycleUtils = new CycleUtils();
	}
	
	@Test(status="start")
	public void testSort() {
		cycleUtils.add("20181101214100");
		cycleUtils.add(null);
		cycleUtils.add("20181101204100");
//		cycleUtils.add("20181101194100");
		logger.info(""+cycleUtils.getSortFirst());
	}
	
	public static void main(String[] args) {
		new CycleTest().test();
	}
}
