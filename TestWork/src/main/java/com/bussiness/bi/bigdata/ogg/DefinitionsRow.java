package com.bussiness.bi.bigdata.ogg;

import java.util.Map;

/**
 * OggJsonSchemaDefinitionsRow
 *
 * @author chenqixu
 */
public class DefinitionsRow {
    private String type;
    private Map<String, Object> properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
