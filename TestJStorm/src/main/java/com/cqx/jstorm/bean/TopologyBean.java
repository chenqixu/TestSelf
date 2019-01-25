package com.cqx.jstorm.bean;

import java.util.Map;

/**
 * 拓扑
 *
 * @author chenqixu
 */
public class TopologyBean {
    private int worker_num;
    private int ack_num;
    private int worker_memory;
    private String name;

    public static TopologyBean newbuilder() {
        return new TopologyBean();
    }

    public TopologyBean parser(Object param) {
        Map<String, ?> tmp = (Map<String, ?>) param;
        worker_num = (Integer) tmp.get("worker_num");
        ack_num = (Integer) tmp.get("ack_num");
        worker_memory = (Integer) tmp.get("worker_memory");
        name = (String) tmp.get("name");
        return this;
    }

    public int getWorker_num() {
        return worker_num;
    }

    public void setWorker_num(int worker_num) {
        this.worker_num = worker_num;
    }

    public int getAck_num() {
        return ack_num;
    }

    public void setAck_num(int ack_num) {
        this.ack_num = ack_num;
    }

    public int getWorker_memory() {
        return worker_memory;
    }

    public void setWorker_memory(int worker_memory) {
        this.worker_memory = worker_memory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
