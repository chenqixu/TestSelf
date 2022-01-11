package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.system.ArraysUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaConsumerGRUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGRUtilTest.class);

    @Test
    public void polls() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            String topic = (String) param.get("topic");//获取话题
            kafkaConsumerUtil.subscribe(topic);//订阅
            for (IKVList.Entry<String, GenericRecord> entry : kafkaConsumerUtil.pollsHasKey(1000L).entrySet()) {
                Object value = entry.getValue();
                logger.info("【key】{}，【value】{}，【value.class】{}",
                        entry.getKey(), value, value != null ? value.getClass() : null);
            }
            kafkaConsumerUtil.commitSync();
        }
    }

    @Test
    public void pollsUSER_PRODUCT() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe("USER_PRODUCT");
//            // 打印position
//            Map<TopicPartition, Long> topicPartitionOffsetMap = kafkaConsumerUtil.getNextOffset();
//            // 设置消费位置
//            for (Map.Entry<TopicPartition, Long> entry : topicPartitionOffsetMap.entrySet()) {
//                logger.info("key：{}，value：{}", entry.getKey(), entry.getValue());
//                topicPartitionOffsetMap.put(entry.getKey(), 14L);
//            }
//            kafkaConsumerUtil.fromOffset(topicPartitionOffsetMap);
//            kafkaConsumerUtil.commitSync();
            for (int i = 0; i < 15; i++) {
                try {
//                for (IKVList.Entry<String, GenericRecord> entry : kafkaConsumerUtil.pollsHasKey(1000L).entrySet()) {
//                    for (ConsumerRecord<String, byte[]> consumerRecord : kafkaConsumerUtil.pollHasConsumerRecord(1000L)) {
//
//                    }
                    for (IKVList.Entry<Long, GenericRecord> entry : kafkaConsumerUtil.pollsHasOffset(1000L).entrySet()) {
                        Object value = entry.getValue();
                        logger.info("【offset】{}，【value】{}，【value.class】{}",
                                entry.getKey(), value, value != null ? value.getClass() : null);
                    }
                    // 记录成功的偏移量
                } catch (Exception e) {
                    logger.error("consumer.poll 异常：" + e.getMessage(), e);
                    // 重载
                    kafkaConsumerUtil.reloadByMap();
                }
            }
            // 提交
//            kafkaConsumerUtil.commitSync();
        }
    }

    @Test
    public void polls1() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe("USER_PRODUCT");
            // 从头开始消费
            kafkaConsumerUtil.fromBeginning();
            for (int i = 0; i < 1500; i++) {
                try {
                    for (IKVList.Entry<Long, GenericRecord> entry : kafkaConsumerUtil.pollsHasOffset(1000L).entrySet()) {
                        Object value = entry.getValue();
                        logger.info("【offset】{}，【value】{}，【value.class】{}",
                                entry.getKey(), value, value != null ? value.getClass() : null);
                    }
                } catch (Exception e) {
                    logger.error("consumer.poll 异常：" + e.getMessage(), e);
                }
            }
        }
    }

    @Test
    public void polls2() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe("USER_ADDITIONAL_INFO");
            // 从头开始消费
            kafkaConsumerUtil.fromBeginning();
            for (int i = 0; i < 1500; i++) {
                try {
                    for (IKVList.Entry<Long, GenericRecord> entry : kafkaConsumerUtil.pollsHasOffset(1000L).entrySet()) {
                        Object value = entry.getValue();
                        logger.info("【offset】{}，【value】{}，【value.class】{}",
                                entry.getKey(), value, value != null ? value.getClass() : null);
                    }
                } catch (Exception e) {
                    logger.error("consumer.poll 异常：" + e.getMessage(), e);
                }
            }
        }
    }

    @Test
    public void pollNoScheam() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerUtil<byte[], byte[]> kafkaConsumerUtil = new KafkaConsumerUtil<>(param)) {
            String topic = "con1";
            kafkaConsumerUtil.subscribe(topic);//订阅
            for (int i = 0; i < 10; i++) {
                for (byte[] value : kafkaConsumerUtil.poll(1000L)) {
                    logger.info("【【value】{}，【value.class】{}", value, value != null ? value.getClass() : null);
                }
            }
        }
    }

    @Test
    public void offset() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("param：{}", param);
        long lastOffset = 0L;
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            kafkaConsumerUtil.subscribe("USER_PRODUCT");// 订阅
            // 获取分区信息
            kafkaConsumerUtil.getTopicPartitions();
            // 打印position
            Map<TopicPartition, Long> topicPartitionOffsetMap = kafkaConsumerUtil.getNextOffset();
            // 从头消费
