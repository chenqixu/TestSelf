package com.newland.bi.bigdata.redis;

/**
 * redis具体结果，按行
 *
 * @author chenqixu
 */
public class RedisRowData {

    private String[] value;
    private int start = 0;

    public RedisRowData(int columnIndex) {
        value = new String[columnIndex];
    }

    public String getValue(int columnIndex) {
        return value[columnIndex];
    }

    public void setValue(String value) {
        this.value[start] = value;
        start++;
    }
}
