package com.cqx.common.utils.file;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseCallableV1;
import com.cqx.common.utils.thread.ExecutorFactoryV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发处理
 *
 * @author chenqixu
 */
public abstract class FileParallelRead implements IFileRead {
    private static final Logger logger = LoggerFactory.getLogger(FileParallelRead.class);
    private int parallel;
    private List<BlockingQueue<String>> bqList;
    private BlockingQueue<byte[]> allDataQueue;
    private ExecutorFactoryV1 executorFactory;
    private AtomicInteger lineNum;
    private AtomicInteger dealNum;

    public FileParallelRead(int parallel) {
        this.parallel = parallel;
        this.bqList = new ArrayList<>();
        this.allDataQueue = new LinkedBlockingQueue<>();
        this.executorFactory = ExecutorFactoryV1.newInstance(parallel + 1);
        for (int i = 0; i < parallel; i++) {
            this.bqList.add(new LinkedBlockingQueue<>());
            this.executorFactory.submit(new FPDeal(this.bqList.get(i)));
        }
        this.executorFactory.submit(new FPConsumer());
        this.lineNum = new AtomicInteger(0);
        this.dealNum = new AtomicInteger(0);
    }

    @Override
    public void run(String content) throws IOException {
        int _lineNum = lineNum.incrementAndGet();
        int mod = _lineNum % parallel;
        try {
            String _content = prepare(content);
            if (_content != null && _content.length() > 0) {
                bqList.get(mod).put(content);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void run(byte[] content) throws IOException {
    }

    @Override
    public void tearDown() throws IOException {
        while (lineNum.get() != dealNum.get()) {
            SleepUtil.sleepMilliSecond(20);
        }
        while (allDataQueue.size() > 0) {
            SleepUtil.sleepMilliSecond(20);
        }
        executorFactory.stop();
    }

    /**
     * 数据预处理，有需要就重写
     *
     * @param content
     * @return
     */
    public String prepare(String content) {
        return content;
    }

    public abstract byte[] parallelDeal(String content) throws Exception;

    public abstract void consumer(byte[] t) throws Exception;

    class FPDeal extends BaseCallableV1 {
        BlockingQueue<String> dataQueue;

        public FPDeal(BlockingQueue<String> dataQueue) {
            this.dataQueue = dataQueue;
        }

        @Override
        public void exec() throws Exception {
            String content = dataQueue.poll();
            if (content != null && content.length() > 0) {
                try {
                    allDataQueue.put(parallelDeal(content));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                dealNum.incrementAndGet();
            } else {
                SleepUtil.sleepMilliSecond(1);
            }
        }
    }

    class FPConsumer extends BaseCallableV1 {

        @Override
        public void exec() throws Exception {
            byte[] t;
            if ((t = allDataQueue.poll()) != null) {
                try {
                    consumer(t);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                SleepUtil.sleepMilliSecond(1);
            }
        }
    }
}
