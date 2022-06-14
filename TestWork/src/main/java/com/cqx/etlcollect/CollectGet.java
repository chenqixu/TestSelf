package com.cqx.etlcollect;

import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模拟采集
 * <pre>
 *      从调度获取采集任务，执行采集任务，并反馈给调度
 * </pre>
 *
 * @author chenqixu
 */
public class CollectGet implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CollectGet.class);
    private CollectDispatch collectDispatch;
    private volatile boolean flag = true;

    public CollectGet(CollectDispatch collectDispatch) {
        this.collectDispatch = collectDispatch;
    }

    @Override
    public void run() {
        while (flag) {
            TaskMonitor taskMonitor;
            while ((taskMonitor = collectDispatch.getTask()) != null) {
                // 采集

                // 分发

                // 完成,反馈给调度

            }
            SleepUtils.sleepMilliSecond(1);
        }
    }

    public void stop() {
        flag = false;
    }
}
