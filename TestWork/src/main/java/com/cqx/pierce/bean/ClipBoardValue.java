package com.cqx.pierce.bean;

import java.util.Map;

/**
 * ClipBoardValue
 *
 * @author chenqixu
 */
public class ClipBoardValue {
    private String user_id;
    private String task_name;
    private String type;
    private Map<String, String> params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }
}
