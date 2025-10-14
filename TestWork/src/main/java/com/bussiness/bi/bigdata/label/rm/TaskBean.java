package com.bussiness.bi.bigdata.label.rm;

/**
 * 任务
 *
 * @author chenqixu
 */
public class TaskBean {
    private String uuid;
    private String task_id;
    private String queueName;
    private int resource;
    private boolean isRequest;
    private boolean isRelease;

    public TaskBean() {
    }

    public TaskBean(String uuid, String task_id) {
        this.uuid = uuid;
        this.task_id = task_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public boolean isRelease() {
        return isRelease;
    }

    public void setRelease(boolean release) {
        isRelease = release;
    }
}
