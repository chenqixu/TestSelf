package com.bussiness.bi.tag.extract.oracle.schedule;

import com.bussiness.bi.tag.extract.oracle.task.PutHdfsTask;
import com.bussiness.bi.tag.extract.common.TaskConstanct;
import com.bussiness.bi.tag.extract.common.TaskSchedule;

/**
 * PutHdfsSchedule
 *
 * @author chenqixu
 */
public class PutHdfsSchedule extends TaskSchedule<PutHdfsTask> {

    @Override
    protected Class<PutHdfsTask> getTaskCls() {
        return PutHdfsTask.class;
    }

    @Override
    protected String getTaskTag() {
        return TaskConstanct.TAG_PUT_HDFS;
    }
}
