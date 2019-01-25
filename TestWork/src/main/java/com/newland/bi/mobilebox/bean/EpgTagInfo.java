package com.newland.bi.mobilebox.bean;

import com.alibaba.fastjson.JSON;

/**
 * EpgTagInfo
 *
 * @author chenqixu
 */
public class EpgTagInfo {
    private String catgId;//EPG的ID
    private String fId;//EPG的父ID
    private String catgName;//EPG的名称

    public static EpgTagInfo newbuilder() {
        return new EpgTagInfo();
    }

    public static EpgTagInfo newbuilder(EpgTag epgTag) {
        return newbuilder()
                .setCatgId(epgTag.getCatgId())
                .setCatgName(epgTag.getCatgName())
                .setfId(epgTag.getfId());
    }

    public String getCatgId() {
        return catgId;
    }

    public EpgTagInfo setCatgId(String catgId) {
        this.catgId = catgId;
        return this;
    }

    public String getfId() {
        return fId;
    }

    public EpgTagInfo setfId(String fId) {
        this.fId = fId;
        return this;
    }

    public String getCatgName() {
        return catgName;
    }

    public EpgTagInfo setCatgName(String catgName) {
        this.catgName = catgName;
        return this;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
