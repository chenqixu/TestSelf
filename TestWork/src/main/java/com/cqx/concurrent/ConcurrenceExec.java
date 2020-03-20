package com.cqx.concurrent;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


import com.newland.bi.bigdata.utils.SleepUtils;

/**
 * 高并发框架
 *
 * @author chenqixu
 */
public class ConcurrenceExec {

    private static MyLogger logger = MyLoggerFactory.getLogger(ConcurrenceExec.class);
    private IBolt iBolt;
    /**
     * 默认1并发
     */
    private int parallelism = 1;
    private String id;

    public ConcurrenceExec() {
    }

    public void setBolt(String id, IBolt iBolt, int parallelism) {
        this.id = id;
        this.iBolt = iBolt;
        this.parallelism = parallelism;
    }

    public void start() throws CloneNotSupportedException {
        if (id != null && iBolt != null) {
            for (int i = 0; i < parallelism; i++) {
                IBolt _iBolt = (IBolt) iBolt.clone();
                new Thread(new ExecRunnable(_iBolt)).start();
            }
        }
    }

    class ExecRunnable implements Runnable {

        private IBolt execiBolt;

        public ExecRunnable(IBolt execiBolt) {
            this.execiBolt = execiBolt;
//            logger.info("execiBolt：{}", execiBolt);
            execiBolt.init();
        }

        @Override
        public void run() {
            for (; ; ) {
                execiBolt.exec();
            }
        }
    }
}
