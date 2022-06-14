package com.bussiness.bi.bigdata.streamfile.check;

import com.bussiness.bi.bigdata.bean.MyCountDownBean;
import com.bussiness.bi.bigdata.streamfile.FiniteQueue;
import com.bussiness.bi.bigdata.utils.hadoop.HDFSUtil;
import com.bussiness.bi.bigdata.utils.hadoop.IOUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * FileCheck
 * <pre>
 *  外部循环，判断是否需要周期切换，如果需要
 *  然后判断是否需要做校验，如果需要
 *  则提交校验任务到后台，直到全部提交完成
 *  等待步骤1执行完成
 *  等待步骤2执行完成
 *
 *  如果不需要做校验
 *  直接提交（关闭tmp文件流，移除tmp后缀，从map中移除）到步骤2中
 *  等待步骤2执行完成
 *
 *  步骤1：
 *      获取HDFS文件大小
 *      获取缓存汇总大小
 *      删除本地缓存临时文件
 *      删除hdfs临时校验文件
 *      进行比较
 *      从缓存中获取备份文件清单，进行本地合并
 *      上传HDFS
 *      再次校验
 *      如果一致，提交步骤2
 *  步骤2：
 *      如果有校验
 *      移动原有文件变成.delete
 *      移动校验后上传的文件变成原文件
 *      删除.delete文件
 *      如果没有校验
 *      （关闭tmp文件流，移除tmp后缀，从map中移除）
 * </pre>
 *
 * @author chenqixu
 */
public class FileCheck {

    private static final String CACHE_PATH = "/cache/";
    private static final String HDFS_CACHE = ".check";
    private static final String HDFS_DELEE = ".delete";
    private static Logger logger = LoggerFactory.getLogger(FileCheckStep1.class);
    private MyCountDownBean fileCheckBean;
    private FileSystem fs;
    private FileCheckService fileCheckService;
    private FileCheckExecutorsFactory step1Factory;
    private FileCheckExecutorsFactory step2Factory;
    // 任务提交队列
    private FiniteQueue<MyCountDownBean> taskQueue;

    public FileCheck(int parallel_num) {
        step1Factory = new FileCheckExecutorsFactory(parallel_num);
        step2Factory = new FileCheckExecutorsFactory(parallel_num);
        taskQueue = new FiniteQueue<>();
    }

    public void submitTask(boolean isCheck, MyCountDownBean fileCheckBean) {
        if (isCheck) {
            try {
                //判断是否不在执行队列中
                if (!taskQueue.find(fileCheckBean)) {
                    step1Factory.submitTask(new FileCheckStep1(fileCheckBean));
                    taskQueue.put(fileCheckBean);
                }
            } catch (InterruptedException e) {
                logger.warn("任务没有提交成功！fileCheckBean：{}", fileCheckBean);
            }
        } else {
            try {
                step2Factory.addTask(new FileCheckStep2(fileCheckBean));
            } catch (InterruptedException e) {
            }
        }
    }

    public void await() {
        //等待step1
        step1Factory.await();
        //启动step2
        try {
            step2Factory.startTask();
        } catch (InterruptedException e) {
        }
        //等待step2
        step2Factory.await();
    }

    public void close() {
        //取消step1
        step1Factory.cancelTask();
        //等待step2
        step2Factory.await();
    }

    public void isInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {// 任务被取消
            throw new InterruptedException("[" + Thread.currentThread() + "] task is Interrupted.");
        }
    }

    class FileCheckStep1 implements Callable<Boolean> {

        private MyCountDownBean fileCheckBean;

        public FileCheckStep1(MyCountDownBean fileCheckBean) {
            this.fileCheckBean = fileCheckBean;
        }

        @Override
        public Boolean call() throws Exception {
            Path mergerPath = fileCheckBean.getMergerPath();
            String localBackUpPath = fileCheckBean.getLocalBackUpPath();
            String allFileName = mergerPath.toString();
            String mergerFileName = mergerPath.getName();
            // 获取HDFS文件大小
            long fileSize = HDFSUtil.getFileSize(fs, allFileName);
            isInterrupted();// 中断
            // 获取缓存汇总大小
            long sumCache = fileCheckService.getSumCache(mergerFileName);
            isInterrupted();// 中断
            // 本地合并缓存=备份路径+CACHE_PATH+合并周期名称
            String localMergeFileName = localBackUpPath + CACHE_PATH + mergerFileName;
            // hdfs临时校验文件=hdfs全路径+HDFS_CACHE
            String hdfsMergeCheckFileName = allFileName + HDFS_CACHE;
            logger.info("localMergeFileName：{}，hdfsMergeCheckFileName：{}，sumCache：{}，fileSize：{}", localMergeFileName, hdfsMergeCheckFileName, sumCache, fileSize);
            // 删除本地缓存临时文件
            IOUtil.del(localMergeFileName);
            isInterrupted();// 中断
            // 删除hdfs临时校验文件
            HDFSUtil.delete(fs, hdfsMergeCheckFileName);
            isInterrupted();// 中断
            // 进行比较
            if (sumCache == -1 || sumCache == 0) {// 缓存没有值
                logger.warn("{} sumCache is null.", mergerFileName);
            } else if (sumCache > 0 && (sumCache != fileSize)) {// 如果不一致
                logger.debug("从缓存中获取备份文件清单，进行本地合并，最后上传到HDFS，再校验一次");
                // 从缓存中获取备份文件清单，进行本地合并，最后上传到HDFS，再校验一次
                List<String> cacheList = fileCheckService.getCacheList(mergerFileName);
                isInterrupted();// 中断
                IOUtil.mergeFile(localBackUpPath, cacheList, localMergeFileName);
                isInterrupted();// 中断
                // 上传HDFS
                fs.copyFromLocalFile(true,
                        new Path("file:///" + localMergeFileName),
                        new Path(hdfsMergeCheckFileName));
                isInterrupted();// 中断
                // 再次校验
                fileSize = HDFSUtil.getFileSize(fs, hdfsMergeCheckFileName);
                isInterrupted();// 中断
                // 如果一致
                if (sumCache == fileSize) {
                    // 提交step2
                    step2Factory.addTask(new FileCheckStep2(fileCheckBean));
                }
            } else {
                logger.debug("sumCache > 0 && (sumCache == fileSize)，localMergeFileName：{}，hdfsMergeCheckFileName：{}，sumCache：{}，fileSize：{}",
                        localMergeFileName, hdfsMergeCheckFileName, sumCache, fileSize);
            }
            return true;
        }
    }

    class FileCheckStep2 implements Callable<Boolean> {

        private MyCountDownBean fileCheckBean;

        public FileCheckStep2(MyCountDownBean fileCheckBean) {
            this.fileCheckBean = fileCheckBean;
        }

        @Override
        public Boolean call() throws Exception {

            taskQueue.remove(fileCheckBean);
            return null;
        }
    }
}
