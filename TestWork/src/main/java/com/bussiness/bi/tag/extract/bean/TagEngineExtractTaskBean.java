package com.bussiness.bi.tag.extract.bean;

import java.util.Date;

/**
 * TagEngineExtractTaskBean
 *
 * @author chenqixu
 */
public class TagEngineExtractTaskBean {
    private String uuid;// varchar2(20),
    private String task_type;// varchar2(10),--oracle,hive
    private String task_tag;// varchar2(20),
    private String task_id;// varchar2(50),
    private String task_param;// clob,
    private long file_size;// number(20),
    private long file_num;// number(20),
    private Date create_time;
    private Date complete_time;
    private int status;// number(1)

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_param() {
        return task_param;
    }

    public void setTask_param(String task_param) {
        this.task_param = task_param;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public long getFile_num() {
        return file_num;
    }

    public void setFile_num(long file_num) {
        this.file_num = file_num;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getComplete_time() {
        return complete_time;
    }

    public void setComplete_time(Date complete_time) {
        this.complete_time = complete_time;
    }

    public String getTask_tag() {
        return task_tag;
    }

    public void setTask_tag(String task_tag) {
        this.task_tag = task_tag;
    }
}
