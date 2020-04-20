package com.cqx.etlcollect;

import com.cqx.etlcollect.bean.FileBean;
import com.newland.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模拟扫描
 * <pre>
 *     不停的从源端扫描出文件加入到调度列表中等待采集消费
 *     只管扫描
 * </pre>
 *
 * @author chenqixu
 */
public class CollectScan implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CollectScan.class);
    private CollectDispatch collectDispatch;
    private volatile boolean flag = true;

    public CollectScan(CollectDispatch collectDispatch) {
        this.collectDispatch = collectDispatch;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                // 5毫秒往服务器put一个文件
                SleepUtils.sleepMilliSecond(5);
                FileBean fileBean = new FileBean();
                fileBean.setFileName("LTE_S1MME_028470789002_20190603110100.txt");
                fileBean.setFtpHost("127.0.0.1");
                fileBean.setFilePath("/bi/data/hwlte/");
                fileBean.setFileSize(10);
                fileBean.setFileDate("20190603110100.");
                collectDispatch.put(fileBean);
                logger.info("{}扫描到文件：{}", this, fileBean);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void stop() {
        flag = false;
    }
}
