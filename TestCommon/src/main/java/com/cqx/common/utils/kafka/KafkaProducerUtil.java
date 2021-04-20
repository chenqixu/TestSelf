package com.cqx.common.utils.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * KafkaProducerUtil<br>
 * 2021-04-20 cqx 增加Closeable接口，方便JDK8语法糖使用
 *
 * @author chenqixu
 */
public class KafkaProducerUtil<K, V> implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);
    private KafkaProducer<K, V> producer;

    public KafkaProducerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        producer = new KafkaProducer<>(properties);
    }

    public KafkaProducerUtil(Map stormConf) throws IOException {
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        String kafka_username = properties.getProperty("newland.kafka_username");
        String kafka_password = properties.getProperty("newland.kafka_password");
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        producer = new KafkaProducer<>(properties);
    }

    /**
     * 使用内存中的配置来替代每一台的配置文件
     *
     * @param stormConf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaProducerUtil(Map stormConf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        producer = new KafkaProducer<>(properties);
    }

    public void send(String topic, K key, V value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    public void send(String topic, V value) {
        producer.send(new ProducerRecord<K, V>(topic, value));
    }

    public void release() {
        if (producer != null) producer.close();
    }

    public void close() {
        release();
    }
}
