package com.cqx.sync.bean;

import java.sql.Timestamp;

/**
 * Cfg_etl_time_rule
 *
 * @author chenqixu
 */
public class CfgEtlTimeRule {
    private String time_rule;
    private String conv_file_head;
    private java.sql.Timestamp insert_time;
    private Long task_template_id;

    public String getTime_rule() {
        return time_rule;
    }

    public void setTime_rule(String time_rule) {
        this.time_rule = time_rule;
    }

    public String getConv_file_head() {
        return conv_file_head;
    }

    public void setConv_file_head(String conv_file_head) {
        this.conv_file_head = conv_file_head;
    }

    public Timestamp getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Timestamp insert_time) {
        this.insert_time = insert_time;
    }

    public Long getTask_template_id() {
        return task_template_id;
    }

    public void setTask_template_id(Long task_template_id) {
        this.task_template_id = task_template_id;
    }
}
