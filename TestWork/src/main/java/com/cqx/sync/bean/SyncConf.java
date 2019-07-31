package com.cqx.sync.bean;

import java.sql.Timestamp;

/**
 * SyncConf
 *
 * @author chenqixu
 */
public class SyncConf {
    private String SYNC_NAME;
    private java.sql.Timestamp SYNC_TIME;

    public String getSYNC_NAME() {
        return SYNC_NAME;
    }

    public void setSYNC_NAME(String SYNC_NAME) {
        this.SYNC_NAME = SYNC_NAME;
    }

    public Timestamp getSYNC_TIME() {
        return SYNC_TIME;
    }

    public void setSYNC_TIME(Timestamp SYNC_TIME) {
        this.SYNC_TIME = SYNC_TIME;
    }
}
