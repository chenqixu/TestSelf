package com.cqx.common.utils.kafka;

import java.io.IOException;
import java.util.Map;

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

    public KafkaProducerGRUtil(Map stormConf) throws IOException {
        super(stormConf);
        String schema_url = (String) stormConf.get("schema_url");
        //初始化工具类
        genericRecordUtil = new GenericRecordUtil(schema_url);
    }

    public void setTopic(String topic) {
        //初始化schema
        this.topic = topic;
        genericRecordUtil.addTopic(topic);
    }

    /**
     * 发送随机数据
     */
    public void sendRandom() {
        send(topic, genericRecordUtil.genericRandomRecordByAvroRecord(topic));
    }

    /**
     * 发送随机数据，可以自行调整部分字段内容
     *
     * @param param
     */
    public void sendRandom(Map<String, String> param) {
        send(topic, genericRecordUtil.genericRandomRecord(topic, param));
    }

    public void sends(String kafkaKey, Map<String, String> kafkaValue) {
        byte[] msg = genericRecordUtil.genericRecord(topic, kafkaValue);
        if (kafkaKey != null) {
            send(topic, kafkaKey, msg);
        } else {
            send(topic, msg);
        }
    }

    public void sends(Map<String, String> kafkaValue) {
        sends(null, kafkaValue);
    }
}
