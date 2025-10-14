package com.bussiness.bi.bigdata.label.rm;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RM日志解析
 *
 * @author chenqixu
 */
public class LogsParse {
    private final Logger logger = LoggerFactory.getLogger(LogsParse.class);
    private final String fileName = "d:\\Work\\实时\\标签大宽表\\标签平台\\故障\\2025-07-22队列掉资源日志\\%s.log.2025-07-21";
    private final Object startLock = new Object();
    private final Object queueLock = new Object();
    // 线程安全的list
    private List<RmLogBean> logsList = new ArrayList<>();
    private final List<RmLogBean> syncList = Collections.synchronizedList(logsList);
    private volatile QueueBean cycletaskQueue = new QueueBean("cycletask", 10);
    private LinkedHashMap<String, TaskBean> taskBeanMap = new LinkedHashMap<>();
    // 线程安全的map
    private ConcurrentHashMap<String, TaskBean> taskStartMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TaskBean> taskEndMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TaskBean> readTaskStartMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TaskBean> readTaskRequestMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, TaskBean> readTaskReleaseMap = new ConcurrentHashMap<>();
    private List<Thread> threadList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        LogsParse logsParse = new LogsParse();
        logsParse.parse1("z2qrk");
        logsParse.parse1("c4wq");
        logsParse.parse_start();
        SleepUtil.sleepMilliSecond(2000L);
        logsParse.print();
    }

    public synchronized void addTask(String uuid, String task_id) throws InterruptedException {
        taskBeanMap.put(task_id, new TaskBean(uuid, task_id));
        this.notify();
    }

    public synchronized void startTask(String tag, String task_id, String queue, String queue_count) throws InterruptedException {
        TaskBean taskBean = taskBeanMap.get(task_id);
        if (taskBean == null) {
            this.wait();
        } else {
            synchronized (startLock) {
                taskBean.setRequest(true);
                taskStartMap.put(task_id, taskBean);
                cycletaskQueue.setCurrent_source(Integer.valueOf(queue_count));
                System.out.printf("[%s]-[资源扣除]-[task_id]%s %s %s 当前运行任务%s%n"
                        , tag, task_id, queue, queue_count, taskStartMap.size());
                startLock.notify();
            }
        }
    }

    public void releaseTask(String tag, String task_id, String queue, String queue_count) throws InterruptedException {
        synchronized (startLock) {
            TaskBean taskBean = taskBeanMap.get(task_id);
            if (taskBean == null || !taskBean.isRequest()) {
                startLock.wait();
            } else {
                taskBean.setRelease(true);
                taskStartMap.remove(task_id);
                taskEndMap.put(task_id, taskBean);
                cycletaskQueue.setCurrent_source(Integer.valueOf(queue_count));
                System.out.printf("[%s]-[资源释放]-[task_id]%s %s %s 当前运行任务%s%n"
                        , tag, task_id, queue, queue_count, taskStartMap.size());
            }
        }
    }

    public void parse_start() throws InterruptedException {
        for (Thread thread : threadList) {
            thread.start();
        }
    }

    public void print() {
        for (Thread thread : threadList) {
            System.out.printf("%s %s%n", thread, thread.isAlive());
        }

        Collections.sort(syncList, new Comparator<RmLogBean>() {
            @Override
            public int compare(RmLogBean o1, RmLogBean o2) {
                return Long.compare(o1.getTime(), o2.getTime());
            }
        });
        for (RmLogBean s : syncList) {
            System.out.println(s.getLogContent());
        }
    }

    private void parse_add(String tag, String content) {
        // 从redis获取nm列表 获取uuid和task_id
        if (content.contains("从redis获取nm列表")) {
            String[] arr = content.split(" ", -1);
            String uuid = arr[5];
            uuid = uuid.substring("[资源注册-".length(), uuid.length() - 1);
            String task_id = arr[6].substring("task_id:".length());
            try {
                addTask(uuid, task_id);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse_set(String tag, String content) {
        // 已经扣除
        if (content.contains("资源注册") && content.contains("] 队列剩余资源：")) {
            String[] arr = content.split(" ", -1);
            String task_id = arr[6].substring("task_id:".length());
            String queue = arr[7];
            queue = queue.substring(1, queue.length() - 1);
            String queue_count = arr[8].substring("队列剩余资源：".length());
            if (queue.contains("cycletask")) {
                try {
                    startTask(tag, task_id, queue, queue_count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (content.contains("资源注册") && content.contains("提交事务")) {
        }
        if (content.contains("[任务资源注册成功]")) {
        }
        if (content.contains("[尝试向nm提交任务]")) {
        }
        if (content.contains("[尝试向nm提交任务-结果]")) {
        }
        if (content.contains("[任务提交完成]")) {
        }
        // 已经释放
        if (content.contains("资源释放") && content.contains("] 队列剩余资源：")) {
            String[] arr = content.split(" ", -1);
            String task_id = arr[6].substring("task_id:".length());
            String queue = arr[7];
            queue = queue.substring(1, queue.length() - 1);
            String queue_count = arr[8].substring("队列剩余资源：".length());
            if (queue.contains("cycletask")) {
                try {
                    releaseTask(tag, task_id, queue, queue_count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (content.contains("资源释放") && content.contains("提交事务")) {
        }
        if (content.contains("[任务运行完成]")) {
        }

        // 剩余资源 带uuid
        // 业务处理（请求 OR 释放）都有带uuid
        // 请求

        // 释放

        // 解锁 不带uuid（需要优化）
    }

    private void readStart(String tag, String content) {
        // 从redis获取nm列表 获取uuid和task_id
        if (content.contains("从redis获取nm列表")) {
            String[] arr = content.split(" ", -1);
            String time = arr[1] + " " + arr[2];
            String uuid = arr[5];
            uuid = uuid.substring("[资源注册-".length(), uuid.length() - 1);
            String task_id = arr[6].substring("task_id:".length());

            TaskBean tb = readTaskStartMap.get(task_id);
//            logger.info("[readStart]{}, [tb]{}", task_id, tb);
//            System.out.printf("[readStart]%s, [tb]%s%n", task_id, tb);
            if (tb == null) {
                readTaskStartMap.put(task_id, new TaskBean(uuid, task_id));
            } else {// 可能是任务重做
//                throw new RuntimeException("!!!");
            }
        }
    }

    private void readRequest(String tag, String content) {
        // 已经扣除
        if (content.contains("资源注册") && content.contains("] 队列剩余资源：")) {
            String[] arr = content.split(" ", -1);
            String time = arr[1] + " " + arr[2];
            String task_id = arr[6].substring("task_id:".length());
            String queue = arr[7];
            queue = queue.substring(1, queue.length() - 1);
            String queue_count = arr[8].substring("队列剩余资源：".length());
            if (queue.contains("cycletask")) {
                while (true) {
                    TaskBean tb = readTaskStartMap.get(task_id);
                    if (tb != null) {
                        taskStartMap.put(task_id, tb);
                        tb.setQueueName(queue);
                        tb.setRequest(true);
                        synchronized (queueLock) {
                            cycletaskQueue.setCurrent_source(Integer.valueOf(queue_count));
                        }
                        String logContent = String.format("[%s %s]-[资源扣除ok]-[task_id]%s [queue]%s [queue_count]%s [当前运行任务]%s"
                                , tag, time, task_id, queue, queue_count, taskStartMap.size());
                        syncList.add(new RmLogBean(tag, time, logContent));
                        break;
                    } else {
                        SleepUtil.sleepMilliSecond(5L);
                    }
                }
            }
        }
    }

    private void readRelease(String tag, String content) {
        // 已经释放
        if (content.contains("资源释放") && content.contains("] 队列剩余资源：")) {
            String[] arr = content.split(" ", -1);
            String time = arr[1] + " " + arr[2];
            String task_id = arr[6].substring("task_id:".length());
            String queue = arr[7];
            queue = queue.substring(1, queue.length() - 1);
            String queue_count = arr[8].substring("队列剩余资源：".length());
            if (queue.contains("cycletask")) {
                while (true) {
                    TaskBean tb = readTaskStartMap.get(task_id);
                    if (tb != null) {
                        taskStartMap.remove(task_id);
                        tb.setRelease(true);
                        synchronized (queueLock) {
                            cycletaskQueue.setCurrent_source(Integer.valueOf(queue_count));
                        }
                        String logContent = String.format("[%s %s]-[资源释放ok]-[task_id]%s [queue]%s [queue_count]%s [当前运行任务]%s%n"
                                , tag, time, task_id, queue, queue_count, taskStartMap.size());
                        syncList.add(new RmLogBean(tag, time, logContent));
                        break;
                    } else {
                        SleepUtil.sleepMilliSecond(5L);
                    }
                }
            }
        }
    }

    public void parse(String tag) {
        Thread add = new Thread(() -> {
            FileUtil fileUtil = new FileUtil();
            try {
                fileUtil.setReader(String.format(fileName, tag));
                fileUtil.read(new FileCount() {
                    @Override
                    public void run(String content) throws IOException {
                        parse_add(tag, content);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileUtil.closeRead();
            }
        });

        Thread set = new Thread(() -> {
            FileUtil fileUtil = new FileUtil();
            try {
                fileUtil.setReader(String.format(fileName, tag));
                fileUtil.read(new FileCount() {
                    @Override
                    public void run(String content) throws IOException {
                        parse_set(tag, content);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileUtil.closeRead();
            }
        });

        threadList.add(add);
        threadList.add(set);
    }

    public void parse1(String tag) {
        Thread ps = new Thread(() -> {
            FileUtil fileUtil = new FileUtil();
            try {
                fileUtil.setReader(String.format(fileName, tag));
                fileUtil.read(new FileCount() {
                    @Override
                    public void run(String content) throws IOException {
                        readStart(tag, content);
                        readRequest(tag, content);
                        readRelease(tag, content);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fileUtil.closeRead();
            }
        });

        threadList.add(ps);
    }
}
