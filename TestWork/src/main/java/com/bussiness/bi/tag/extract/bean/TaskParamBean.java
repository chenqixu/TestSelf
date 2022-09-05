package com.bussiness.bi.tag.extract.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * TaskParamBean
 *
 * @author chenqixu
 */
public class TaskParamBean<T> {
    private String className;
    private String values;
    @JSONField(serialize = false)
    private T t;

    public TaskParamBean() {
    }

    public TaskParamBean(T t) {
        this.className = t.getClass().getName();
        this.values = JSON.toJSONString(t);
        this.t = t;
    }

    public String toJSONValues() {
        return JSON.toJSONString(this);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
