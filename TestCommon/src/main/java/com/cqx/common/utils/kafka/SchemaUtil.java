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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * SchemaUtil
 *
 * @author chenqixu
 */
public class SchemaUtil {
    public static final String SCHEMA_MODE = "kafkaconf.newland.schema.mode";
    public static final String SCHEAM_FILE = "kafkaconf.newland.schema.file";
    public static final String SCHEMA_CLUSER_NAME = "kafkaconf.newland.schema.cluster.name";
    public static final String SCHEMA_GROUP_ID = "kafkaconf.newland.schema.group.id";
    public static final String SCHEMA_GROUP_ID_CONSUMER = "kafkaconf.group.id";
    public static final String SCHEMA_URL = "kafkaconf.newland.schema.url";
    public static final String SCHEMA_URL_OLD = "schema_url";
    private static final Logger logger = LoggerFactory.getLogger(SchemaUtil.class);
    private String urlStr;

    public SchemaUtil(String urlStr) {
        this(urlStr, null, null);
    }

    public SchemaUtil(String urlStr, String cluster_name, String group_id) {
        init(urlStr, cluster_name, group_id);
    }

    /**
     * 由于构造(String urlStr)的存在<br>
     * 会产生new SchemaUtil(null)这样的初始化<br>
     * 所以如果使用(Map stormConf)这样的构造，就会冲突，当传入null的时候不知道该执行哪一个构造<br>
     * 所以这个构造就变成了两个参数，并且第一个参数无效
     *
     * @param urlStr    参数无效
     * @param stormConf yaml参数
     */
    public SchemaUtil(String urlStr, Map stormConf) {
        // 先保证原有的配置schema_url可以使用，然后集群和消费者都使用默认值
        String schema_url = (String) stormConf.get(SchemaUtil.SCHEMA_URL_OLD);
        String schema_cluster_name = null;
        String schema_group_id = null;
        // 有多个消费者且各自管理各自schema的情况下，必须使用新配置!!!
        if (schema_url == null || schema_url.length() == 0) {
            logger.warn("配置中找不到schema参数{}，使用新参数{}，{}，{}"
                    , SchemaUtil.SCHEMA_URL_OLD
                    , SchemaUtil.SCHEMA_URL
                    , SchemaUtil.SCHEMA_CLUSER_NAME
                    , SchemaUtil.SCHEMA_GROUP_ID
            );
            schema_url = (String) stormConf.get(SchemaUtil.SCHEMA_URL);
            schema_cluster_name = (String) stormConf.get(SchemaUtil.SCHEMA_CLUSER_NAME);
            // 先使用新参数，新参数找不到的情况下，才会去找消费者配置的消费组ID
            schema_group_id = (String) stormConf.get(SchemaUtil.SCHEMA_GROUP_ID);
            if (schema_group_id == null || schema_group_id.length() == 0) {
                schema_group_id = (String) stormConf.get(SchemaUtil.SCHEMA_GROUP_ID_CONSUMER);
            }
        }
        init(schema_url, schema_cluster_name, schema_group_id);
    }

    private void init(String urlStr, String cluster_name, String group_id) {
        if (cluster_name != null && group_id != null) {
            String tmp_cluster_name = cluster_name;
            if (tmp_cluster_name.length() == 0) {
                tmp_cluster_name = "kafka";
            }
            String tmp_group_id = group_id;
            if (tmp_group_id.length() == 0) {
                tmp_group_id = "default";
            }
            if (urlStr != null) {
                urlStr = urlStr.replace("getSchema?t=", "getSchema?");
                urlStr += String.format("c=%s&g=%s&t=", tmp_cluster_name, tmp_group_id);
            }
        }
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
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                contentBuffer.append(tmpStr);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("{} schema：{}", topic, contentBuffer.toString());
        if (contentBuffer.toString().length() == 0) {
            throw new NullPointerException("无法获取到话题" + topic + "的schema，请检查配置！");
        }
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
