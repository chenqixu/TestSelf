package com.newland.bi.bigdata.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * HdfsFileMergeBoltZookeeperTool
 *
 * @author chenqixu
 */
public class HdfsFileMergeBoltZookeeperTool {

    private BlockingQueue<OperatingBean> toDoFilesQueue = new LinkedBlockingQueue<>();

    public void put(OperatingBean operatingBean) throws InterruptedException {
        toDoFilesQueue.put(operatingBean);
    }

    class OperatingBean {
        //操作动作
        //操作数据
    }
}
