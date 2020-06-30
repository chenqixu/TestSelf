package com.cqx.pierce.task;

import com.cqx.pierce.bean.ClipBoardValue;

import java.util.Map;

/**
 * ITask
 *
 * @author chenqixu
 */
public interface ITask {
    String getTaskName();

    String getTaskType();

    Map<String, String> getTaskParams();

    ClipBoardValue run(ClipBoardValue clipBoardValue);
}
