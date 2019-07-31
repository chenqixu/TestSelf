package com.cqx.sync.bean;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * cfg_etl_time_rule
 *
 * @author chenqixu
 */
public class cfg_etl_time_rule {
    Timestamp insert_time;

    public Timestamp getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Timestamp insert_time) {
        this.insert_time = insert_time;
    }
}
