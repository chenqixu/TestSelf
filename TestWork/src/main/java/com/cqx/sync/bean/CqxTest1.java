package com.cqx.sync.bean;

/**
 * CqxTest1
 *
 * @author chenqixu
 */
public class CqxTest1 {
    private String id;

    public CqxTest1() {
    }

    public CqxTest1(String id) {
        this.id = id;
    }

    public String toString() {
        return super.toString() + "，id：" + id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
