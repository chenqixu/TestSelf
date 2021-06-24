package com.cqx.common.bean.kafka;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * avro的层级数据
 *
 * @author chenqixu
 */
public class AvroLevelData {
    private String name;
    private Map<String, Object> val = new HashMap<>();
    private Map<String, AvroLevelData> childMap = new HashMap<>();

    private AvroLevelData(String name) {
        this.name = name;
    }

    public static AvroLevelData newInstance(String name) {
        return new AvroLevelData(name);
    }

    public void putVal(String key, Object value) {
        val.put(key, value);
    }

    public void putChildVal(String childName, String key, Object value) {
        AvroLevelData child = childMap.get(childName);
        if (child == null) {
            child = new AvroLevelData(childName);
            childMap.put(childName, child);
        }
        child.putVal(key, value);
    }

    public boolean hasVal() {
        return val.size() > 0;
    }

    public boolean hasChild() {
        return childMap.size() > 0;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getVal() {
        return val;
    }

    public Collection<AvroLevelData> getChildMap() {
        return childMap.values();
    }
}
