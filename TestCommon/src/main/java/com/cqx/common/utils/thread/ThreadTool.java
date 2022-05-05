package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ThreadTool
 *
 * @author chenqixu
 */
public class ThreadTool {

    private static final Logger logger = LoggerFactory.getLogger(ThreadTool.class);
    private final AtomicLong run = new AtomicLong();
    private final AtomicLong end = new AtomicLong();
    private Queue<Thread> task = new LinkedBlockingQueue<>();
    private int parallel_num;
    private int scan_interval;

    public ThreadTool() {
        this(5, 50);//默认5个并发，50毫秒的扫描间隔
    }

    public ThreadTool(int parallel_num) {
        this(parallel_num, 50);//默认5个并发，50毫秒的扫描间隔
    }

    public ThreadTool(int parallel_num, int scan_interval) {
        if (parallel_num > 0) this.parallel_num = parallel_num;
        if (scan_interval > 0) this.scan_interval = scan_interval;
    }

    public void addTask(final Runnable runnable) {
        task.add(new Thread() {
            public void run() {
                run.incrementAndGet();
                runnable.run();
                end.incrementAndGet();
            }
        });
    }

    public void startTask() {
        while (task.size() > 0) {
            long running = run.get() - end.get();
            logger.debug("running {}，all_task：{}", running, task.size());
            if (running < parallel_num) {
                //启动5-cnt个线程
                long enable_submit_num = parallel_num - running;
                long s_num = 0;
                logger.debug("enable_submit_num {}", enable_submit_num);
                //找到NEW的，启动它
                Iterator<Thread> it = task.iterator();
                while (it.hasNext()) {
                    Thread t = it.next();
                    if (t.getState().equals(Thread.State.NEW)) {
                        if (s_num == enable_submit_num) break;
                        t.start();
                        s_num++;
                    } else if (t.getState().equals(Thread.State.TERMINATED)) {
                        //完成才会移除，所以这里已经实现了join过程
                        it.remove();
                    }
                }
            }
            SleepUtil.sleepMilliSecond(scan_interval);
        }
    }

    public void startTaskNotWait() {
        for (Thread t : task) {
            t.start();
        }
    }
}
