package com.bussiness.bi.tag.extract.oracle.schedule;

import com.bussiness.bi.tag.extract.oracle.task.DownLoadTask;
import com.bussiness.bi.tag.extract.common.TaskConstanct;
import com.bussiness.bi.tag.extract.common.TaskSchedule;

/**
 * DownLoadSchedule
 *
 * @author chenqixu
 */
public class DownLoadSchedule extends TaskSchedule<DownLoadTask> {

    @Override
    protected Class<DownLoadTask> getTaskCls() {
        return DownLoadTask.class;
    }

    @Override
    protected String getTaskTag() {
        return TaskConstanct.TAG_DOWN_LOAD;
    }
}
