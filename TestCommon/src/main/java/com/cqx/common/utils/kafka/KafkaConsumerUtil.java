package com.cqx.common.utils.kafka;

import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * KafkaConsumerUtil<br>
 * 2021-04-20-V1 cqx 增加Closeable接口，方便JDK8语法糖使用<br>
 * 2021-04-20-V2 cqx 增加commitSync方法，适配非自动提交的场景
 *
 * @author chenqixu
 */
public class KafkaConsumerUtil<K, V> implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerUtil.class);
    private KafkaConsumer<K, V> consumer;
    private String topic;

    public KafkaConsumerUtil(String conf) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        init(properties);
    }

    public KafkaConsumerUtil(Map stormConf) throws IOException {
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        String kafka_username = properties.getProperty("newland.kafka_username");
        String kafka_password = properties.getProperty("newland.kafka_password");
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        consumer = new KafkaConsumer<>(properties);
    }

    public KafkaConsumerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        consumer = new KafkaConsumer<>(properties);
    }

    /**
     * 使用内存中的配置来替代每一台的配置文件
     *
     * @param stormConf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaConsumerUtil(Map stormConf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        consumer = new KafkaConsumer<>(properties);
    }

    private void init(Properties properties) {
        //java.security.auth.login.config 变量设置
        String propertyAuth = properties.getProperty("java.security.auth.login.config");
        if (propertyAuth != null && !"".equals(propertyAuth)) {
            logger.info("java.security.auth.login.config is not null，{}", propertyAuth);
            System.setProperty("java.security.auth.login.config", propertyAuth);
            properties.remove("java.security.auth.login.config");
            logger.info("java.security.auth.login.config remove from properties");
        }
        consumer = new KafkaConsumer<>(properties);
    }

    /**
     * 订阅话题
     *
     * @param topic
     */
    public void subscribe(String topic) {
        consumer.subscribe(Arrays.asList(topic));
        this.topic = topic;
    }

    /**
     * 从头开始消费
     *
     * @throws Exception
     */
    public void fromBeginning() throws Exception {
        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();
        // 指定分区从头消费
        Map<TopicPartition, Long> beginOffsets = consumer.beginningOffsets(assignment);
        for (TopicPartition tp : assignment) {
            Long offset = beginOffsets.get(tp);
            logger.info("分区 {} 从 {} 开始消费", tp, offset);
            consumer.seek(tp, offset);
        }
    }

    /**
     * 从末尾消费
     *
     * @throws Exception
     */
    public void fromEnd() throws Exception {
        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();
        // 指定分区从末尾最新位置消费
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(assignment);
        for (TopicPartition tp : assignment) {
            Long offset = endOffsets.get(tp);
            logger.info("分区 {} 从 {} 开始消费", tp, offset);
            consumer.seek(tp, offset);
        }
    }

    /**
     * 从某个位置开始消费
     *
     * @param offset
     * @throws Exception
     */
    public void fromOffsetng(long offset) throws Exception {
        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();
        // 指定分区从指定位置消费
        for (TopicPartition tp : assignment) {
            logger.info("分区 {} 从 {} 开始消费", tp, offset);
            consumer.seek(tp, offset);
        }
    }

    /**
     * 从指定时间开始消费
     *
     * @param fetchDataTimeStr
     * @throws ParseException
     */
    public void fromTime(String fetchDataTimeStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long fetchDataTime = df.parse(fetchDataTimeStr).getTime();
        logger.info("fetchDataTimeStr：{}", fetchDataTimeStr);
        fromTime(fetchDataTime);
    }

    /**
     * 从指定时间戳开始消费
     *
     * @param fetchDataTime
     */
    public void fromTime(long fetchDataTime) {
        logger.info("fetchDataTime：{}", fetchDataTime);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();

        // 创建分区偏移量对象
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (TopicPartition tp : assignment) {
            timestampsToSearch.put(new TopicPartition(tp.topic(), tp.partition()), fetchDataTime);
        }

        // offsetsForTimes
        Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(timestampsToSearch);
        logger.info("开始设置{}各分区初始偏移量...", topic);
        int success = 0;
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : map.entrySet()) {
            // 如果设置的查询偏移量的时间点大于最大的索引记录时间，那么value就为空
            TopicPartition tp = entry.getKey();
            OffsetAndTimestamp offsetTimestamp = entry.getValue();
            if (offsetTimestamp != null) {
                int partition = entry.getKey().partition();
                long timestamp = offsetTimestamp.timestamp();
                long offset = offsetTimestamp.offset();
                logger.info("topic = {}, partition = {}, time = {}, offset = {}",
                        tp.topic(), partition, df.format(new Date(timestamp)), offset);
                // 设置读取消息的偏移量
                consumer.seek(entry.getKey(), offset);
                success++;
            }
        }
        logger.info("设置{}各分区初始偏移量结束，成功设置{}个", topic, success);
    }

    /**
     * 获取当前所有分区
     *
     * @return
     */
    private Set<TopicPartition> getConsumerAssignment() {
        Set<TopicPartition> assignment = new HashSet<>();
        // 在poll()方法内部执行分区分配逻辑，该循环确保分区已被分配。
        // 当分区消息为0时进入此循环，如果不为0，则说明已经成功分配到了分区。
        while (assignment.size() == 0) {
            // poll data from kafka server to prevent lazy operation
            consumer.poll(100);
            // assignment()方法是用来获取消费者所分配到的分区消息的
            // assignment的值为：topic-demo-3, topic-demo-0, topic-demo-2, topic-demo-1
            assignment = consumer.assignment();
        }
        return assignment;
    }

    /**
     * 消费，返回&lt;K，V&gt;中的V，可以设置commitSync
     *
     * @param timeout
     * @param isCommitSync
     * @return
     */
    public List<V> poll(long timeout, boolean isCommitSync) {
        List<V> resultList = new ArrayList<>();
        ConsumerRecords<K, V> records = consumer.poll(timeout);
        for (ConsumerRecord<K, V> record : records.records(topic)) {
            V msgByte = record.value();
            logger.debug("######## offset = {}, key = {}, value = {}", record.offset(), record.key(), msgByte);
            resultList.add(msgByte);
        }
        if (isCommitSync) {
            // 同步提交 消费偏移量，todo 已经设置了自动提交，其实这里可以不需要
            consumer.commitSync();
        }
        return resultList;
    }

    /**
     * 消费，返回&lt;K，V&gt;中的V
     *
     * @param timeout
     * @return
     */
    public List<V> poll(long timeout) {
        return poll(timeout, false);
    }

    /**
     * 消费，返回&lt;K，V&gt;，可以设置commitSync
     *
     * @param timeout
     * @param isCommitSync
     * @return
     */
    public IKVList<K, V> pollHasKey(long timeout, boolean isCommitSync) {
        IKVList<K, V> kvList = new KVList<>();
        ConsumerRecords<K, V> records = consumer.poll(timeout);
        for (ConsumerRecord<K, V> record : records.records(topic)) {
            K keyByte = record.key();
            V valueByte = record.value();
            logger.debug("######## offset = {}, key = {}, value = {}", record.offset(), keyByte, valueByte);
            kvList.put(keyByte, valueByte);
        }
        if (isCommitSync) {
            // 同步提交 消费偏移量，todo 已经设置了自动提交，其实这里可以不需要
            consumer.commitSync();
        }
        return kvList;
    }

    /**
     * 消费，返回&lt;K，V&gt;
     *
     * @param timeout
     * @return
     */
    public IKVList<K, V> pollHasKey(long timeout) {
        return pollHasKey(timeout, false);
    }

    /**
     * 提交同步
     */
    public void commitSync() {
        if (consumer != null) consumer.commitSync();
    }

    @Override
    public void close() {
        // 关闭
        if (consumer != null) consumer.close();
    }
}
