package com.cqx.thread;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseThread;

import java.util.Random;

/**
 * StepOneThread
 *
 * @author chenqixu
 */
public class StepOneThread extends BaseThread {

    @Override
    public void run() {
        logger.info("Start running.");
        Random random = new Random();
        long sleep = random.nextInt(2000);
        sleep = 191;
        logger.info("Ready to sleep for {} ms.", sleep);
        SleepUtil.sleepMilliSecond(sleep);
//        if (sleep % 2 == 0) setError(String.format("%s is an even numbers.", sleep));//even
        if (!(sleep % 2 == 0)) setError(String.format("%s is an odd numbers.", sleep));//odd
        logger.info("Run complete.");
    }
}
