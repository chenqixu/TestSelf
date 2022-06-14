package com.bussiness.bi.bigdata.net;

import com.alibaba.fastjson.JSON;

/**
 * HeartBean
 *
 * @author chenqixu
 */
public class HeartBean {
    private long lastCheck;
    private String name;

    public static HeartBean jstonToBean(String json) {
        return JSON.parseObject(json, HeartBean.class);
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
