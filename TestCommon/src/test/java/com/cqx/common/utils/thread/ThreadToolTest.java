package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadToolTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadToolTest.class);

    private void sleep(int cnt) {
        int all = cnt;
        while (cnt > 0) {
            logger.info("sleep，all：{}，last：{}", all, cnt);
            SleepUtil.sleepSecond(1);
            cnt--;
        }
    }

    @Test
    public void startTask() {
//        ThreadTool threadTool = new ThreadTool(3);
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(10);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(1);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(2);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(3);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(4);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(2);
//            }
//        });
//        threadTool.addTask(new Runnable() {
//            @Override
//            public void run() {
//                sleep(4);
//            }
//        });
//        threadTool.startTask();


        ThreadTool threadTool = new ThreadTool(40);
        threadTool.addTask(new Runnable() {
            @Override
            public void run() {
                sleep(4);
            }
        });
        threadTool.addTask(new Runnable() {
            @Override
            public void run() {
                sleep(1);
            }
        });
        threadTool.addTask(new Runnable() {
            @Override
            public void run() {
                sleep(2);
            }
        });
        threadTool.addTask(new Runnable() {
            @Override
            public void run() {
                sleep(3);
            }
        });
        threadTool.startTask();
    }
}