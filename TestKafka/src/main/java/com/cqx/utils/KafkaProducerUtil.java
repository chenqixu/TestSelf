package com.cqx.utils;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * KafkaProducerUtil
 *
 * @author chenqixu
 */
public class KafkaProducerUtil<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);
    private KafkaProducer<K, V> producer;

    public KafkaProducerUtil(String conf) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        init(properties);
    }

    public KafkaProducerUtil(Properties properties) {
        init(properties);
    }

    public KafkaProducerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        producer = new KafkaProducer<>(properties);
    }

    public void init(Properties properties) {
        //java.security.auth.login.config 变量设置
        String propertyAuth = properties.getProperty("java.security.auth.login.config");
        if (propertyAuth != null && !"".equals(propertyAuth)) {
            logger.info("java.security.auth.login.config is not null，{}", propertyAuth);
            System.setProperty("java.security.auth.login.config", propertyAuth);
            properties.remove("java.security.auth.login.config");
            logger.info("java.security.auth.login.config remove from properties");
        }
        producer = new KafkaProducer<>(properties);
    }

    public void send(String topic, K key, V value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    public void send(String topic, V value) {
        producer.send(new ProducerRecord<K, V>(topic, value));
    }

    /**
     * 异步发送
     *
     * @param topic
     * @param key
     * @param value
     */
    public void sendCallBack(String topic, K key, V value) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
        producer.send(record,
                new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            logger.info("The offset of the record we just sent is: {}", metadata.offset());
                        }
                    }
                });
    }

    public void release() {
        if (producer != null) producer.close();
    }
}
