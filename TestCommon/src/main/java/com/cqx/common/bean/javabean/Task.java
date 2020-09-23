package com.cqx.common.bean.javabean;

import com.cqx.common.annotation.BeanDesc;

/**
 * Task
 *
 * @author chenqixu
 */
public class Task implements ITask {
    @BeanDesc(value = "任务id")
    private int task_id;
    @BeanDesc(value = "任务名称")
    private String task_name;
    @BeanDesc(value = "任务状态")
    private int task_status;
    @BeanDesc(value = "任务是否结束")
    private boolean is_complete;

    public Task() {
    }

    public Task(int task_id) {
        this.task_id = task_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getTask_status() {
        return task_status;
    }

    public void setTask_status(int task_status) {
        this.task_status = task_status;
    }

    public boolean getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }
}
