package com.cqx.common.utils.hdfs;

/**
 * YarnApplicationBean
 *
 * @author chenqixu
 */
public class YarnApplicationBean {
    // Application-Id application_1667339730322_24697
    private String Application_Id;
    // Application-Name select * from tmp_tag_wide_table_prog ...1=0 (Stage-1)
    private String Application_Name;
    // Application-Type MAPREDUCE
    private String Application_Type;
    // User yz_newland
    private String User;
    // Queue-User yz_newland
    private String Queue_User;
    // Queue yz_newland
    private String Queue;
    // State FINISHED
    private String State;
    // Final-State SUCCEEDED
    private String Final_State;
    // Progress 100%
    private String Progress;
    // Tracking-URL https://10.1.12.84:26014/jobhistory/job/job_1667339730322_24697
    private String Tracking_URL;
    // 用于判断是否解析数据成功
    private boolean hasValue = false;

    public YarnApplicationBean(String value, String splitStr) {
        String[] values = value.split(splitStr, -1);
        if (values.length == 10) {
            setApplication_Id(values[0].trim());
            setApplication_Name(values[1].trim());
            setApplication_Type(values[2].trim());
            setUser(values[3].trim());
            setQueue_User(values[4].trim());
            setQueue(values[5].trim());
            setState(values[6].trim());
            setFinal_State(values[7].trim());
            setProgress(values[8].trim());
            setTracking_URL(values[9].trim());
            setHasValue(true);
        }
    }

    @Override
    public String toString() {
        return String.format("Application_Id=%s,Application_Name=%s,Application_Type=%s" +
                        ",User=%s,Queue_User=%s,Queue=%s,State=%s,Final_State=%s,Progress=%s,Tracking_URL=%s"
                , getApplication_Id(), getApplication_Name(), getApplication_Type()
                , getUser(), getQueue_User(), getQueue(), getState(), getFinal_State(), getProgress(), getTracking_URL());
    }

    public String getApplication_Id() {
        return Application_Id;
    }

    public void setApplication_Id(String application_Id) {
        Application_Id = application_Id;
    }

    public String getApplication_Name() {
        return Application_Name;
    }

    public void setApplication_Name(String application_Name) {
        Application_Name = application_Name;
    }

    public String getApplication_Type() {
        return Application_Type;
    }

    public void setApplication_Type(String application_Type) {
        Application_Type = application_Type;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getQueue_User() {
        return Queue_User;
    }

    public void setQueue_User(String queue_User) {
        Queue_User = queue_User;
    }

    public String getQueue() {
        return Queue;
    }

    public void setQueue(String queue) {
        Queue = queue;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getFinal_State() {
        return Final_State;
    }

    public void setFinal_State(String final_State) {
        Final_State = final_State;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }

    public String getTracking_URL() {
        return Tracking_URL;
    }

    public void setTracking_URL(String tracking_URL) {
        Tracking_URL = tracking_URL;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }
}
