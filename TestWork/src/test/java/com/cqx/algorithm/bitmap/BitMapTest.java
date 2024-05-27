package com.cqx.algorithm.bitmap;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseCallableV1;
import com.cqx.common.utils.thread.ExecutorFactoryV1;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class BitMapTest {
    private static final Logger logger = LoggerFactory.getLogger(BitMapTest.class);

    @Test
    public void add() {
        BitMap bitmap = new BitMap(2000000000);
        int num = 171;
        bitmap.add(num);
        System.out.println("插入" + num + "成功");

        boolean isexsit = bitmap.contain(num);
        System.out.println(num + "是否存在:" + isexsit);

        bitmap.clear(num);
        isexsit = bitmap.contain(num);
        System.out.println(num + "是否存在:" + isexsit);

        System.out.println(bitmap.getBits());
    }

    @Test
    public void andNot() throws Exception {
        BitmapUv b1 = BitmapUv.newBitmapUv();
        for (long i = 1; i < 10; i++) {
            b1.add(i);
        }
        BitmapUv b2 = BitmapUv.newBitmapUv();
        for (long i = 5; i < 9; i++) {
            b2.add(i);
        }
        BitmapUv b3 = b1.andNot(b2);
        BitmapUvHelper.bitmapPrint(b3);
    }

    @Test
    public void threadCannelTest() {
        Thread thread = new Thread(new Runnable() {
            ExecutorFactoryV1 ef = ExecutorFactoryV1.newInstance(5);

            @Override
            public void run() {
                Random rd = new Random(System.currentTimeMillis());
                for (int i = 0; i < 10; i++) {
                    ef.submit(new TCT(rd.nextInt(10000)));
                }
                while (true) {
                    logger.info("{}", System.currentTimeMillis());
                    SleepUtil.sleepMilliSecond(500L);

                }
            }
        });
        thread.start();

        SleepUtil.sleepMilliSecond(5000L);
        thread.interrupt();
    }

    public class TCT extends BaseCallableV1 {
        int sleep;

        TCT(int sleep) {
            this.sleep = sleep;
        }

        @Override
        public void exec() throws Exception {
            logger.info("{} run...sleep={}", this, sleep);
            SleepUtil.sleepMilliSecond(sleep);
            logger.info("{} end.", this);
        }
    }
}