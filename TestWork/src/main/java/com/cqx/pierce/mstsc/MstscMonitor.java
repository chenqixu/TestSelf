package com.cqx.pierce.mstsc;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.pierce.bean.ClipBoardValue;
import com.cqx.pierce.bean.PierceTask;
import com.cqx.pierce.clipboard.ClipBoard;
import com.cqx.pierce.clipboard.ClipBoardTool;
import com.cqx.pierce.util.JobUtil;
import com.cqx.pierce.util.PierceConstant;
import com.cqx.pierce.util.ReleaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MstscMonitor
 *
 * @author chenqixu
 */
public class MstscMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(MstscMonitor.class);
    private ConcurrentMap<String, PierceTask> pierceTasks;
    private BlockingQueue<ClipBoardValue> resultQueue;
    private BlockingQueue<PierceTask> jobQueue;

    public MstscMonitor() {
        resultQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        JobUtil jobUtil = new JobUtil();
        ReleaseUtil releaseUtil = new ReleaseUtil();
        while (true) {
            SleepUtil.sleepMilliSecond(PierceConstant.MONITOR_SLEEP);
            ClipBoard clipBoard = ClipBoardTool.read();
            switch (clipBoard.getStatus()) {
                case VM_RELEASE:
                    logger.info("get VM_RELEASE，now to MSTSC_WAIT");
                    //releaseUtil clean
                    releaseUtil.clean();
                    //mstsc wait
                    ClipBoardTool.setClipBoardStatus(ClipBoard.ClipBoardStatus.MSTSC_WAIT);
                    break;
                case VM_SEND://vm send，处理
                    logger.info("get VM_SEND，now to MSTSC_WAIT and submit job");
                    //releaseUtil clean
                    releaseUtil.clean();
                    //mstsc wait
                    ClipBoardTool.setClipBoardStatus(ClipBoard.ClipBoardStatus.MSTSC_WAIT);
                    //get vm cmd
                    ClipBoardValue vm_cmd = clipBoard.getClipBoardValue();
                    //submit job
                    jobUtil.submit(vm_cmd, resultQueue);
                    break;
                case MSTSC_WAIT:
                    ClipBoardValue result = resultQueue.poll();
                    if (result != null) {
                        logger.info("get MSTSC_WAIT，result is not null，now to MSTSC_SEND");
                        //releaseUtil clean
                        releaseUtil.clean();
                        //mstsc send
                        ClipBoardTool.setClipBoard(ClipBoard.ClipBoardStatus.MSTSC_SEND, result);
                    } else {
                        if (releaseUtil.add()) {//状态切换
                            logger.info("get MSTSC_WAIT，result is null，releaseUtil is full，now to MSTSC_RELEASE");
                            //mstsc release
                            ClipBoardTool.setClipBoardStatus(ClipBoard.ClipBoardStatus.MSTSC_RELEASE);
                        } else {
                            logger.info("get MSTSC_WAIT，result is null，now to releaseUtil add");
                        }
                    }
                    break;
            }
        }
    }
}