//            kafkaConsumerUtil.fromBeginning();
//            kafkaConsumerUtil.fromEnd();
            // 消费1秒
            for (IKVList.Entry<Long, GenericRecord> entry : kafkaConsumerUtil.pollsHasOffset(1000L).entrySet()) {
                logger.info("【offset】{}，【value】{}", entry.getKey(), entry.getValue());
                lastOffset = entry.getKey();
            }
            if (lastOffset == 0L) {
                for (Long offset : topicPartitionOffsetMap.values()) {
                    lastOffset = offset;
                }
            }
            // 设置消费位置
            for (Map.Entry<TopicPartition, Long> entry : topicPartitionOffsetMap.entrySet()) {
                logger.info("key：{}，value：{}", entry.getKey(), entry.getValue());
                topicPartitionOffsetMap.put(entry.getKey(), lastOffset - 3);
            }
            kafkaConsumerUtil.fromOffset(topicPartitionOffsetMap);
            kafkaConsumerUtil.commitSync();
            // 打印position
            kafkaConsumerUtil.getNextOffset();
        }
    }

    @Test
    public void multipleOffset() throws Exception {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        offset();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
            ts.add(t);
        }
        for (Thread t : ts) {
            t.start();
        }
        for (Thread t : ts) {
            t.join();
        }
    }

    @Test
    public void pollRollBack() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe("USER_PRODUCT");
            for (int i = 0; i < 15; i++) {
                kafkaConsumerUtil.poll(1000L, new AbstractKafkaUtil<ConsumerRecord<String, byte[]>, GenericRecord>() {

                    public boolean callBack(List<IKVList.Entry<ConsumerRecord<String, byte[]>, GenericRecord>> records) {
                        for (IKVList.Entry<ConsumerRecord<String, byte[]>, GenericRecord> entry : records) {
                            logger.info("【topic】{}，【partition】{}，【offset】{}，【value】{}"
                                    , entry.getKey().topic(), entry.getKey().partition(), entry.getKey().offset(), entry.getValue());
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Test
    public void ogg_test() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe("ogg_test");
            logger.info("获取当前 consumer已经分配到的分区：{}", kafkaConsumerUtil.assignedPartitions());
            // 从指定位置偏移
            // 获取分区信息
//            Collection<TopicPartition> topicPartitions = kafkaConsumerUtil.getTopicPartitions();
//            Map<TopicPartition, Long> topicPartitionOffsetMap = new HashMap<>();
//            Iterator<TopicPartition> it = topicPartitions.iterator();
//            if (it.hasNext()) {
//                topicPartitionOffsetMap.put(it.next(), 20408L);
//            }
//            kafkaConsumerUtil.fromOffset(topicPartitionOffsetMap);
            // 从头偏移
//            kafkaConsumerUtil.fromBeginning();
            // 因为没有自动提交，如果偏移了，不提交，下次又想从这个位置消费，偏移后就要同步下
//            kafkaConsumerUtil.commitSync();
//            for (int i = 0; i < 5; i++) {
            for (ConsumerRecord<String, byte[]> record : kafkaConsumerUtil.pollHasConsumerRecord(1000L)) {
                logger.info("offset：{}，timestamp：{}，formatTime：{}，key：{}，value：{}"
                        , record.offset(), record.timestamp(), Utils.formatTime(record.timestamp()), record.key(), new String(record.value()));
                OggRecord oggRecord = kafkaConsumerUtil.getValueTryToChangeSchema(record.value());
                kafkaConsumerUtil.commitSync(record.partition(), record.offset());
                if (oggRecord.isRecord()) {
                    logger.info("genericRecord：{}", oggRecord.getGenericRecord());
                } else if (oggRecord.isSchema()) {
                    logger.info("new schema：{}", oggRecord.getSchema());
                }
                long lastCommitedOffset = kafkaConsumerUtil.commited(record.partition());
                logger.info("lastCommitedOffset：{}", lastCommitedOffset);
                break;
            }
            logger.info("获取当前 consumer已经分配到的分区：{}", kafkaConsumerUtil.assignedPartitions());
//            }
        }
    }

    @Test
    public void updatePksTest() throws Exception {
        String oggTopic = "USER_PRODUCT";
        String flatTopic = "FLAT_USER_PRODUCT";
        String[] pks = {"home_city",
                "product_type",
                "subscription_id",
                "user_id",
                "product_id"};
        String[] send_pks_before_array = ArraysUtil.arrayAddPrefix(pks, "before_");
        String[] send_pks_after_array = ArraysUtil.arrayAddPrefix(pks, "after_");
        // 从配置文件解析参数
        Map param = (Map) getParam("kafka.yaml").get("param");
        // 从配置中获取话题工具URL
        String schemaUrl = (String) param.get("schema_url");
        logger.info("【param】{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            // 订阅
            kafkaConsumerUtil.subscribe(oggTopic);
            // 获取OggSchema
            Schema oggScheam = kafkaConsumerUtil.getSchema();
            SchemaUtil schemaUtil = new SchemaUtil(schemaUrl);
            // 获取扁平化Schema
            Schema flatSchema = schemaUtil.getSchemaByTopic(flatTopic);

            // 扁平化工具
            FlatUtil flatUtil = new FlatUtil(oggTopic, oggScheam, flatSchema);

            for (IKVList.Entry<ConsumerRecord<String, byte[]>, GenericRecord> entry : kafkaConsumerUtil.pollsHasConsumerRecord(1000L).entrySet()) {
                ConsumerRecord<String, byte[]> key = entry.getKey();
                GenericRecord value = entry.getValue();
                logger.info("【timestamp】{}，【offset】{}，【value】{}", key.timestamp(), key.offset(), value);

                // 扁平化处理
                GenericRecord flatRecord = flatUtil.flat(value);
                Map beforePksMap = new HashMap();
                for (String before : send_pks_before_array) {
                    logger.debug("key：{}，value：{}", before, flatRecord.get(before));
                    beforePksMap.put(before.replace("before_", ""), flatRecord.get(before));
                }
                for (String after : send_pks_after_array) {
                    boolean compare = compare(beforePksMap.get(after.replace("after_", ""))
                            , flatRecord.get(after));
                    logger.debug("key：{}，value：{}，compare：{}", after, flatRecord.get(after), compare);
                    if (!compare) {
                        logger.warn("捕获到主键更新！");
//                        break;
                    }
                }
            }
        }
    }

    /**
     * 比较两个对象，转成toString进行比较
     *
     * @param o1
     * @param o2
     * @return
     */
    private boolean compare(Object o1, Object o2) {
        logger.info("o1：{} {}，o2：{} {}", o1.getClass(), o1, o2.getClass(), o2);
        return o1.toString().equals(o2.toString());
    }

    /**
     * 从scram认证模式下的kafka话题进行消费
     *
     * @throws Exception
     */
    @Test
    public void pollScram() throws Exception {
        Map param = (Map) getParam("kafka_scram.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            String topic = (String) param.get("topic");//获取话题
            kafkaConsumerUtil.subscribe(topic);//订阅
            for (IKVList.Entry<String, byte[]> entry : kafkaConsumerUtil.pollHasKey(1000L).entrySet()) {
                byte[] value = entry.getValue();
                logger.info("【key】{}，【value】{}，【value.class】{}",
                        entry.getKey(), new String(value), value.getClass().getSimpleName());
            }
        }
    }
}