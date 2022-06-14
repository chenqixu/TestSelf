package com.bussiness.bi.bigdata.txt;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件列表落地
 *
 * @author chenqixu
 */
public class FileListLog {

    private static final Logger logger = LoggerFactory.getLogger(FileListLog.class);
    private final Object writeLock = new Object();
    private FileUtil fileUtil = new FileUtil();
    private String format;
    private String current;
    private String path;
    private FileMonitor fileMonitor;

    public FileListLog(String path, String format) throws FileNotFoundException, UnsupportedEncodingException {
        this.format = format;
        this.path = path;
        //获取当前小时
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        current = simpleDateFormat.format(new Date());
        fileUtil.createFile(getFileName(current), "UTF-8");
    }

    private String getFileName(String current) {
        return path + current + ".txt";
    }

    /**
     * 上游传下来的文件名
     *
     * @param filename
     */
    public void exec(String filename) {
        synchronized (writeLock) {
            fileUtil.write(filename + "\r\n");
        }
    }

    /**
     * 实时监控，确保1小时切换1次
     */
    public void startMonitor() {
        fileMonitor = new FileMonitor();
        fileMonitor.start();
    }

    public void stopMonitor() throws InterruptedException {
        if (fileMonitor != null) {
            fileMonitor.stopMonitor();
            fileMonitor.join();
        }
    }

    class FileMonitor extends Thread {

        private boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                //获取当前小时
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                String _current = simpleDateFormat.format(new Date());
                logger.info("_current：{}，current：{}", _current, current);
                if (!current.equals(_current)) {//小时切换
                    synchronized (writeLock) {
                        fileUtil.closeWrite();
                        try {
                            fileUtil.createFile(getFileName(_current), "UTF-8");
                            current = _current;
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            fileUtil.closeWrite();
                        }
                    }
                }
                SleepUtil.sleepMilliSecond(500);
            }
            if (fileUtil != null) fileUtil.closeWrite();
        }

        public void stopMonitor() {
            flag = false;
        }
    }
}
