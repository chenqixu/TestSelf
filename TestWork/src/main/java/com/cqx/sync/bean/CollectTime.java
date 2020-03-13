package com.cqx.sync.bean;

import java.sql.Timestamp;

/**
 * CollectTime
 *
 * @author chenqixu
 */
public class CollectTime {
    private String collect_time;
    private java.sql.Timestamp sync_time;

    public String getCollect_time() {
        return collect_time;
    }

    public void setCollect_time(String collect_time) {
        this.collect_time = collect_time;
    }

    public Timestamp getSync_time() {
        return sync_time;
    }

    public void setSync_time(Timestamp sync_time) {
        this.sync_time = sync_time;
    }
}
