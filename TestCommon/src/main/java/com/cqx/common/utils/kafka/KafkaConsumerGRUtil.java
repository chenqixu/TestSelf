package com.cqx.common.utils.kafka;

import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * KafkaConsumerGRUtil
 * <pre>
 *     开发参数参考：
 *       kafkaconf.bootstrap.servers: "10.1.8.200:9092,10.1.8.201:9092,10.1.8.202:9092"
 *       kafkaconf.key.deserializer: "org.apache.kafka.common.serialization.StringDeserializer"
 *       kafkaconf.value.deserializer: "org.apache.kafka.common.serialization.ByteArrayDeserializer"
 *       kafkaconf.security.protocol: "SASL_PLAINTEXT"
 *       kafkaconf.sasl.mechanism: "PLAIN"
 *       kafkaconf.group.id: "throughput_jstorm"
 *       kafkaconf.enable.auto.commit: "true"
 *       kafkaconf.fetch.min.bytes: "52428800"
 *       kafkaconf.max.poll.records: "12000"
 *       kafkaconf.newland.kafka_username: admin
 *       kafkaconf.newland.kafka_password: admin
 *       schema_url: "http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t="
 * </pre>
 *
 * @author chenqixu
 */
public class KafkaConsumerGRUtil extends KafkaConsumerUtil<String, byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGRUtil.class);
    private SchemaUtil schemaUtil;
    private RecordConvertor recordConvertor = null;
    private Schema schema;
    private String mode;
    private String groupId;
    private String fromTime;

    public KafkaConsumerGRUtil(Map stormConf) throws IOException {
        super(stormConf);
        String schema_url = (String) stormConf.get("schema_url");
        //schema工具类
        schemaUtil = new SchemaUtil(schema_url);
        //模式
        mode = (String) stormConf.get("kafkaconf.newland.consumer.mode");
        //消费时间
        fromTime = (String) stormConf.get("kafkaconf.newland.consumer.fromTime");
        //消费组id
        groupId = (String) stormConf.get("kafkaconf.group.id");
    }

    /**
     * 订阅话题
     *
     * @param topic
     */
    @Override
    public void subscribe(String topic) {
        super.subscribe(topic);
        schema = schemaUtil.getSchemaByTopic(topic);
        //记录转换工具类
        recordConvertor = new RecordConvertor(schema);
        try {
            //模式匹配
            if ("fromBeginning".equals(mode)) {
                fromBeginning();//从头开始消费
                logger.info("消费模式：{}，从头开始消费", mode);
            } else if ("fromEnd".equals(mode)) {
                fromEnd();//从最新位置消费
                logger.info("消费模式：{}，从最新位置消费", mode);
            } else if ("fromTime".equals(mode) && fromTime != null) {
                fromTime(fromTime);
                logger.info("消费模式：{}，从{}开始消费", mode, fromTime);
            } else {
                //从group id的上个位置消费
                if (groupId == null) throw new NullPointerException("group id未配置！请配置参数：kafkaconf.group.id");
                logger.info("从group id：{}，的上个位置消费", groupId);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("kafka模式匹配异常，对应模式：%s", mode), e);
        }
    }

    /**
     * 消费，只有Value，获得List&lt;GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public List<GenericRecord> polls(long timeout) {
        List<byte[]> values = poll(timeout);
        List<GenericRecord> records = new ArrayList<>();
        for (byte[] bytes : values) {
            records.add(getGenericRecord(bytes));
        }
        return records;
    }

    /**
     * 消费，带Key和Value，获得IKVList&lt;String, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<String, GenericRecord> pollsHasKey(long timeout) {
        IKVList<String, byte[]> recordshaskey = pollHasKey(timeout);
        IKVList<String, GenericRecord> records = new KVList<>();
        for (IKVList.Entry<String, byte[]> entry : recordshaskey.entrySet()) {
            records.put(entry.getKey(), getGenericRecord(entry.getValue()));
        }
        return records;
    }

    /**
     * 字节数组转换成GenericRecord
     *
     * @param bytes
     * @return
     */
    private GenericRecord getGenericRecord(byte[] bytes) {
        GenericRecord record;
        try {
            record = recordConvertor.binaryToRecord(bytes);
        } catch (RuntimeException e) {
            logger.error(String.format("GenericRecord转换异常，%s", bytes), e);
            throw e;
        }
        return record;
    }

    /**
     * 如果目前的avro无法成功转换，尝试更新schema，实际上这个业务模型不是很成熟
     *
     * @param bytes
     * @return
     */
    private Object getValueTryToChangeSchema(byte[] bytes) {
        Object obj = null;
        try {
            //尝试能否转换成avro
            obj = getGenericRecord(bytes);
        } catch (Exception e) {
            logger.warn("{} 无法转换成avro，尝试能否更新schema." + new String(bytes));
            //不能转换成avro
            try {
                //就尝试能否更新schema
                updateSchema(new String(bytes));
                //再次尝试转换成avro
                obj = getGenericRecord(bytes);
            } catch (Exception e1) {
                logger.error(String.format("%s 再次尝试转换成avro异常！", new String(bytes)), e1);
            }
        }
        return obj;
    }

    /**
     * 更新schema
     *
     * @param str
     */
    private void updateSchema(String str) {
        //更新schema
        schema = schemaUtil.getSchemaByString(str);
        //重构记录转换工具类
        recordConvertor = new RecordConvertor(schema);
        logger.info("转换Schema成功：{}", str);
    }

    public Schema getSchema() {
        return schema;
    }
}
