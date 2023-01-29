package com.cqx.finance;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.finance.bean.StockCompany;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StockServerTest {
    private StockServer stockServer;

    @Before
    public void setUp() throws Exception {
        stockServer = new StockServer("D:\\Soft\\Apache\\tomcat\\apache-tomcat-7.0.73-bat\\webapps\\data\\%s.txt");
        stockServer.addCompany(new StockCompany("000997", 15f));
    }

    @After
    public void tearDown() throws Exception {
        stockServer.stop();
    }

    @Test
    public void start() {
        stockServer.start();
        SleepUtil.sleepSecond(100);
    }
}