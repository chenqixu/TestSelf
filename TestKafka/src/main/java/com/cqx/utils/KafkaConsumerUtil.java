package com.cqx.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * KafkaConsumerUtil
 *
 * @author chenqixu
 */
public class KafkaConsumerUtil<K, V> {
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerUtil.class);
    private KafkaConsumer<K, V> consumer;
    private String topic;

    public KafkaConsumerUtil(String conf) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        init(properties);
    }

    public KafkaConsumerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
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

    public void subscribe(String topic) {
        // 订阅话题
        consumer.subscribe(Arrays.asList(topic));
        this.topic = topic;
    }

    public List<V> poll(long timeout) {
        List<V> resultList = new ArrayList<>();
        ConsumerRecords<K, V> records = consumer.poll(timeout);
        for (ConsumerRecord<K, V> record : records.records(topic)) {
            V msgByte = record.value();
            logger.info("######## offset = {}, key = {}, value = {}", record.offset(), record.key(), msgByte);
            resultList.add(msgByte);
        }
//        // 同步提交 消费偏移量，todo 已经设置了自动提交，其实这里可以不需要
//        consumer.commitSync();
        return resultList;
    }

    public void close() {
        // 关闭
        consumer.close();
    }
}
