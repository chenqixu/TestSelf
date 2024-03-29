package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.AvroRecord;
import com.cqx.common.bean.kafka.DefaultBean;
import org.apache.avro.AvroRuntimeException;
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
    private static final Logger logger = LoggerFactory.getLogger(GenericRecordUtil.class);
    private Map<String, Schema> schemaMap = new HashMap<>();
    private Map<String, AvroRecord> avroRecordMap = new HashMap<>();
    private Map<String, Map<String, Schema.Type>> schemaFieldMap = new HashMap<>();
    private Map<String, RecordConvertor> recordConvertorMap = new HashMap<>();
    private SchemaUtil schemaUtil;
    private DefaultBean defaultBean = new DefaultBean();

    public GenericRecordUtil(String schemaUrl) {
        // 初始化schema工具类
        schemaUtil = new SchemaUtil(schemaUrl);
    }

    public GenericRecordUtil(String schemaUrl, Map stormConf) {
        // 初始化schema工具类
        schemaUtil = new SchemaUtil(schemaUrl, stormConf);
    }

    /**
     * 避免和构造public GenericRecordUtil(String schemaUrl)冲突，加了一个不使用的参数
     *
     * @param schemaUtil
     * @param no_use_param
     */
    public GenericRecordUtil(SchemaUtil schemaUtil, String no_use_param) {
        // 初始化schema工具类
        this.schemaUtil = schemaUtil;
    }

    /**
     * 话题初始化，从远程服务获取话题的schema
     *
     * @param topic 话题名称
     */
    public void addTopic(String topic) {
        // 从远程服务获取话题的schema
        Schema schema = schemaUtil.getSchemaByTopic(topic);
        // 初始化话题和对应的schema
        init(topic, schema);
    }

    /**
     * 根据传入的schema字符串对话题进行初始化
     *
     * @param topic        话题名称
     * @param schemaString schema
     */
    public void addTopicBySchemaString(String topic, String schemaString) {
        // 通过字符串解析话题的schema
        Schema schema = schemaUtil.getSchemaByString(schemaString);
        // 初始化话题和对应的schema
        init(topic, schema);
    }

    /**
     * 初始化话题和对应的schema
     *
     * @param topic  话题名称
     * @param schema schema
     */
    private void init(String topic, Schema schema) {
        logger.info("init，topic：{}，schema：{}", topic, schema);
        // 把话题和schema加入映射关系的map中
        schemaMap.put(topic, schema);
        // 构造对应的记录转换器，并加入map
        recordConvertorMap.put(topic, new RecordConvertor(schema));
        // 根据获取的schema，构造一个AvroRecord对象
        AvroRecord avroRecord = schemaUtil.dealSchema(schema, null);
        // 把AvroRecord对象加入map
        avroRecordMap.put(topic, avroRecord);
        //########################################
        // 把字段类型加入map中，只适用于字段平铺开来的情况
        //########################################
        addSchemaFieldMap(topic, schema);
    }

    /**
     * 只适用于字段平铺开来的情况
     *
     * @param topic
     * @param schema
     */
    private void addSchemaFieldMap(String topic, Schema schema) {
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

    /**
     * 通过解析Map，生成一条Record
     *
     * @param topic
     * @param values
     * @return
     */
    public byte[] genericRecord(String topic, Map<String, String> values) {
        // 通过话题名称获取对应schema
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
                    case LONG:
                    case BOOLEAN:
                    case FLOAT:
                    case DOUBLE:
                        obj = defaultBean.getValue(type, value);
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
                    case FLOAT:
                    case DOUBLE:
                    case LONG:
                    case BOOLEAN:
                        obj = defaultBean.getDefaultValue(type);
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
     * 随机产生一条数据，可以自行调整
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
                    case FLOAT:
                    case DOUBLE:
                    case LONG:
                    case BOOLEAN:
                        obj = defaultBean.getValue(type, val);
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
     * 解析AvroRecord，产生GenericRecord
     *
     * @param avroRecord
     * @param father
     * @param isRoot
     */
    private void genericByAvroRecord(AvroRecord avroRecord, GenericRecord father, boolean isRoot) {
        if (avroRecord.hasChild()) {
            logger.debug("getSchema：{}", avroRecord.getSchema());
            GenericRecord realUse = father;
            if (!isRoot) {
                realUse = new GenericData.Record(avroRecord.getSchema());
                father.put(avroRecord.getName(), realUse);
            }
            for (AvroRecord child : avroRecord.getChilds()) {
                genericByAvroRecord(child, realUse, false);
            }
        } else {
            Schema.Type type = avroRecord.getType();
            Object obj;
            switch (type) {
                case INT:
                case FLOAT:
                case DOUBLE:
                case LONG:
                case BOOLEAN:
                case STRING:
                case ARRAY:
                case MAP:
                    obj = defaultBean.getDefaultValue(type);
                    break;
                case BYTES:
                case UNION:
                case ENUM:
                case NULL:
                case FIXED:
                case RECORD:
                default:
                    throw new AvroRuntimeException("不支持的类型：" + type);
            }
            father.put(avroRecord.getName(), obj);
            logger.debug("father.put {} , {}，type：{}", avroRecord.getName(), obj, type);
        }
    }

    /**
     * 从AvroRecord找到匹配的父级
     *
     * @param genericRecord
     * @param avroRecord
     * @param name
     * @return
     */
    private GenericRecord findAvroRecord(GenericRecord genericRecord, AvroRecord avroRecord, String name) {
        if (avroRecord.getName().equals(name)) {
            // 返回父级本身
            return genericRecord;
        } else {
            if (avroRecord.hasChild()) {
                // 查找子级
                for (AvroRecord child : avroRecord.getChilds()) {
                    if (child.getName().equals(name)) {
                        // 递归调用，如果是父级就返回本身，否则继续子级查找
                        return findAvroRecord((GenericRecord) genericRecord.get(name), child, name);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 设置参数到GenericRecord
     *
     * @param genericRecord
     * @param setValue
     */
    private void putGR(GenericRecord genericRecord, Map<String, Object> setValue) {
        if (setValue != null) {
            for (Map.Entry<String, Object> entry : setValue.entrySet()) {
                genericRecord.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 从AvroRecord找到匹配的父级，然后设置到对应的子级去
     *
     * @param genericRecord
     * @param avroRecord
     * @param avroLevelData
     */
    private void findAndSet(GenericRecord genericRecord, AvroRecord avroRecord, AvroLevelData avroLevelData) {
        if (avroLevelData != null && avroLevelData.hasVal()) {
            // 从AvroRecord找到匹配的父级
            GenericRecord fatherGR = findAvroRecord(genericRecord, avroRecord, avroLevelData.getName());
            if (fatherGR == null) {
                throw new NullPointerException("找不到匹配的父级：" + avroLevelData.getName());
            }
            // 设置参数
            putGR(fatherGR, avroLevelData.getVal());
            // 如果有子节点
            if (avroLevelData.hasChild()) {
                for (AvroLevelData entry : avroLevelData.getChildMap()) {
                    // 递归
                    findAndSet(genericRecord, avroRecord, entry);
                }
            }
        }
    }

    /**
     * 通过解析出来的AvroRecord树产生一条数据
     *
     * @param topic
     * @return
     */
    public byte[] genericRandomRecordByAvroRecord(String topic, AvroLevelData avroLevelData) {
        Schema schema = schemaMap.get(topic);
        AvroRecord avroRecord = avroRecordMap.get(topic);
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        GenericRecord genericRecord = new GenericData.Record(schema);
        genericByAvroRecord(avroRecord, genericRecord, true);
        // 从AvroRecord找到匹配的父级，然后设置到对应的子级去
        findAndSet(genericRecord, avroRecord, avroLevelData);
        logger.debug("genericRandomRecord：{}", genericRecord);
        return recordConvertor.recordToBinary(genericRecord);
    }

    /**
     * 通过解析出来的AvroRecord树产生一条数据
     *
     * @param topic
     * @return
     */
    public byte[] genericRandomRecordByAvroRecord(String topic) {
        return genericRandomRecordByAvroRecord(topic, null);
    }

    /**
     * 把GenericRecord转换成byte[]
     *
     * @param topic
     * @param genericRecord
     * @return
     */
    public byte[] recordToBinary(String topic, GenericRecord genericRecord) {
        RecordConvertor recordConvertor = recordConvertorMap.get(topic);
        return recordConvertor.recordToBinary(genericRecord);
    }

    /**
     * 返回话题的schema
     *
     * @param topic
     * @return
     */
    public Schema getSchema(String topic) {
        return schemaMap.get(topic);
    }

    /**
     * 设置默认值
     *
     * @param defaultBean
     */
    public void setDefaultBean(DefaultBean defaultBean) {
        this.defaultBean = defaultBean;
    }
}
