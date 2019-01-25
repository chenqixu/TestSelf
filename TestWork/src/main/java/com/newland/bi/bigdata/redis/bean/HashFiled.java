package com.newland.bi.bigdata.redis.bean;

/**
 * 字段
 *
 * @author chenqixu
 */
public class HashFiled {
    boolean key = false;
    boolean field = false;
    boolean value = false;

    private HashFiled() {
    }

    public static HashFiled newbuilder() {
        return new HashFiled();
    }

    public void setAll() {
        key = true;
        field = true;
        value = true;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public boolean isField() {
        return field;
    }

    public void setField(boolean field) {
        this.field = field;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
