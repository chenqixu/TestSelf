package com.bussiness.bi.bigdata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * ADBExcelBean
 *
 * @author chenqixu
 */
public class ADBExcelBean {
    private String adb_table_name;
    private String source_table_name;
    private String ogg_topic;
    private String flat_topic;
    private String ogg_pks;
    private String ogg_asvc_name;
    private List<String> fields = new ArrayList<>();
    private List<String> fields_type = new ArrayList<>();

    public void addField(String field) {
        fields.add(field);
    }

    public void addFieldType(String fieldType) {
        fields_type.add(fieldType);
    }

    public String getAdb_table_name() {
        return adb_table_name;
    }

    public void setAdb_table_name(String adb_table_name) {
        this.adb_table_name = adb_table_name;
    }

    public String getSource_table_name() {
        return source_table_name;
    }

    public void setSource_table_name(String source_table_name) {
        this.source_table_name = source_table_name;
    }

    public String getOgg_topic() {
        return ogg_topic;
    }

    public void setOgg_topic(String ogg_topic) {
        this.ogg_topic = ogg_topic;
    }

    public String getFlat_topic() {
        return flat_topic;
    }

    public void setFlat_topic(String flat_topic) {
        this.flat_topic = flat_topic;
    }

    public String getOgg_pks() {
        return ogg_pks;
    }

    public void setOgg_pks(String ogg_pks) {
        this.ogg_pks = ogg_pks;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getFields_type() {
        return fields_type;
    }

    public String getFieldsWithSeparator() {
        return getStringWithSeparator(fields, ",");
    }

    public String getFieldsTypeWithSeparator() {
        return getStringWithSeparator(fields_type, ",");
    }

    private String getStringWithSeparator(List<String> list, String peparator) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append(peparator);
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getOgg_asvc_name() {
        return ogg_asvc_name;
    }

    public void setOgg_asvc_name(String ogg_asvc_name) {
        this.ogg_asvc_name = ogg_asvc_name;
    }
}
