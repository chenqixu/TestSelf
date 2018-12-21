package com.cqx.write;

import com.cqx.util.HdfsTool;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 持续写入Hdfs测试
 * 需要覆盖2个场景：1、租约过期；2、ClosedChannelException异常处理
 *
 * @author chenqixu
 */
public class WriteHdfs {

    private static final Logger logger = LoggerFactory.getLogger(WriteHdfs.class);
    //配置文件
    private Configuration conf = null;
    //分布式文件系统
    private FileSystem fs = null;
    //路径
    private String path = null;
    // 内容队列
    private BlockingQueue<byte[]> contentQueue = null;
    //线程池
    private ExecutorService executor = null;

    public WriteHdfs() throws IOException {
        addHook();
        init();
    }

    private void init() throws IOException {
        conf = HdfsTool.getLocalConf();
        fs = HdfsTool.getFileSystem(conf);
        contentQueue = new LinkedBlockingQueue<byte[]>();
        executor = Executors.newFixedThreadPool(2);
    }

    private void close() throws IOException {
        logger.info("close：{}", this.fs);
        HdfsTool.closeFileSystem(this.fs);
    }

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

    public void exec() throws IOException {
        FSDataOutputStream fsDataOutputStream = null;
        if (StringUtils.isEmpty(this.path))
            throw new NullPointerException("path is null.");
        boolean isexist = HdfsTool.isExist(this.fs, this.path);
        if (!isexist) {
            fsDataOutputStream = HdfsTool.createFile(this.fs, this.path);
        } else {
            fsDataOutputStream = HdfsTool.appendFile(this.fs, this.path);
        }
        if (fsDataOutputStream == null)
            throw new NullPointerException("fsDataOutputStream is null.");
        int limitcnt = 100000;//1GB 10000000
        WriteHdfsCallable writeHdfsThread = new WriteHdfsCallable(fsDataOutputStream);
        writeHdfsThread.setLimitcnt(limitcnt);
        executor.submit(writeHdfsThread);
        WriteProducerCallable writeProducerCallable = new WriteProducerCallable();
        writeProducerCallable.setLimitcnt(limitcnt + 1);
        executor.submit(writeProducerCallable);
        executor.shutdown();
    }

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
            logger.info("this：{}，cnt：{}", this, cnt);
            return this.cnt;
        }

        public void setLimitcnt(int limitcnt) {
            if (limitcnt < 1000) return;
            this.limitcnt = limitcnt;
        }
    }

    class WriteHdfsCallable implements Callable<Integer> {

        private FSDataOutputStream fsDataOutputStream;
        private int cnt;
        private int limitcnt = 1000;

        public WriteHdfsCallable(FSDataOutputStream fsDataOutputStream) {
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
                    if (this.cnt % 10000 == 0)
                        this.fsDataOutputStream.flush();
                }
            } finally {
                this.fsDataOutputStream.close();
            }
            logger.info("this：{}，cnt：{}", this, cnt);
            return this.cnt;
        }

        public void setLimitcnt(int limitcnt) {
            if (limitcnt < 1000) return;
            this.limitcnt = limitcnt;
        }
    }

    public static void main(String[] args) throws IOException {
        HdfsTool.setHadoopUser("edc_base");
        WriteHdfs writeHdfs = new WriteHdfs();
        writeHdfs.setPath("D:\\tmp\\data\\orcouputnull\\hdfsappend.txt");
        writeHdfs.exec();
    }
}
