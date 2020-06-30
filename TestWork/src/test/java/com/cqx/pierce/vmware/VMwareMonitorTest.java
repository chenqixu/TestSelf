package com.cqx.pierce.vmware;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.pierce.bean.ClipBoardValue;
import com.cqx.pierce.mstsc.MstscMonitor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VMwareMonitorTest {

    @Test
    public void run() throws Exception {
        final BlockingQueue<ClipBoardValue> sendQueue = new LinkedBlockingQueue<>();
        VMwareMonitor vMwareMonitor = new VMwareMonitor(sendQueue);
        MstscMonitor mstscMonitor = new MstscMonitor();
        vMwareMonitor.start();
        mstscMonitor.start();
//        vMwareMonitor.join();
//        mstscMonitor.join();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ClipBoardValue clipBoardValue = new ClipBoardValue();
                clipBoardValue.setType("PLSQL");
                Map<String, String> params = new HashMap<>();
                params.put("tns", "jdbc:127.0.0.1@ora");
                clipBoardValue.setParams(params);
                sendQueue.add(clipBoardValue);
            }
        }, 1000);//1秒后启动

        SleepUtil.sleepMilliSecond(5000);
    }
}