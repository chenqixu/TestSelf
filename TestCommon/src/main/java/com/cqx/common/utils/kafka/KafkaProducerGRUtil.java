package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.IOException;
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
    private GenericRecordUtil genericRecordUtil;
    private String topic;
    private Schema schema;

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
    private void initGR(Map stormConf) {
        String schema_url = (String) stormConf.get("schema_url");
        //初始化工具类
        genericRecordUtil = new GenericRecordUtil(schema_url);
    }

    public void setTopic(String topic) {
        //初始化schema
        this.topic = topic;
        genericRecordUtil.addTopic(topic);
        schema = genericRecordUtil.getSchema(topic);
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
}
