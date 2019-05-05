package com.newland.bi.bigdata.streamfile;

import com.newland.bi.bigdata.bean.MyCountDownBean;
import com.newland.bi.bigdata.streamfile.check.FileCheck;

/**
 * 合并
 *
 * @author chenqixu
 */
public class HdfsFileMergeBolt {
    private FileCheck check;

    public HdfsFileMergeBolt() {
        //初始化
        init();
    }

    private void init() {

    }

    /**
     * 定时任务
     */
    public void timerRun() {
        boolean isNeedChangeCycle = true;
        boolean isCheck = true;
        MyCountDownBean fileCheckBean = new MyCountDownBean();
        //判断是否需要周期切换
        if (isNeedChangeCycle) {
            //关闭tmp文件流，结束tmp文件，tmp文件重命名为正式文件
            //修改逻辑，tmp文件不重命名为正式文件，而是命名为.check文件
            //提交任务，如果需要校验，则走校验流程，如果不需要校验，那走移动.check文件的流程
            check.submitTask(isCheck, fileCheckBean);
        }
    }

    /**
     * 关闭
     */
    public void close() {
        check.close();
    }
}
