package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroRecord;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * SchemaUtil
 *
 * @author chenqixu
 */
public class SchemaUtil {
    private static final Logger logger = LoggerFactory.getLogger(SchemaUtil.class);
    private String urlStr;

    public SchemaUtil(String urlStr) {
        this.urlStr = urlStr;
        logger.info("urlStr：{}", urlStr);
    }

    /**
     * 传入话题名称，通过springboot服务来获取对应的schema
     *
     * @param topic
     * @return
     */
    public Schema getSchemaByTopic(String topic) {
        return new Schema.Parser().parse(readUrlContent(topic));
    }

    /**
     * 传入schema字符串，直接解析成schema对象
     *
     * @param str
     * @return
     */
    public Schema getSchemaByString(String str) {
        return new Schema.Parser().parse(str);
    }

    /**
     * 通过springboot服务来获取对应的schema
     *
     * @param topic
     * @return
     */
    public String readUrlContent(String topic) {
        StringBuffer contentBuffer = new StringBuffer();
        try {
            BufferedReader reader = null;
            URL url = new URL(urlStr + topic);
            logger.info("{} url：{}", topic, urlStr + topic);
            URLConnection con = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                contentBuffer.append(tmpStr);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("{} schema：{}", topic, contentBuffer.toString());
        return contentBuffer.toString();
    }

    /**
     * 字段处理
     *
     * @param field
     * @param father
     */
    private void dealField(Schema.Field field, AvroRecord father) {
        String field_name = field.name();
        Schema.Type field_type = field.schema().getType();
        // 仅处理有字段名称的数据
        if (field_name != null && field_name.length() > 0) {
            switch (field_type) {
                // 组合类型需要映射出真正的类型
                case UNION:
                    logger.debug("组合类型需要映射出真正的类型field field.name：{}，field.type：{}，field：{}", field_name, field_type, field);
                    // 获取组合类型中的所有类型
                    List<Schema> types = field.schema().getTypes();
                    // 循环判断
                    for (Schema _field_schema : types) {
                        Schema.Type _file_type = _field_schema.getType();
                        switch (_file_type) {
                            case RECORD:
                                logger.debug("RECORD类型，需要递归解析，schema：{}", _field_schema);
                                AvroRecord record = new AvroRecord(field_name, _file_type, _field_schema);
                                father.addChild(record);
                                //需要递归解析
                                dealSchema(_field_schema, record);
                                break;
                            //常见类型
                            case INT:
                            case STRING:
                            case LONG:
                            case FLOAT:
                            case DOUBLE:
                            case BOOLEAN:
                            case MAP:
                            case ARRAY:
                                logger.debug("常见类型，field_name：{}，_file_type：{}", field_name, _file_type);
                                father.addChild(new AvroRecord(field_name, _file_type));
                                break;
                            default:
                                logger.debug("非RECORD也非常见类型，field_name：{}，_file_type：{}，不处理", field_name, _file_type);
                                break;
                        }
                    }
                    break;
                //常见类型
                case INT:
                case STRING:
                case LONG:
                case FLOAT:
                case DOUBLE:
                case BOOLEAN:
                case MAP:
                case ARRAY:
                    father.addChild(new AvroRecord(field_name, field_type));
                    logger.debug("常见类型，field_name：{}，field_type：{}", field_name, field_type);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Schema处理
     *
     * @param schema
     * @param father
     * @return
     */
    public AvroRecord dealSchema(Schema schema, AvroRecord father) {
        String name = schema.getName();
        Schema.Type type = schema.getType();
        if (father == null) {
            father = new AvroRecord(name, type, schema);
        }
        switch (type) {
            case RECORD:
                List<Schema.Field> fields = schema.getFields();
                logger.debug("schema name：{}，type：{}，RECORD类型，fields：{}", name, type, fields);
                if (fields != null && fields.size() > 0) {
                    for (Schema.Field field : fields) {
                        dealField(field, father);
                    }
                }
                break;
            default:
                logger.debug("schema name：{}，type：{}，非RECORD类型", name, type);
                break;
        }
        return father;
    }
}
