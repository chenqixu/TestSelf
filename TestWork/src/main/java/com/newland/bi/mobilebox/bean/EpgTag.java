package com.newland.bi.mobilebox.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 一级分类维表
 *
 * @author chenqixu
 */
public class EpgTag {
    private String catgId;//EPG的ID
    private String fId;//EPG的父ID
    private String catgName;//EPG的名称
    private String dt;//时间分区

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getCatgId() {
        return catgId;
    }

    public void setCatgId(String catgId) {
        this.catgId = catgId;
    }

    public String getfId() {
        return fId;
    }

    public void setfId(String fId) {
        this.fId = fId;
    }

    public String getCatgName() {
        return catgName;
    }

    public void setCatgName(String catgName) {
        this.catgName = catgName;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
