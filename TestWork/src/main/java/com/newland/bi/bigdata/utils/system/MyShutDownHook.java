package com.newland.bi.bigdata.utils.system;

import com.cqx.concurrent.IBolt;
import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;
import com.newland.bi.bigdata.time.TimeHelper;
import com.newland.bi.bigdata.utils.SleepUtils;

/**
 * 我的jvm钩子
 *
 * @author chenqixu
 */
public class MyShutDownHook extends IBolt {

    private static Logger logger = LogInfoFactory.getInstance(MyShutDownHook.class);

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
