package com.cqx.common.utils.kafka;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

/**
 * OggRecord
 *
 * @author chenqixu
 */
public class OggRecord {
    private GenericRecord genericRecord;
    private Schema schema;
    private boolean isSchema = false;// 默认不是schema
    private boolean isRecord = true;// 默认是数据
    private long offset;

    public GenericRecord getGenericRecord() {
        return genericRecord;
    }

    public void setGenericRecord(GenericRecord genericRecord) {
        this.genericRecord = genericRecord;
    }

    public Schema getSchema() {
        return schema;
    }

    public boolean isSchema() {
        return isSchema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void setSchema(boolean schema) {
        isSchema = schema;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void setRecord(boolean record) {
        isRecord = record;
    }

    public void updateSchema(Schema schema) {
        setRecord(false);
        setSchema(true);
        setSchema(schema);
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
