package com.cqx.common.utils.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扁平化工具
 *
 * @author chenqixu
 */
public class FlatUtil {
    private static final Logger logger = LoggerFactory.getLogger(FlatUtil.class);
    // <字段名小写>与<字段名>的映射关系
    private Map<String, String> schemaFieldMap = new HashMap<>();
    // 消费者Schema
    private Schema oggSchema;
    // 生产者Schema
    private Schema flatSchema;
    // 生产者所有fields
    private JSONArray keys;
    // 消费者话题
    private String oggTopic;

    public FlatUtil(String oggTopic, Schema oggSchema, Schema flatSchema) {
        this.oggTopic = oggTopic;
        this.oggSchema = oggSchema;
        this.flatSchema = flatSchema;
        // 映射关系初始化
        initSchemaFieldMap();
        // 获取生产者所有fields
        this.keys = ((JSONObject) JSON.parse(this.flatSchema.toString())).getJSONArray("fields");
    }

    /**
     * 处理单条记录
     *
     * @param record
     * @return
     */
    public GenericRecord flat(GenericRecord record) {
        if (record == null) {
            return null;
        }

        GenericRecord sendRecord = new GenericData.Record(flatSchema);

        int keySize = this.keys.size();
        for (int i = 0; i < keySize; i++) {
            String attrName = this.keys.getJSONObject(i).getString("name");
            String attrVal = this.schemaFieldMap.get(attrName);
            Object value = null;

            // 取值
            if (attrName.startsWith("before_")) {
                GenericRecord beforeRecord = (GenericRecord) record.get("before");
                if (beforeRecord != null)
                    value = beforeRecord.get(attrVal);
            } else if (attrName.startsWith("after_")) {
                GenericRecord afterRecord = (GenericRecord) record.get("after");
                if (afterRecord != null)
                    value = afterRecord.get(attrVal);
            } else {
                value = record.get(attrVal);
            }

            String type = null;
            boolean canBeNull = false;
            {// 判断type能不能接受null值
                Object typeObj = this.keys.getJSONObject(i).get("type");
                if (typeObj == null)
                    continue;
                else if (typeObj instanceof JSONArray) {
                    JSONArray typeAry = (JSONArray) typeObj;
                    for (int j = 0; j < typeAry.size(); j++) {
                        if ("null".equals(typeAry.getString(j)))
                            canBeNull = true;
                        type = typeAry.getString(j);
                    }
                } else {
                    type = typeObj.toString();
                }
            }

            // null处理
            if (value == null && !canBeNull) {
                if ("string".equals(type))
                    value = "null";
                if ("boolean".equals(type))
                    value = Boolean.FALSE;
                if ("int".equals(type))
                    value = 0;
                if ("long".equals(type))
                    value = 0L;
                if ("float".equals(type))
                    value = (float) 0.0;
                if ("double".equals(type))
                    value = 0.0;
            }
            if ("string".equals(type) && value != null) {
                value = value.toString();
            }

            sendRecord.put(attrName, value);
        }

        return sendRecord;
    }

    /**
     * 处理多条记录
     *
     * @param records
     * @return
     */
    public List<GenericRecord> flat(List<GenericRecord> records) {
        List<GenericRecord> flatRet = new ArrayList<>();
        if (records == null || records.size() == 0) {
            return null;
        }

        for (GenericRecord record : records) {
            // 处理完一条记录，加入List
            flatRet.add(flat(record));
        }
        return flatRet;
    }

    /**
     * 映射关系初始化
     */
    private void initSchemaFieldMap() {
        // <字段名小写>与<字段名>的映射关系
        Schema sc = oggSchema;
        schemaFieldMap.clear();
        if (null != sc) {
            for (Schema.Field field : sc.getFields()) {
                schemaFieldMap.put(field.name().toLowerCase(), field.name());
            }
            List<Schema.Field> fields = sc.getFields();
            for (Schema.Field field : fields) {
                if (Schema.Type.STRING == field.schema().getType()) {
                    schemaFieldMap.put(field.name().toLowerCase(), field.name());
                } else {
                    Schema inSchema = getInRecordSchema(field);
                    if (null != inSchema) {
                        String pre = field.name() + "_";
                        for (Schema.Field field2 : inSchema.getFields()) {
                            if (Schema.Type.UNION == field2.schema().getType()) {
                                schemaFieldMap.put((pre + field2.name()).toLowerCase(), field2.name());
                            } else {
                                schemaFieldMap.put((pre + field2.name()).toLowerCase(), field2.name());
                            }
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException(oggTopic + "的scheam为空！");
        }
    }

    /**
     * 获取UNION下RECORD的schema
     *
     * @param field
     * @return
     */
    private Schema getInRecordSchema(Schema.Field field) {
        if (Schema.Type.UNION == field.schema().getType()) {
            List<Schema> typeSchemas = field.schema().getTypes();
            for (Schema sc : typeSchemas) {
                if (Schema.Type.RECORD == sc.getType())
                    return sc;
            }
        }
        return null;
    }
}
