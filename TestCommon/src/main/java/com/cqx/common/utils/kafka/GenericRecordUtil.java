package com.cqx.common.utils.kafka;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GenericRecordUtil
 *
 * @author chenqixu
 */
public class GenericRecordUtil {

    private static Logger logger = LoggerFactory.getLogger(GenericRecordUtil.class);
    private Map<String, Schema> schemaMap = new HashMap<>();
    private Map<String, Map<String, Schema.Type>> schemaFieldMap = new HashMap<>();
    private Map<String, RecordConvertor> recordConvertorMap = new HashMap<>();
    private String schemaUrl;
    private SchemaUtil schemaUtil;

    public GenericRecordUtil(String schemaUrl) {
        this.schemaUrl = schemaUrl;
        // 初始化schema工具类
        schemaUtil = new SchemaUtil(schemaUrl);
    }

    public void addTopic(String topic) {
        Schema schema = schemaUtil.getSchemaByTopic(topic);
        logger.info("addTopic，topic：{}，schema：{}", topic, schema);
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
            if (field != null && field.length() > 0) {
                Schema.Type type = _schemaFieldMap.get(field);
                String value = (entry.getValue() == null ? "" : entry.getValue());
                Object obj = value;
                switch (type) {
                    case INT:
                        if (value.length() == 0) {
                            obj = 0;
                        } else {
                            obj = Integer.valueOf(value);
                        }
                        break;
                    case LONG:
                        if (value.length() == 0) {
                            obj = 0L;
                        } else {
                            obj = Long.valueOf(value);
                        }
                        break;
                    case BOOLEAN:
                        if (value.length() == 0) {
                            obj = false;
                        } else {
                            obj = Boolean.valueOf(value);
                        }
                        break;
                    case FLOAT:
                        if (value.length() == 0) {
                            obj = 0;
                        } else {
                            obj = Float.valueOf(value);
                        }
                        break;
                    case DOUBLE:
                        if (value.length() == 0) {
                            obj = 0;
                        } else {
                            obj = Double.valueOf(value);
                        }
                        break;
                    case BYTES:
                    case UNION:
                    case MAP:
                    case ENUM:
                    case NULL:
                    case ARRAY:
                    case FIXED:
                    case RECORD:
                    case STRING:
                    default:
                        break;
                }
                genericRecord.put(field, obj);
            }
        }
        logger.debug("genericRecord：{}", genericRecord);
        return recordConvertor.recordToBinary(genericRecord);
    }

    /**
     * 随机产生一条数据
     *
     * @param topic
     * @return
     */
    public byte[] genericRandomRecord(String topic) {
        Schema schema = schemaMap.get(topic);
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        GenericRecord genericRecord = new GenericData.Record(schema);
        Map<String, Schema.Type> _schemaFieldMap = schemaFieldMap.get(topic);
        for (Map.Entry<String, Schema.Type> entry : _schemaFieldMap.entrySet()) {
            String field = schema.getField(entry.getKey()).name();
            if (field != null && field.length() > 0) {
                Schema.Type type = _schemaFieldMap.get(field);
                Object obj = "";
                switch (type) {
                    case INT:
                        obj = 0;
                        break;
                    case FLOAT:
                        obj = (float) 0;
                        break;
                    case DOUBLE:
                        obj = (double) 0;
                        break;
                    case LONG:
                        obj = (long) 0;
                        break;
                    case BOOLEAN:
                        obj = false;
                        break;
                    case BYTES:
                    case UNION:
                    case MAP:
                    case ENUM:
                    case NULL:
                    case ARRAY:
                    case FIXED:
                    case RECORD:
                    case STRING:
                    default:
                        break;
                }
                genericRecord.put(field, obj);
            }
        }
        logger.info("genericRandomRecord：{}", genericRecord);
        return recordConvertor.recordToBinary(genericRecord);
    }

    /**
     * 随机产生一条数据
     *
     * @param topic
     * @param param
     * @return
     */
    public byte[] genericRandomRecord(String topic, Map<String, String> param) {
        Schema schema = schemaMap.get(topic);
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        GenericRecord genericRecord = new GenericData.Record(schema);
        Map<String, Schema.Type> _schemaFieldMap = schemaFieldMap.get(topic);
        for (Map.Entry<String, Schema.Type> entry : _schemaFieldMap.entrySet()) {
            String field = schema.getField(entry.getKey()).name();
            if (field != null && field.length() > 0) {
                Object obj = "";
                String val = param.get(field);
                if (val != null) obj = val;
                Schema.Type type = _schemaFieldMap.get(field);
                switch (type) {
                    case INT:
                        obj = ((val != null) ? Integer.valueOf(val) : 0);
                        break;
                    case FLOAT:
                        obj = ((val != null) ? Float.valueOf(val) : (float) 0);
                        break;
                    case DOUBLE:
                        obj = ((val != null) ? Double.valueOf(val) : (double) 0);
                        break;
                    case LONG:
                        obj = ((val != null) ? Long.valueOf(val) : (long) 0);
                        break;
                    case BOOLEAN:
                        obj = ((val != null) ? Boolean.valueOf(val) : false);
                        break;
                    case BYTES:
                    case UNION:
                    case MAP:
                    case ENUM:
                    case NULL:
                    case ARRAY:
                    case FIXED:
                    case RECORD:
                    case STRING:
                    default:
                        break;
                }
                genericRecord.put(field, obj);
            }
        }
        logger.info("genericRandomRecord：{}", genericRecord);
        return recordConvertor.recordToBinary(genericRecord);
    }
}
