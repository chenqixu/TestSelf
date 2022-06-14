package com.bussiness.bi.bigdata.bean;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

public class CycleTest extends AbsTestFactory {

    private static final MyLogger logger = MyLoggerFactory.getLogger(CycleTest.class);
    private CycleUtils cycleUtils;

    public static void main(String[] args) {
        new CycleTest().test();
    }

    @Before
    public void testBuild() {
        cycleUtils = new CycleUtils();
    }

    @Test(status = "start")
    public void testSort() {
        cycleUtils.add("20181101214100");
        cycleUtils.add(null);
        cycleUtils.add("20181101204100");
//		cycleUtils.add("20181101194100");
        logger.info("" + cycleUtils.getSortFirst());
    }
}
