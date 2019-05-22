package com.cqx.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * GenericRecordUtil
 *
 * @author chenqixu
 */
public class GenericRecordUtil {

    private Map<String, Schema> schemaMap = new HashMap<>();
    private Map<String, RecordConvertor> recordConvertorMap = new HashMap<>();
    private String schemaUrl;
    private SchemaUtil schemaUtil;

    public GenericRecordUtil(String schemaUrl) {
        this.schemaUrl = schemaUrl;
        // 初始化schema工具类
        schemaUtil = new SchemaUtil(this.schemaUrl);
    }

    public void addTopic(String topic) {
        Schema schema = schemaUtil.getSchemaByTopic(topic);
        schemaMap.put(topic, schema);
        recordConvertorMap.put(topic, new RecordConvertor(schema));
    }

    public byte[] genericRecord(String topic, Map<String, String> values) {
        Schema schema = schemaMap.get(topic);
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        GenericRecord genericRecord = new GenericData.Record(schema);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String field = schema.getField(entry.getKey()).name();
            if (field != null && field.length() > 0)
                genericRecord.put(field, entry.getValue());
        }
        return recordConvertor.recordToBinary(genericRecord);
    }
}
