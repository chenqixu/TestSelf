package com.newland.bi.bigdata.file;

import com.newland.bi.bigdata.utils.SleepUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;

/**
 * 进程锁
 *
 * @author chenqixu
 */
public class ProcessLock {

    private static final int SLEEP_TIME = 5000;
    private FileLock lock = null;
    private FileOutputStream outStream = null;

    /**
     * 在path路径创建tmp.lock文件锁
     *
     * @param path
     * @return
     */
    public boolean tryLock(String path) {
        boolean getLock = false;
        File lockFile = new File(path + "tmp.lock");
        try {
            outStream = new FileOutputStream(lockFile);
            FileChannel channel = outStream.getChannel();
            // 尝试获取文件锁
            lock = channel.tryLock();
            if (lock != null) {
                getLock = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getLock;
    }

    /**
     * 允许在${retryCnt}次数内尝试获取文件锁，间隔${SLEEP_TIME}
     *
     * @param path
     * @param retryCnt 重试次数
     * @return
     */
    public boolean tryLock(String path, int retryCnt) throws Exception {
        int _retryCnt = retryCnt;
        boolean getLock = false;
        while (!getLock && _retryCnt > 0) {
            getLock = tryLock(path);
            if (!getLock) {
                _retryCnt--;
                System.out.println("get Lock fail，retryCnt：" + _retryCnt);
                if (_retryCnt > 0) {
                    SleepUtils.sleepMilliSecond(SLEEP_TIME);
                } else {
                    throw new Exception("获取锁失败，已被其他进程占用");
                }
            }
        }
        return getLock;
    }

    /**
     * 释放文件锁
     */
    public void releaseLock() {
        if (null != lock) {
            try {
                System.out.println("Release The Lock " + new Date().toString());
                lock.release();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != outStream) {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        releaseLock();
    }
}
