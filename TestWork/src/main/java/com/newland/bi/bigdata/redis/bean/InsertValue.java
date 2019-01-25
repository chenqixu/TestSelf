package com.newland.bi.bigdata.redis.bean;

/**
 * 插入值
 *
 * @author chenqixu
 */
public class InsertValue {
    private String field;
    private String value;

    private InsertValue() {
    }

    public static InsertValue newbuilder() {
        return new InsertValue();
    }

    public String getField() {
        return field;
    }

    public InsertValue setField(String field) {
        this.field = field;
        return this;
    }

    public String getValue() {
        return value;
    }

    public InsertValue setValue(String value) {
        this.value = value;
        return this;
    }
}
