package com.bussiness.bi.bigdata.time;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TimeCostUtilTest {

    private static Logger logger = LoggerFactory.getLogger(TimeCostUtilTest.class);
    private static String str = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,";
    TimeCostUtil timeCostUtil2 = new TimeCostUtil(true);
    private Random random = new Random();

    @Test
    public void time() {
        for (int j = 0; j < 10; j++) {
            TimeCostUtil timeCostUtil = new TimeCostUtil();
            timeCostUtil.start();
            for (int i = 0; i < 100; i++) {
                //do1
                do1();
                //do2，只计算do2花费
                do2();
                //do3
                do3();
            }
            timeCostUtil.stop();
            logger.info("timeCostUtil2：{}，all cost：{}", timeCostUtil2.getIncrementCost(), timeCostUtil.getCost());
            timeCostUtil2.resetIncrementCost();
        }
    }

    private void do1() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            sb.append(str);
        }
        int cnt = 0;
        String[] arr = sb.toString().split(",", -1);
        for (String str : arr) {
            cnt++;
        }
    }

    private void do2() {
        timeCostUtil2.start();
        int ii = random.nextInt(5000);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ii; i++) {
            sb.append(str);
        }
        int cnt = 0;
        String[] arr = sb.toString().split(",", -1);
        for (String str : arr) {
            cnt++;
        }
        timeCostUtil2.stopAndIncrementCost();
    }

    private void do3() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            sb.append(str);
        }
        int cnt = 0;
        String[] arr = sb.toString().split(",", -1);
        for (String str : arr) {
            cnt++;
        }
    }

    @Test
    public void stopAndIncrementCost() {
        timeCostUtil2.stopAndIncrementCost();
        System.out.println(timeCostUtil2.getIncrementCost());
    }
}