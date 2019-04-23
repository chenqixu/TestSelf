package com.cqx.write;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.hdfs.bean.HdfsToolBean;
import com.cqx.util.HdfsTool;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.*;

/**
 * 持续写入Hdfs测试
 * 需要覆盖2个场景：1、租约过期；2、ClosedChannelException异常处理
 *
 * @author chenqixu
 */
public class WriteHdfs {

    private static final Logger logger = LoggerFactory.getLogger(WriteHdfs.class);
    //任务个数
    private static final int TASK_CNT = 2;
    //配置文件
    private Configuration conf = null;
    //分布式文件系统
    private FileSystem fs = null;
    //路径
    private String path = null;
    //内容队列
    private BlockingQueue<byte[]> contentQueue = null;
    //线程池
    private ExecutorService executor = null;
    //用来判断任务是否完成
    private CountDownLatch countDownLatch = null;
    //参数
    private HdfsToolBean hdfsToolBean;

    /**
     * 构造
     *
     * @throws IOException
     */
    public WriteHdfs(HdfsToolBean hdfsToolBean) throws IOException {
        this.hdfsToolBean = hdfsToolBean;
        addHook();
        init();
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    private void init() throws IOException {
        conf = HdfsTool.getLocalConf(hdfsToolBean.getConf_path());
        fs = HdfsTool.getFileSystem(conf);
        contentQueue = new LinkedBlockingQueue<byte[]>();
        executor = Executors.newFixedThreadPool(TASK_CNT);
        countDownLatch = new CountDownLatch(TASK_CNT);
    }

    /**
     * 续写测试
     *
     * @param file_path 文件名
     */
    public void append(String file_path, int seq) {
        logger.info("append：{}", file_path);
        OutputStream fsDataOutputStream = null;
        try {
            boolean isexist = HdfsTool.isExist(this.fs, file_path);
            logger.info("file：{}，isexist：{}", file_path, isexist);
            if (isexist) {
                fsDataOutputStream = HdfsTool.appendFile(this.fs, file_path);
                logger.info("appendFile：{}", file_path);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.info("file：{}，重试：{}", file_path, seq - 1);
            if (seq > 0) {
                append(file_path, seq - 1);
            }
        } finally {
            if (fsDataOutputStream != null) {
                try {
                    fsDataOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 续写但不关闭测试
     *
     * @param file_path 文件名
     */
    public void appendNotClose(String file_path) {
        logger.info("appendNotClose：{}", file_path);
        FSDataOutputStream fsDataOutputStream = null;
        try {
            boolean isexist = HdfsTool.isExist(this.fs, file_path);
            logger.info("file：{}，isexist：{}", file_path, isexist);
            if (isexist) {
                HdfsTool.appendFile(this.fs, file_path);
                logger.info("appendNotCloseFile：{}", file_path);
                SleepUtil.sleepSecond(30);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 关闭
     *
     * @throws IOException
     */
    public void close() throws IOException {
        logger.info("close：{}", this.fs);
        HdfsTool.closeFileSystem(this.fs);
    }

    /**
     * 绑定钩子
     */
    private void addHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread("relase-shutdown-hook" + this) {
                    @Override
                    public void run() {
                        // 释放连接池资源
                        logger.info("hook-release：{}", this);
                        try {
                            close();
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
        );
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 执行写入测试，含两个线程：1、生产者；2、消费者；
     *
     * @throws IOException
     */
    public void exec() throws Exception {
        OutputStream fsDataOutputStream = null;
        if (StringUtils.isEmpty(this.path))
            throw new NullPointerException("path is null.");
        //判断文件是否存在
        boolean isexist = HdfsTool.isExist(this.fs, this.path);
        if (!isexist) {
            fsDataOutputStream = HdfsTool.createFile(this.fs, this.path);
        } else {
            fsDataOutputStream = HdfsTool.appendFile(this.fs, this.path);
        }
        if (fsDataOutputStream == null)
            throw new NullPointerException("fsDataOutputStream is null.");
        int limitcnt = hdfsToolBean.getLimitcnt();//1GB 10000000
        //消费者
        WriteHdfsCallable writeHdfsThread = new WriteHdfsCallable(fsDataOutputStream);
        writeHdfsThread.setLimitcnt(limitcnt);
        executor.submit(writeHdfsThread);
        //生产者
        WriteProducerCallable writeProducerCallable = new WriteProducerCallable();
        writeProducerCallable.setLimitcnt(limitcnt + 1);
        executor.submit(writeProducerCallable);
        //等待线程执行完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        executor.shutdown();
    }

    /**
     * 生产者
     */
    class WriteProducerCallable implements Callable<Integer> {

        private int cnt;
        private int limitcnt = 1000;
        private String separator = System.getProperty("line.separator");
        /**
         * 1000行100KB
         */
        private byte[] value = ("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" + separator).getBytes();

        @Override
        public Integer call() throws Exception {
            while (this.cnt < this.limitcnt) {
                contentQueue.put(this.value);
                this.cnt++;
            }
            logger.info("Producer，cnt：{}", cnt);
            countDownLatch.countDown();
            return this.cnt;
        }

        public void setLimitcnt(int limitcnt) {
            if (limitcnt < 1000) return;
            this.limitcnt = limitcnt;
        }
    }

    /**
     * 消费者
     */
    class WriteHdfsCallable implements Callable<Integer> {

        private OutputStream fsDataOutputStream;
        private int cnt;
        private int limitcnt = 1000;

        public WriteHdfsCallable(OutputStream fsDataOutputStream) {
            this.fsDataOutputStream = fsDataOutputStream;
        }

        @Override
        public Integer call() throws Exception {
            try {
                while (this.cnt < this.limitcnt) {
                    byte[] value = null;
                    if ((value = contentQueue.poll()) != null) {
                        this.fsDataOutputStream.write(value);
                        this.cnt++;
                    }
                    if (this.cnt % 10000 == 0) {
                        if (hdfsToolBean.isFlush())
                            this.fsDataOutputStream.flush();
                        if (this.cnt != 0)
                            logger.info("write，cnt：{}", cnt);
                    }
                }
            } finally {
                this.fsDataOutputStream.close();
            }
            logger.info("write end，cnt：{}", this, cnt);
            countDownLatch.countDown();
            return this.cnt;
        }

        public void setLimitcnt(int limitcnt) {
            if (limitcnt < 1000) return;
            this.limitcnt = limitcnt;
        }
    }
}
