package com.bussiness.bi.bigdata.label.rm;

/**
 * 队列
 *
 * @author chenqixu
 */
public class QueueBean {
    private String queueName;
    private int current_source;

    public QueueBean() {
    }

    public QueueBean(String queueName, int current_source) {
        this.queueName = queueName;
        this.current_source = current_source;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getCurrent_source() {
        return current_source;
    }

    public void setCurrent_source(int current_source) {
        this.current_source = current_source;
    }
}
