package com.cqx.etlcollect.bean;

import java.util.Date;

/**
 * 任务信息
 *
 * @author chenqixu
 */
public class TaskBean {
    private String taskId;
    private String taskName;
    private Object taskObj;
    private TaskStatus taskStatus;
    private TaskResult result;
    private Date taskCreateTime;
    private Date taskRunStartTime;
    private Date taskRunEndTime;
    private boolean isActive = true;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Object getTaskObj() {
        return taskObj;
    }

    public void setTaskObj(Object taskObj) {
        this.taskObj = taskObj;
        if (taskObj instanceof FileBean) {
            FileBean fileBean = (FileBean) taskObj;
            this.taskId = fileBean.hashCode() + "";
            this.taskName = fileBean.getFileName();
            this.taskCreateTime = new Date();
        }
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskResult getResult() {
        return result;
    }

    public void setResult(TaskResult result) {
        this.result = result;
    }

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public Date getTaskRunStartTime() {
        return taskRunStartTime;
    }

    public void setTaskRunStartTime(Date taskRunStartTime) {
        this.taskRunStartTime = taskRunStartTime;
    }

    public Date getTaskRunEndTime() {
        return taskRunEndTime;
    }

    public void setTaskRunEndTime(Date taskRunEndTime) {
        this.taskRunEndTime = taskRunEndTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
