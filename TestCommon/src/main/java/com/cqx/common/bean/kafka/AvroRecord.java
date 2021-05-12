package com.cqx.common.bean.kafka;

import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Record
 *
 * @author chenqixu
 */
public class AvroRecord {
    private String name;
    private Schema.Type type;
    private Schema schema;
    private List<AvroRecord> childs = new ArrayList<>();

    public AvroRecord(String name, Schema.Type type) {
        this(name, type, null);
    }

    public AvroRecord(String name, Schema.Type type, Schema schema) {
        this.name = name;
        this.type = type;
        this.schema = schema;
    }

    public void addChild(AvroRecord avroRecord) {
        childs.add(avroRecord);
    }

    public boolean hasChild() {
        return childs.size() > 0;
    }

    public List<AvroRecord> getChilds() {
        return childs;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public Schema.Type getType() {
        return type;
    }
}
