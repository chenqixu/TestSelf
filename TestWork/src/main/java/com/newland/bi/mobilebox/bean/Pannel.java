package com.newland.bi.mobilebox.bean;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * 一级分类
 *
 * @author chenqixu
 */
public class Pannel {
    private String id;
    private String imgurl;
    private String title;

    public static Pannel newbuilder() {
        return new Pannel();
    }

    public static Pannel jsonToBean(String string) {
        return JSON.parseObject(string, Pannel.class);
    }

    public static String listToJson(List<Pannel> list) {
        if (list != null)
            return JSON.toJSONString(list);
        return null;
    }

    public Pannel build() {
        return this;
    }

    public String getId() {
        return id;
    }

    public Pannel setId(String id) {
        this.id = id;
        return this;
    }

    public String getImgurl() {
        return imgurl;
    }

    public Pannel setImgurl(String imgurl) {
        this.imgurl = imgurl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Pannel setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
