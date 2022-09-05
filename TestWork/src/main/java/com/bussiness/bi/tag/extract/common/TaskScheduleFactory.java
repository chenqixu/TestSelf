package com.bussiness.bi.tag.extract.common;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskScheduleFactory
 *
 * @author chenqixu
 */
public class TaskScheduleFactory {
    private List<Thread> threadList = new ArrayList<>();

    public void addTaskSchedule(TaskSchedule taskSchedule) {
        threadList.add(new Thread(taskSchedule));
    }

    public void startAndWait() throws InterruptedException {
        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
    }
}
