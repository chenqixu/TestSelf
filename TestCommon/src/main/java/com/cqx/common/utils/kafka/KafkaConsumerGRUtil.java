package com.cqx.common.utils.kafka;

import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String fromOffset;
    private Map<TopicPartition, Long> topicPartitionOffsetMap;

    public KafkaConsumerGRUtil(Map stormConf) throws IOException {
        super(stormConf);
        String schema_url = (String) stormConf.get("schema_url");
        // schema工具类
        schemaUtil = new SchemaUtil(schema_url);
        // 模式
        mode = (String) stormConf.get("kafkaconf.newland.consumer.mode");
        // 消费时间
        fromTime = (String) stormConf.get("kafkaconf.newland.consumer.fromTime");
        // 消费组id
        groupId = (String) stormConf.get("kafkaconf.group.id");
        // 消费位置
        fromOffset = (String) stormConf.get("kafkaconf.newland.consumer.fromOffset");
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
            } else if ("fromOffset".equals(mode)) {
                if (fromOffset != null && fromOffset.trim().length() > 0) {
                    topicPartitionOffsetMap = new HashMap<>();
                    String[] offsetArray = fromOffset.split(";", -1);
                    for (String topicPartition : offsetArray) {
                        String[] topicPartitionArray = topicPartition.split(",", -1);
                        if (topicPartitionArray.length == 2) {
                            int partition = Integer.valueOf(topicPartitionArray[0]);
                            long offset = Long.valueOf(topicPartitionArray[1]);
                            topicPartitionOffsetMap.put(new TopicPartition(topic, partition), offset);
                        }
                    }
                }
                if (topicPartitionOffsetMap.size() > 0) {
                    fromOffset(topicPartitionOffsetMap);
                    for (Map.Entry<TopicPartition, Long> entry : topicPartitionOffsetMap.entrySet()) {
                        logger.info("消费模式：{}，分区{}从{}开始消费", mode, entry.getKey(), entry.getValue());
                    }
                } else {
                    throw new NullPointerException("fromOffset配置不正确！格式请参考：分区,偏移量，多个分区之间用分号分割");
                }
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
     * 消费，带offset，获得IKVList&lt;Long, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<Long, GenericRecord> pollsHasOffset(long timeout) {
        IKVList<Long, GenericRecord> records = new KVList<>();
        for (IKVList.Entry<ConsumerRecord<String, byte[]>, GenericRecord> entry : pollsHasConsumerRecord(timeout).entrySet()) {
            records.put(entry.getKey().offset(), entry.getValue());
        }
        return records;
    }

    /**
     * 消费，返回IKVList&lt;ConsumerRecord, GenericRecord&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<ConsumerRecord<String, byte[]>, GenericRecord> pollsHasConsumerRecord(long timeout) {
        List<ConsumerRecord<String, byte[]>> recordshasConsumerRecord = pollHasConsumerRecord(timeout);
        IKVList<ConsumerRecord<String, byte[]>, GenericRecord> records = new KVList<>();
        for (ConsumerRecord<String, byte[]> entry : recordshasConsumerRecord) {
            records.put(entry, getGenericRecord(entry.value()));
        }
        return records;
    }

    /**
     * 消费，处理成功则提交，异常则回滚，回滚异常则抛出运行时异常
     *
     * @param timeout
     * @param abstractKafkaUtil
     */
    public void poll(long timeout, AbstractKafkaUtil<ConsumerRecord<String, byte[]>, GenericRecord> abstractKafkaUtil) {
        long firstOffset = -1L;
        TopicPartition topicPartition = null;
        try {
            // 先消费，再转换成GenericRecord
            List<ConsumerRecord<String, byte[]>> records = pollHasConsumerRecord(timeout);
            if (records.size() > 0) {
                // 记录第一个offset
                firstOffset = records.get(0).offset();
                // 记录话题分区
                topicPartition = new TopicPartition(records.get(0).topic(), records.get(0).partition());
                // 转成GenericRecord
                IKVList<ConsumerRecord<String, byte[]>, GenericRecord> genericRecordIKVList = new KVList<>();
                for (ConsumerRecord<String, byte[]> entry : records) {
                    genericRecordIKVList.put(entry, getGenericRecord(entry.value()));
                }
                // 成功处理
                if (abstractKafkaUtil.callBack(genericRecordIKVList.entrySet())) {
                    // 提交
                    commitSync();
                }
            }
        } catch (Exception e) {
            // 异常，进行回滚
            if (firstOffset > -1 && topicPartition != null) {
                // 拼接回滚的分区和偏移量
                Map<TopicPartition, Long> topicPartitionOffsetMap = new HashMap<>();
                topicPartitionOffsetMap.put(topicPartition, firstOffset);
                try {
                    // 回滚
                    fromOffset(topicPartitionOffsetMap);
                    // 提交
                    commitSync();
                    logger.warn("异常，进行回滚，topicPartition：{}，firstOffset：{}，具体异常信息：", topicPartition, firstOffset, e);
                } catch (Exception rollBackException) {
                    // 回滚异常，抛出运行时异常
                    throw new RuntimeException(String.format("回滚异常，topicPartition：%s，firstOffset：%s，异常信息：%s"
                            , topicPartition, firstOffset, rollBackException.getMessage()), rollBackException);
                }
            }
        }
    }

    /**
     * 字节数组转换成GenericRecord
     *
     * @param bytes
     * @return
     */
    public GenericRecord getGenericRecord(byte[] bytes) {
        GenericRecord record;
        try {
            record = recordConvertor.binaryToRecord(bytes);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("GenericRecord转换异常，%s", new String(bytes)), e);
        }
        return record;
    }

    /**
     * 如果目前的avro无法成功转换，尝试更新schema
     *
     * @param bytes
     * @return
     */
    public OggRecord getValueTryToChangeSchema(byte[] bytes) {
        OggRecord oggRecord = new OggRecord();
        try {
            // 尝试能否转换成avro
            oggRecord.setGenericRecord(getGenericRecord(bytes));
        } catch (Exception e) {
            logger.warn("{} 无法转换成avro，尝试能否更新schema." + new String(bytes));
            // 不能转换成avro
            try {
                // 就尝试能否更新schema
                oggRecord.updateSchema(updateSchema(new String(bytes)));
            } catch (Exception e1) {
                logger.warn(String.format("%s 更新schema异常！", new String(bytes)), e1);
                throw e1;
            }
        }
        return oggRecord;
    }

    /**
     * 更新schema
     *
     * @param str
     * @return
     */
    private Schema updateSchema(String str) {
        //更新schema
        schema = schemaUtil.getSchemaByString(str);
        //重构记录转换工具类
        recordConvertor = new RecordConvertor(schema);
        logger.info("转换Schema成功：{}", str);
        return schema;
    }

    public Schema getSchema() {
        return schema;
    }
}
