package com.bussiness.bi.bigdata.net.bean;

import java.io.Serializable;

/**
 * NetBody
 *
 * @author chenqixu
 */
public class NetBody implements Serializable {
    private Object object;

    public NetBody(Object object) {
        this.object = object;
    }

    public Object getValue() {
        return object;
    }

    public void setValue(Object object) {
        this.object = object;
    }

    public String toString() {
        return object.toString();
    }
}
