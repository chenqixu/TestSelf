package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.list.IKVList;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
}