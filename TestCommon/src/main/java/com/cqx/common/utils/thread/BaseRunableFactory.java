package com.cqx.common.utils.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseRunableFactory
 *
 * @author chenqixu
 */
public class BaseRunableFactory {
    private final static BaseRunableFactory baseRunableFactory = new BaseRunableFactory();
    private List<BaseRunable> tasks = new ArrayList<>();
    private List<Thread> threads = new ArrayList<>();

    private BaseRunableFactory() {
    }

    public static BaseRunableFactory newInstance() {
        return baseRunableFactory;
    }

    public BaseRunableFactory addTask(BaseRunable task) {
        tasks.add(task);
        threads.add(new Thread(task));
        return this;
    }

    public BaseRunableFactory startTask() {
        for (Thread thread : threads) {
            thread.start();
        }
        return this;
    }

    public void stopTask() throws InterruptedException {
        for (BaseRunable baseRunable : tasks) {
            baseRunable.stop();
        }
        waitTask();
    }

    public void waitTask() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
