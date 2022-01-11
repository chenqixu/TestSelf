package com.cqx.common.utils.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * KafkaProducerUtil<br>
 * 2021-04-20 cqx 增加Closeable接口，方便JDK8语法糖使用<br>
 * <pre>
 *     acks=0，不等待broker的确认信息，最小延迟
 *     acks=1，leader已经接收了数据的确认信息，Replica异步拉取信息，比较折衷
 *     acks=-1，ISR列表中的所有Replica都返回确认信息
 *     acks=all，ISR列表中的所有Replica都返回确认信息
 * </pre>
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
        String kafkaSecurityProtocol = properties.getProperty("sasl.mechanism");
        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password, kafkaSecurityProtocol));
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

    public Future<RecordMetadata> send(String topic, K key, V value) {
        return producer.send(new ProducerRecord<>(topic, key, value));
    }

    public Future<RecordMetadata> send(String topic, V value) {
        return send(topic, null, value);
    }

    /**
     * 回调模式
     *
     * @param topic
     * @param key
     * @param value
     * @param callback
     * @return
     */
    public Future<RecordMetadata> sendCallback(String topic, K key, V value, Callback callback) {
        return producer.send(new ProducerRecord<>(topic, key, value), callback);
    }

    public void release() {
        if (producer != null) producer.close();
    }

    @Override
    public void close() {
        release();
    }
}
