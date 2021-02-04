package com.cqx.work.jdk8.model;

/**
 * BoltBean
 *
 * @author chenqixu
 */
public class BoltBean {
    private String data;

    public BoltBean() {
    }

    public BoltBean(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return this.data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
