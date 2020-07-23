package com.cqx.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GenericRecordUtil
 *
 * @author chenqixu
 */
public class GenericRecordUtil {

    private Map<String, Schema> schemaMap = new HashMap<>();
    private Map<String, Map<String, Schema.Type>> schemaFieldMap = new HashMap<>();
    private Map<String, RecordConvertor> recordConvertorMap = new HashMap<>();
    private SchemaUtil schemaUtil;

    public GenericRecordUtil() {
        this(null);
    }

    public GenericRecordUtil(String schemaUrl) {
        if (schemaUrl != null && schemaUrl.length() > 0) {
            this.schemaUtil = new SchemaUtil(schemaUrl);
        } else {
            this.schemaUtil = new SchemaUtil();
        }
    }

    public void addTopic(String topic) {
        Schema schema;
        if (schemaUtil.isLocal()) {
            schema = schemaUtil.getSchemaByTopic(topic);
        } else {
            schema = schemaUtil.getSchemaByUrlTopic(topic);
        }
        schemaMap.put(topic, schema);
        recordConvertorMap.put(topic, new RecordConvertor(schema));
        // 获取字段类型，进行映射，防止不规范写法
        Map<String, Schema.Type> _schemaFieldMap = new HashMap<>();
        // 获取字段
        for (Schema.Field field : schema.getFields()) {
            // 字段名称
            String name = field.name();
            /**
             * 字段类型
             * RECORD, ENUM, ARRAY, MAP, UNION, FIXED, STRING, BYTES,
             * INT, LONG, FLOAT, DOUBLE, BOOLEAN, NULL;
             */
            Schema.Type type = field.schema().getType();
            // 仅处理有字段名称的数据
            if (name != null && name.length() > 0) {
                // 判断字段类型
                switch (type) {
                    // 组合类型需要映射出真正的类型
                    case UNION:
                        // 获取组合类型中的所有类型
                        List<Schema> types = field.schema().getTypes();
                        // 循环判断
                        for (Schema schema1 : types) {
                            Schema.Type type1 = schema1.getType();
                            if (type1.equals(Schema.Type.INT) ||
                                    type1.equals(Schema.Type.STRING) ||
                                    type1.equals(Schema.Type.LONG) ||
                                    type1.equals(Schema.Type.FLOAT) ||
                                    type1.equals(Schema.Type.DOUBLE) ||
                                    type1.equals(Schema.Type.BOOLEAN)
                            ) {
                                _schemaFieldMap.put(name, type1);
                                break;
                            }
                        }
                        break;
                    default:
                        _schemaFieldMap.put(name, type);
                        break;
                }
            }
        }
        schemaFieldMap.put(topic, _schemaFieldMap);
    }

    public byte[] genericRecord(String topic, Map<String, String> values) {
        Schema schema = schemaMap.get(topic);
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        GenericRecord genericRecord = new GenericData.Record(schema);
        Map<String, Schema.Type> _schemaFieldMap = schemaFieldMap.get(topic);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String field = schema.getField(entry.getKey()).name();
            Schema.Type type = _schemaFieldMap.get(field);
            if (field != null && field.length() > 0) {
                String value = entry.getValue();
                Object obj = value;
                switch (type) {
                    case INT:
                        if (value == null || value.length() == 0) {
                            obj = 0;
                        } else {
                            obj = Integer.valueOf(value);
                        }
                        break;
                    case LONG:
                        if (value == null || value.length() == 0) {
                            obj = 0L;
                        } else {
                            obj = Long.valueOf(value);
                        }
                        break;
                    case FLOAT:
                        if (value == null || value.length() == 0) {
                            obj = 0F;
                        } else {
                            obj = Float.valueOf(value);
                        }
                        break;
                    case DOUBLE:
                        if (value == null || value.length() == 0) {
                            obj = 0D;
                        } else {
                            obj = Double.valueOf(value);
                        }
                        break;
                    case BOOLEAN:
                        if (value == null || value.length() == 0) {
                            obj = false;
                        } else {
                            obj = Boolean.valueOf(value);
                        }
                        break;
                    default:
                        break;
                }
                genericRecord.put(field, obj);
            }
        }
        return recordConvertor.recordToBinary(genericRecord);
    }

}
