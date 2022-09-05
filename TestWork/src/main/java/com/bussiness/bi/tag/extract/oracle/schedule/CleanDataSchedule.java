package com.bussiness.bi.tag.extract.oracle.schedule;

import com.bussiness.bi.tag.extract.oracle.task.CleanDataTask;
import com.bussiness.bi.tag.extract.common.TaskConstanct;
import com.bussiness.bi.tag.extract.common.TaskSchedule;

/**
 * CleanDataSchedule
 *
 * @author chenqixu
 */
public class CleanDataSchedule extends TaskSchedule<CleanDataTask> {

    @Override
    protected Class<CleanDataTask> getTaskCls() {
        return CleanDataTask.class;
    }

    @Override
    protected String getTaskTag() {
        return TaskConstanct.TAG_CLEAN_DATA;
    }
}
