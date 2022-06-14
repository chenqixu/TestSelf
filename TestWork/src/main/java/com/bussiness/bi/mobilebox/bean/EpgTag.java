package com.bussiness.bi.mobilebox.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 一级分类维表
 *
 * @author chenqixu
 */
public class EpgTag {

    private static final String SPLIT_STR = "$";

    private String catgId;//EPG的ID
    private String fId;//EPG的父ID
    private String catgName;//EPG的名称
    private String actionType;//动作
    private String contentType;//类型
    private List<EpgTag> subCatgs;//子EPG

    public static EpgTag newbuilder() {
        return new EpgTag();
    }

    public static EpgTag jsonToBean(String string) {
        return JSON.parseObject(string, EpgTag.class);
    }

    public EpgTag build() {
        return this;
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
//    }

    @Override
    public String toString() {
        return catgId + SPLIT_STR + fId + SPLIT_STR + catgName + SPLIT_STR;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String getCatgId() {
        return catgId;
    }

    public EpgTag setCatgId(String catgId) {
        this.catgId = catgId;
        return this;
    }

    public String getfId() {
        return fId;
    }

    public EpgTag setfId(String fId) {
        this.fId = fId;
        return this;
    }

    public String getCatgName() {
        return catgName;
    }

    public EpgTag setCatgName(String catgName) {
        this.catgName = catgName;
        return this;
    }

    public String getActionType() {
        return actionType;
    }

    public EpgTag setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public EpgTag setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public List<EpgTag> getSubCatgs() {
        return subCatgs;
    }

    public EpgTag setSubCatgs(List<EpgTag> subCatgs) {
        this.subCatgs = subCatgs;
        return this;
    }
}
