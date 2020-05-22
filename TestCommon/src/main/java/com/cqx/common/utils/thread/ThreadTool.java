package com.cqx.common.utils.thread;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ThreadTool
 *
 * @author chenqixu
 */
public class ThreadTool {

    private static final Logger logger = LoggerFactory.getLogger(ThreadTool.class);
    private List<Thread> taskList = new ArrayList<>();
    private int parallel_num;

    public ThreadTool() {
        this(5);//默认5个并发
    }

    public ThreadTool(int parallel_num) {
        if (parallel_num > 0) this.parallel_num = parallel_num;
    }

    public void addTask(Thread thread) {
        taskList.add(thread);
    }

    public void addTask(Runnable runnable) {
        addTask(new Thread(runnable));
    }

    public void startTask() {
        final AtomicLong run = new AtomicLong();
        final AtomicLong end = new AtomicLong();
        while (taskList.size() > 0) {
            long running = run.get() - end.get();
            logger.info("running {}，all_task：{}", running, taskList.size());
            if (running < parallel_num) {
                //启动5-cnt个线程
                long start_num = parallel_num - running;
                long s_num = 0;
                logger.info("start_num {}", start_num);
                //找到NEW的，启动它
                Iterator<Thread> it = taskList.iterator();
                while (it.hasNext()) {
                    Thread t = it.next();
                    if (t.getState().equals(Thread.State.NEW)) {
                        if (s_num == start_num) break;
                        t.start();
                        s_num++;
                    } else if (t.getState().equals(Thread.State.TERMINATED)) {
                        it.remove();
                    }
                }
            }
            SleepUtil.sleepMilliSecond(50);
        }
    }
}
