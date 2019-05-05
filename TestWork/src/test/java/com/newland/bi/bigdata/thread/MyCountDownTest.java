package com.newland.bi.bigdata.thread;

import com.newland.bi.bigdata.bean.MyCountDownBean;
import com.newland.bi.bigdata.utils.SleepUtils;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyCountDownTest {

    private ExecutorsFactory<MyCountDownBean> executorsFactory;
    private MyCountDown myCountDown;
    private int parallel_num = 5;

    @Before
    public void setUp() throws Exception {
        myCountDown = new MyCountDown();
        executorsFactory = new ExecutorsFactory<>(parallel_num);
    }

    @After
    public void tearDown() throws Exception {
        executorsFactory.close();
    }

    @Test
    public void timingCheck() throws Exception {
        executorsFactory.setiExecutorsRun(new IExecutorsRun() {
            @Override
            public void run() throws Exception {
                myCountDown.timingCheck();
            }
        });
        myCountDown.startTimeOut(executorsFactory);
        executorsFactory.startCallable();
        SleepUtils.sleepMilliSecond(5000);
        myCountDown.printStep2Count();
    }

    @Test
    public void allTimingCheck() throws Exception {
        //设置执行类
        executorsFactory.setiExecutorsRun(myCountDown);
        //模拟框架发送close信号
        myCountDown.startTimeOut(executorsFactory);
        //提交任务到step1
        executorsFactory.submitCallable(MyCountDownBean
                .newbuilder().setMergerPath(new Path("/hdfs1")).setLocalBackUpPath("/temp1"));
        executorsFactory.submitCallable(MyCountDownBean
                .newbuilder().setMergerPath(new Path("/hdfs2")).setLocalBackUpPath("/temp2"));
        executorsFactory.submitCallable(MyCountDownBean
                .newbuilder().setMergerPath(new Path("/hdfs3")).setLocalBackUpPath("/temp3"));
        executorsFactory.submitCallable(MyCountDownBean
                .newbuilder().setMergerPath(new Path("/hdfs4")).setLocalBackUpPath("/temp4"));
        executorsFactory.submitCallable(MyCountDownBean
                .newbuilder().setMergerPath(new Path("/hdfs5")).setLocalBackUpPath("/temp5"));
        //等待step1完成
        executorsFactory.awaitFutureListPool();
        executorsFactory.printlnStatus();
        myCountDown.printlnStep2Status();
        //step1完成后，执行step2
        myCountDown.awaitStep2();
        executorsFactory.printlnStatus();
        myCountDown.printlnStep2Status();
//        SleepUtils.sleepMilliSecond(5000);
    }
}