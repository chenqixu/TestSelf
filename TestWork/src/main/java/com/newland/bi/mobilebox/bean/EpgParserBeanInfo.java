package com.newland.bi.mobilebox.bean;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * EpgParserBeanInfo
 *
 * @author chenqixu
 */
public class EpgParserBeanInfo {
    private String catgId;//EPG的ID
    private String fId;//EPG的父ID
    private String catgName;//EPG的名称

    public static List<EpgParserBeanInfo> jsonToList(String string) {
        if (StringUtils.isNotEmpty(string))
            return JSON.parseArray(string, EpgParserBeanInfo.class);
        return new ArrayList<>();
    }

    public String toValue(String output_separator) {
        return catgId + output_separator + fId + output_separator + catgName;
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
}
