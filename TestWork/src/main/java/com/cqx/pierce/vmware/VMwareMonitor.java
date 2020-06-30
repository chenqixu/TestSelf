package com.cqx.pierce.vmware;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.pierce.bean.ClipBoardValue;
import com.cqx.pierce.bean.PierceTask;
import com.cqx.pierce.clipboard.ClipBoard;
import com.cqx.pierce.clipboard.ClipBoardTool;
import com.cqx.pierce.util.PierceConstant;
import com.cqx.pierce.util.ReleaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * VMwareMonitor
 *
 * @author chenqixu
 */
public class VMwareMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(VMwareMonitor.class);
    private BlockingQueue<ClipBoardValue> sendQueue;
    private ConcurrentMap<String, PierceTask> pierceTasks;

    public VMwareMonitor(BlockingQueue<ClipBoardValue> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public void run() {
        ReleaseUtil releaseUtil = new ReleaseUtil();
        while (true) {
            SleepUtil.sleepMilliSecond(PierceConstant.MONITOR_SLEEP);
            ClipBoard clipBoard = ClipBoardTool.read();
            switch (clipBoard.getStatus()) {
                case VM_WAIT://vm wait，允许发送
                case MSTSC_RELEASE://mstsc release，mstsc锁释放，允许发送
                    ClipBoardValue send = sendQueue.poll();
                    if (send != null) {
                        logger.info("get {}，command is not null，now to VM_SEND", clipBoard.getStatus());
                        //releaseUtil clean
                        releaseUtil.clean();
                        //vm send
                        ClipBoardTool.setClipBoard(ClipBoard.ClipBoardStatus.VM_SEND, send);
                    } else {
                        if (releaseUtil.add()) {//状态切换
                            logger.info("get {}，command is null，releaseUtil is full，now to VM_RELEASE", clipBoard.getStatus());
                            //vm release
                            ClipBoardTool.setClipBoardStatus(ClipBoard.ClipBoardStatus.VM_RELEASE);
                        } else {
                            logger.info("get {}，command is null，now to releaseUtil add", clipBoard.getStatus());
                        }
                    }
                    break;
                case MSTSC_SEND://mstsc send，接收处理结果并反馈
                    logger.info("get MSTSC_SEND，now to VM_WAIT and get result");
                    //releaseUtil clean
                    releaseUtil.clean();
                    //get mstsc result
                    ClipBoardValue result = clipBoard.getClipBoardValue();
                    //result to ...
                    logger.info("result is {}", result.getParams());
                    //vm wait
                    ClipBoardTool.setClipBoardStatus(ClipBoard.ClipBoardStatus.VM_WAIT);
                    break;
            }
        }
    }
}
