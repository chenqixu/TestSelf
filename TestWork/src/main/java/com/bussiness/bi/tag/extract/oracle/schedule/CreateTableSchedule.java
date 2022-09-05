package com.bussiness.bi.tag.extract.oracle.schedule;

import com.bussiness.bi.tag.extract.oracle.task.CreateTableTask;
import com.bussiness.bi.tag.extract.common.TaskConstanct;
import com.bussiness.bi.tag.extract.common.TaskSchedule;

/**
 * CreateTableSchedule
 *
 * @author chenqixu
 */
public class CreateTableSchedule extends TaskSchedule<CreateTableTask> {

    @Override
    protected Class<CreateTableTask> getTaskCls() {
        return CreateTableTask.class;
    }

    @Override
    protected String getTaskTag() {
        return TaskConstanct.TAG_CREATE_TABLE;
    }
}
