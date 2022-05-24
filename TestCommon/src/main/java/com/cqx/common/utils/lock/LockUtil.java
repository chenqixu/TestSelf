package com.cqx.common.utils.lock;

import com.codahale.metrics.Meter;
import com.cqx.common.metric.MetricUtils;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * 锁性能测试
 *
 * @author chenqixu
 */
public class LockUtil extends FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(LockUtil.class);
    private final Object writeLock = new Object();

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        if (args.length != 4) {
            logger.info("参数需要4个，文件名，写入次数，计数器打印间隔，同步类型");
            System.exit(-1);
        }
        String fileName = args[0];
        int writeCnt = Integer.valueOf(args[1]);
        int logCnt = Integer.valueOf(args[2]);
        int synType = Integer.valueOf(args[3]);
        logger.info("参数：文件名: {}，写入次数: {}，计数器打印间隔: {}，同步类型: {}"
                , fileName, writeCnt, logCnt, synType);

        Meter producer = MetricUtils.getMeter("producer");
        MetricUtils.build(5, TimeUnit.SECONDS);

        String content = "1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(content);
        }
        String newContent = sb.toString();
        int newContentLen = newContent.getBytes().length;

        LockUtil lockUtil = new LockUtil();
        lockUtil.createFile(fileName);

        ThreadTool threadTool = new ThreadTool();
        if (synType == 1) {
            for (int j = 0; j < 5; j++) {
                threadTool.addTask(new Runnable() {
                    @Override
                    public void run() {
                        long cnt = 0L;
                        while (cnt < writeCnt) {
                            lockUtil.write1(newContent);
                            producer.mark(newContentLen);
                            cnt++;
                            if (cnt % logCnt == 0) {
                                logger.info("cnt: {}", cnt);
                            }
                        }
                    }
                });
            }
        } else {
            for (int j = 0; j < 5; j++) {
                threadTool.addTask(new Runnable() {
                    @Override
                    public void run() {
                        long cnt = 0L;
                        while (cnt < writeCnt) {
                            lockUtil.write2(newContent);
                            producer.mark(newContentLen);
                            cnt++;
                            if (cnt % logCnt == 0) {
                                logger.info("cnt: {}", cnt);
                            }
                        }
                    }
                });
            }
        }
        threadTool.startTask();
    }

    public synchronized void write1(String str) {
        try {
            writer.write(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void write2(String str) {
        synchronized (writeLock) {
            try {
                writer.write(str);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
