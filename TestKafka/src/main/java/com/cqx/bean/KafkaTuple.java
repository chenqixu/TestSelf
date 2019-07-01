package com.cqx.bean;

import java.util.Map;

/**
 * KafkaTuple
 *
 * @author chenqixu
 */
public class KafkaTuple {
    private String keyWord;
    private String topic;
    private String key;
    private String filename;
    private Map<String, String> fields;

    public KafkaTuple() {
    }

    public KafkaTuple(String filename, String keyWord, Map<String, String> fields) {
        this.filename = filename;
        this.keyWord = keyWord;
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

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
