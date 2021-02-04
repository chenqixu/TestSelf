package com.cqx.common.model.commit;

import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 数据提交器
 *
 * @author chenqixu
 */
public class BatchCommit<T> {
    private static final Logger logger = LoggerFactory.getLogger(BatchCommit.class);
    private List<T> currentList;
    private LinkedBlockingQueue<T> queue;
    private int submit_timeout;
    private int submit_cnt;
    private ICallBack<T> iCallBack;
    private long commitTime;
    private Thread thread;
    private BatchCommitRunnable runnable;

    public BatchCommit(int submit_timeout, int submit_cnt, int queue_limit, ICallBack<T> iCallBack) {
        this.submit_timeout = submit_timeout;
        this.submit_cnt = submit_cnt;
        this.iCallBack = iCallBack;
        commitTime = System.currentTimeMillis();
        currentList = new ArrayList<>();
        this.queue = new LinkedBlockingQueue<>(queue_limit);
        runnable = new BatchCommitRunnable();
        thread = new Thread(runnable);
        thread.start();
        logger.info("提交器启动……");
    }

    public void close() {
        logger.info("准备停止提交器……");
        if (thread != null && runnable != null) {
            runnable.stop();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void add(T t) throws InterruptedException {
        if (!queue.offer(t, 1, TimeUnit.SECONDS)) {
            logger.warn("警告：队列达到上限，正在抛数据，数据库性能不足！");
        }
    }

    public interface ICallBack<T> {
        void call(List<T> list);
    }

    private class BatchCommitRunnable extends BaseRunable {

        @Override
        public void exec() throws Exception {
            if ((currentList.size() >= submit_cnt)
                    ||
                    (System.currentTimeMillis() >= commitTime)) {
                if (currentList.size() > 0) {
                    iCallBack.call(currentList);
                    currentList.clear();
                }
                commitTime = System.currentTimeMillis() + submit_timeout;
            }
            T t = queue.poll(1, TimeUnit.SECONDS);
            if (t != null) {
                currentList.add(t);
            }
        }

        @Override
        public void lastExec() throws Exception {
            T t;
            while ((t = queue.poll(1, TimeUnit.SECONDS)) != null) {
                currentList.add(t);
            }
            if (currentList.size() > 0) {
                iCallBack.call(currentList);
                currentList.clear();
            }
            commitTime = System.currentTimeMillis() + submit_timeout;
        }
    }
}
