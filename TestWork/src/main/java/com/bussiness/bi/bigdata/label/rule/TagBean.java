package com.bussiness.bi.bigdata.label.rule;

public class TagBean {
    private String tagId;
    private String tagName;

    public TagBean() {
    }

    public TagBean(String tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return getTagName();
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
