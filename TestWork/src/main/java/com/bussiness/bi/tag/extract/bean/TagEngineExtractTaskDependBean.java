package com.bussiness.bi.tag.extract.bean;

/**
 * TagEngineExtractTaskDependBean
 *
 * @author chenqixu
 */
public class TagEngineExtractTaskDependBean {
    private String uuid;// varchar2(20),
    private String task_type;// varchar2(10),--oracle,hive
    private String task_id;// varchar2(50),
    private String task_depend_id;// varchar2(50)

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_depend_id() {
        return task_depend_id;
    }

    public void setTask_depend_id(String task_depend_id) {
        this.task_depend_id = task_depend_id;
    }

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
}
