package com.cqx.common.bean.javabean;

/**
 * Task
 *
 * @author chenqixu
 */
public class Task {
    private int task_id;
    private String task_name;
    private int task_status;

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
}
