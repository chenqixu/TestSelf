package com.bussiness.bi.bigdata.bean;

import java.util.Map;

/**
 * KafkaTuple
 *
 * @author chenqixu
 */
public class KafkaTuple {
    private String topic;
    private String key;
    private Map<String, String> fields;

    public KafkaTuple() {
    }

    public KafkaTuple(String topic, Map<String, String> fields) {
        this.topic = topic;
        this.fields = fields;
    }

    public KafkaTuple(String topic, String key, Map<String, String> fields) {
        this.topic = topic;
        this.key = key;
        this.fields = fields;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
