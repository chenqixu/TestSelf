package com.cqx.common.utils.jdbc;

import java.sql.Timestamp;

/**
 * <pre>
 * create table multi_test(
 *     task_id number(10),
 *     task_name varchar2(50),
 *     task_type varchar2(10),
 *     task_cycle varchar2(20),
 *     file_size number(15),
 *     create_time date,
 *     update_time date
 * );
 * </pre>
 *
 * @author chenqixu
 */
public class MultiTestBean {
    private long task_id;
    private String task_name;
    private String task_type;
    private String task_cycle;
    private long file_size;
    private Timestamp create_time;
    private Timestamp update_time;

    @Override
    public String toString() {
        return task_id + "," + task_name + "," + task_type + "," + task_cycle + "," + file_size + "," + create_time + "," + update_time;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_cycle() {
        return task_cycle;
    }

    public void setTask_cycle(String task_cycle) {
        this.task_cycle = task_cycle;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
