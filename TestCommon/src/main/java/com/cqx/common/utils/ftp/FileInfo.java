package com.cqx.common.utils.ftp;

import com.alibaba.fastjson.JSON;

import java.sql.Date;

/**
 * FileInfo
 *
 * @author chenqixu
 */
public class FileInfo {
    private long task_template_id;//任务ID
    private String file_name;//文件名，不含后缀
    private String source_machine;//源文件主机
    private String source_path;//源文件路径
    private String check_file_path;//源校验文件路径
    private String data_index;//源文件归属节点，如：data0、data1……
    private String source_file_suffix;//源文件后缀
    private String check_file_suffix;//源校验文件后缀
    private long source_file_createTime = 0L;//源文件生成时间
    private long file_size = 0L;//源文件大小
    private long file_recordNum = 0L;//源文件记录数
    private String file_cycle;//文件归属周期(文件合并周期)
    private String merge_name;//文件合并名称
    private Date insert_time;//操作时间
    private int file_status = -1;//文件操作状态
    private Date file_status_updateTime;//文件操作状态更新时间

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 替换掉文件名中的校验后缀
     */
    public void replaceCheck_file_suffix() {
        if (check_file_suffix != null && file_name != null) {
            file_name = file_name.replace(check_file_suffix, "");
        }
    }

    public long getTask_template_id() {
        return task_template_id;
    }

    public void setTask_template_id(long task_template_id) {
        this.task_template_id = task_template_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getSource_machine() {
        return source_machine;
    }

    public void setSource_machine(String source_machine) {
        this.source_machine = source_machine;
    }

    public String getSource_path() {
        return source_path;
    }

    public void setSource_path(String source_path) {
        if (!source_path.endsWith("/")) {
            source_path = source_path + "/";
        }
        this.source_path = source_path;
    }

    public String getCheck_file_path() {
        return check_file_path;
    }

    public void setCheck_file_path(String check_file_path) {
        this.check_file_path = check_file_path;
    }

    public String getData_index() {
        return data_index;
    }

    public void setData_index(String data_index) {
        this.data_index = data_index;
    }

    public String getSource_file_suffix() {
        return source_file_suffix;
    }

    public void setSource_file_suffix(String source_file_suffix) {
        this.source_file_suffix = source_file_suffix;
    }

    public String getCheck_file_suffix() {
        return check_file_suffix;
    }

    public void setCheck_file_suffix(String check_file_suffix) {
        this.check_file_suffix = check_file_suffix;
    }

    public long getSource_file_createTime() {
        return source_file_createTime;
    }

    public void setSource_file_createTime(long source_file_createTime) {
        this.source_file_createTime = source_file_createTime;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public long getFile_recordNum() {
        return file_recordNum;
    }

    public void setFile_recordNum(long file_recordNum) {
        this.file_recordNum = file_recordNum;
    }

    public String getFile_cycle() {
        return file_cycle;
    }

    public void setFile_cycle(String file_cycle) {
        this.file_cycle = file_cycle;
    }

    public String getMerge_name() {
        return merge_name;
    }

    public void setMerge_name(String merge_name) {
        this.merge_name = merge_name;
    }

    public int getFile_status() {
        return file_status;
    }

    public void setFile_status(int file_status) {
        this.file_status = file_status;
    }

    public Date getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Date insert_time) {
        this.insert_time = insert_time;
    }

    public Date getFile_status_updateTime() {
        return file_status_updateTime;
    }

    public void setFile_status_updateTime(Date file_status_updateTime) {
        this.file_status_updateTime = file_status_updateTime;
    }
}
