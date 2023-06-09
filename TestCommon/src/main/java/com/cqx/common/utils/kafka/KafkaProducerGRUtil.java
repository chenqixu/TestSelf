package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.param.ParamUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * KafkaProducerGRUtil
 * <pre>
 *     开发参数参考：
 *       kafkaconf.bootstrap.servers: "10.1.8.200:9092,10.1.8.201:9092,10.1.8.202:9092"
 *       kafkaconf.key.serializer: "org.apache.kafka.common.serialization.StringSerializer"
 *       kafkaconf.value.serializer: "org.apache.kafka.common.serialization.ByteArraySerializer"
 *       kafkaconf.security.protocol: "SASL_PLAINTEXT"
 *       kafkaconf.sasl.mechanism: "PLAIN"
 *       kafkaconf.newland.kafka_username: admin
 *       kafkaconf.newland.kafka_password: admin
 *       schema_url: "http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t="
 * </pre>
 *
 * @author chenqixu
 */
public class KafkaProducerGRUtil extends KafkaProducerUtil<String, byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerGRUtil.class);
    private GenericRecordUtil genericRecordUtil;
    private String topic;
    private Schema schema;
    private String schemaMode;
    private SchemaUtil schemaUtil;
    private String avscStr;

    public KafkaProducerGRUtil(Map stormConf) throws IOException {
        super(stormConf);
        initGR(stormConf);
    }

    public KafkaProducerGRUtil(Map stormConf, boolean isTransaction) throws IOException {
        super(stormConf, isTransaction);
        initGR(stormConf);
    }

    /**
     * 初始化
     *
     * @param stormConf
     */
    private void initGR(Map stormConf) throws IOException {
        // 新增schema读取模式：[URL|FILE]，默认是URL
        try {
            schemaMode = ParamUtil.setValDefault(stormConf, SchemaUtil.SCHEMA_MODE, KafkaConsumerGRUtil.schemaMode_URL);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            // 默认是URL
            schemaMode = KafkaConsumerGRUtil.schemaMode_URL;
        }
        // schema模式匹配
        switch (schemaMode) {
            // 非avro
            case KafkaConsumerGRUtil.schemaMode_NOAVRO:
                break;
            // 本地文件模式
            case KafkaConsumerGRUtil.schemaMode_FILE:
                // 读取avsc文件
                String avsc_file = (String) stormConf.get(SchemaUtil.SCHEAM_FILE);
                if (avsc_file == null || avsc_file.trim().length() == 0 || !FileUtil.isExists(avsc_file)) {
                    throw new RuntimeException(
                            String.format("初始化失败，读取avsc文件异常！%s：%s", SchemaUtil.SCHEAM_FILE, avsc_file)
                    );
                }
                avscStr = KafkaConsumerGRUtil.avscFromFile(avsc_file);
                logger.info("从 {} 文件读取avsc文件内容：{}", avsc_file, avscStr);
                schemaUtil = new SchemaUtil(null);
                break;
            // 远程服务、默认模式
            case KafkaConsumerGRUtil.schemaMode_URL:
            default:
                //schema工具类
                schemaUtil = new SchemaUtil(null, stormConf);
                break;
        }
        // 初始化GenericRecord工具类
        genericRecordUtil = new GenericRecordUtil(schemaUtil);
    }

    public void setTopic(String topic) {
        //初始化schema
        this.topic = topic;
        // schema模式匹配
        switch (schemaMode) {
            case KafkaConsumerGRUtil.schemaMode_NOAVRO:
                break;
            case KafkaConsumerGRUtil.schemaMode_FILE:
                // schema从本地文件获取
                genericRecordUtil.addTopicBySchemaString(topic, avscStr);
                schema = genericRecordUtil.getSchema(topic);
                break;
            case KafkaConsumerGRUtil.schemaMode_URL:
            default:
                // schema从远程服务器获取
                genericRecordUtil.addTopic(topic);
                schema = genericRecordUtil.getSchema(topic);
                break;
        }
    }

    public void setTopic(String topic, String schemaString) {
        //初始化schema
        this.topic = topic;
        genericRecordUtil.addTopicBySchemaString(topic, schemaString);
        schema = genericRecordUtil.getSchema(topic);
    }

    /**
     * 发送随机数据
     */
    public Future<RecordMetadata> sendRandom() {
        return send(topic, genericRecordUtil.genericRandomRecordByAvroRecord(topic));
    }

    /**
     * 发送随机数据，可以自行调整部分字段内容
     *
     * @param param
     */
    public Future<RecordMetadata> sendRandom(Map<String, String> param) {
        return send(topic, genericRecordUtil.genericRandomRecord(topic, param));
    }

    /**
     * 发送一条已经构建好的AvroLevelData数据，未构建的字段使用默认值
     *
     * @param avroLevelData
     * @return
     */
    public Future<RecordMetadata> sendRandom(AvroLevelData avroLevelData) {
        return send(topic, genericRecordUtil.genericRandomRecordByAvroRecord(topic, avroLevelData));
    }

    public Future<RecordMetadata> sends(String kafkaKey, Map<String, String> kafkaValue) {
        byte[] msg = genericRecordUtil.genericRecord(topic, kafkaValue);
        if (kafkaKey != null) {
            return send(topic, kafkaKey, msg);
        } else {
            return send(topic, msg);
        }
    }

    public Future<RecordMetadata> sends(Map<String, String> kafkaValue) {
        return sends(null, kafkaValue);
    }

    public Future<RecordMetadata> sends(String kafkaKey, GenericRecord genericRecord) {
        byte[] msg = genericRecordUtil.recordToBinary(topic, genericRecord);
        if (kafkaKey != null) {
            return send(topic, kafkaKey, msg);
        } else {
            return send(topic, msg);
        }
    }

    public Future<RecordMetadata> sends(GenericRecord genericRecord) {
        return sends(null, genericRecord);
    }

    public Schema getSchema() {
        return schema;
    }

    public void setDefaultBean(DefaultBean defaultBean) {
        genericRecordUtil.setDefaultBean(defaultBean);
    }

    public GenericRecordUtil getGenericRecordUtil() {
        return genericRecordUtil;
    }
}
