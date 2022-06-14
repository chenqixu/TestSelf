package com.cqx.etlcollect;

import com.cqx.etlcollect.bean.FileBean;
import com.cqx.etlcollect.bean.TaskBean;
import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 调度
 * <pre>
 *     从扫描接收数据，生成采集任务，等待采集端连上来取
 * </pre>
 *
 * @author chenqixu
 */
public class CollectDispatch {

    public static final int MAX_FTP_QUEUE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(CollectDispatch.class);
    private Map<String, BlockingQueue<FileBean>> ftpQueue = new HashMap<>();
    private Map<String, CreateTask> createTaskMonitor = new HashMap<>();
    private BlockingQueue<TaskBean> taskQueue = new LinkedBlockingQueue<>();
    private ReentrantLock putLock = new ReentrantLock();

    private BlockingQueue<FileBean> getQueueByFtpHost(String ftpHost) {
        BlockingQueue<FileBean> blockingQueue;
        putLock.lock();
        try {
            blockingQueue = ftpQueue.get(ftpHost);
            if (blockingQueue == null) {
                blockingQueue = new LinkedBlockingQueue<>();
                CreateTask createTask = new CreateTask(blockingQueue);
                createTaskMonitor.put(ftpHost, createTask);
                new Thread(createTask).start();
            }
        } finally {
            putLock.unlock();
        }
        return blockingQueue;
    }

    public void put(FileBean fileBean) throws InterruptedException {
        String ftpHost = fileBean.getFtpHost();
        BlockingQueue<FileBean> blockingQueue = getQueueByFtpHost(ftpHost);
        if (blockingQueue.size() < MAX_FTP_QUEUE) {
            blockingQueue.put(fileBean);
        } else {
            logger.debug("{}队列满了", ftpHost);
        }
    }

    /**
     * 派发任务
     */
    public TaskMonitor getTask() {
        TaskBean taskBean = taskQueue.poll();
        return new TaskMonitor(taskBean);
    }

    /**
     * 创建任务
     */
    class CreateTask implements Runnable {

        private BlockingQueue<FileBean> blockingQueue;
        private boolean flag = true;

        public CreateTask(BlockingQueue<FileBean> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            while (flag) {
                FileBean fileBean;
                while ((fileBean = blockingQueue.poll()) != null) {
                    TaskBean taskBean = new TaskBean();
                    taskBean.setTaskObj(fileBean);
                    try {
                        taskQueue.put(taskBean);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                SleepUtils.sleepMilliSecond(1);
            }
        }
    }
}
