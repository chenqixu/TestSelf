package com.bussiness.bi.bigdata.utils.system;

import com.bussiness.bi.bigdata.utils.SleepUtils;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.concurrent.IBolt;


import com.bussiness.bi.bigdata.time.TimeHelper;

/**
 * 我的jvm钩子
 *
 * @author chenqixu
 */
public class MyShutDownHook extends IBolt {

    private static MyLogger logger = MyLoggerFactory.getLogger(MyShutDownHook.class);

    @Override
    public void init() {
        Runtime.getRuntime().addShutdownHook(
                new Thread("relase-shutdown-hook" + this) {
                    @Override
                    public void run() {
                        // 释放连接池资源
                        System.out.println("release：" + this);
                    }
                }
        );
    }

    @Override
    public void exec() {
        logger.info("getNow：{}", TimeHelper.getNow());
        SleepUtils.sleepMilliSecond(500);
    }
}
