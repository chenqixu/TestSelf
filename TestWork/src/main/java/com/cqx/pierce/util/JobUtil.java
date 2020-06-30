package com.cqx.pierce.util;

import com.cqx.pierce.bean.ClipBoardValue;
import com.cqx.pierce.task.ITask;
import com.cqx.pierce.task.PLSQLTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * JobUtil
 *
 * @author chenqixu
 */
public class JobUtil {
    private static final Logger logger = LoggerFactory.getLogger(JobUtil.class);

    public void submit(ClipBoardValue clipBoardValue, BlockingQueue<ClipBoardValue> resultQueue) {
        logger.info("submit job：{}，{}", clipBoardValue.getType(), clipBoardValue.getParams());
        ITask iTask;
        switch (clipBoardValue.getType()) {
            case "PLSQL":
                iTask = new PLSQLTask();
                try {
                    resultQueue.put(iTask.run(clipBoardValue));
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                break;
            case "SecureCRT":
                break;
            case "PUT":
                break;
            case "GET":
                break;
        }
    }
}
