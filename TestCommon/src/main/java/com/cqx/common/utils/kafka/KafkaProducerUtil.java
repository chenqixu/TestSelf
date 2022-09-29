package com.cqx.common.utils.kafka;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
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
    private boolean isTransaction;// 事务开关
    private ProducerRecordsBuilder producerRecordsBuilder;

    /**
     * kafka参数从配置文件中获取，认证信息另外传入
     *
     * @param conf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaProducerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        buildProducer(init(properties, kafka_username, kafka_password, null));
    }

    /**
     * kafka参数从Map中获取
     *
     * @param stormConf
     * @throws IOException
     */
    public KafkaProducerUtil(Map stormConf) throws IOException {
        this(stormConf, false);
    }

    /**
     * kafka参数从Map中获取，支持事务
     *
     * @param stormConf
     * @param isTransaction
     * @throws IOException
     */
    public KafkaProducerUtil(Map stormConf, boolean isTransaction) throws IOException {
        this.isTransaction = isTransaction;
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        String kafka_username = properties.getProperty("newland.kafka_username");
        String kafka_password = properties.getProperty("newland.kafka_password");
        String kafkaSecurityProtocol = properties.getProperty("sasl.mechanism");
        // 认证 && 初始化参数
        Properties newProperties = init(properties, kafka_username, kafka_password, kafkaSecurityProtocol);
        if (isTransaction) {
            // 配置生产者端事务ID
            newProperties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG
                    // 避免重复, 拼接 UUID 来保证唯一
                    , "transaction-id" + ":" + UUID.randomUUID().toString());
            // 配置重试机制(all-至少一个副本写入数据, 30S未接收到应答则重复发送)
            newProperties.put(ProducerConfig.ACKS_CONFIG, "all");
            newProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
            // 开启幂等机制
            newProperties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        }
        // 构造生产者
        buildProducer(newProperties);
        if (isTransaction) {
            // 初始化事务
            if (producer != null) producer.initTransactions();
            else throw new NullPointerException("初始化事务失败！生产者为空，请检查。");
        }
    }

    /**
     * kafka参数从Map中获取，认证信息另外传入
     *
     * @param stormConf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaProducerUtil(Map stormConf, String kafka_username, String kafka_password) throws IOException {
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        buildProducer(init(properties, kafka_username, kafka_password, null));
    }

    /**
     * 认证 && 初始化参数
     *
     * @param properties
     * @param kafka_username
     * @param kafka_password
     * @param kafkaSecurityProtocol
     */
    private Properties init(Properties properties, String kafka_username, String kafka_password, String kafkaSecurityProtocol) {
//        Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password, kafkaSecurityProtocol));
        // 一个进程可能有多个kafka客户端，且是不同认证，再使用Configuration.setConfiguration不太合适
        properties.put("sasl.jaas.config", SimpleClientConfiguration.getSaslJaasConfig(kafka_username, kafka_password, kafkaSecurityProtocol));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        // 为避免长时间未达到发送批次上线, 导致数据不发送, 设置必须发送的时间间隔, 毫秒, 5000ms
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 5000);
        return properties;
    }

    /**
     * 构造生产者
     *
     * @param properties
     */
    private void buildProducer(Properties properties) {
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

    /**
     * 使用事务提交
     *
     * @param producerRecords
     */
    public void sendWithTransaction(List<ProducerRecord<K, V>> producerRecords) {
        if (!isTransaction) {
            throw new UnsupportedOperationException("事务未开启，不支持事务提交！");
        }
        try {
            // 生产者开始事务
            producer.beginTransaction();
            for (ProducerRecord<K, V> producerRecord : producerRecords) {
                producer.send(producerRecord);
            }
            // 生产者提交事务
            producer.commitTransaction();
        } catch (Exception e) {
            logger.error("提交事务异常，错误信息：" + e.getMessage(), e);
            // 生产者终止事务
            producer.abortTransaction();
        }
    }

    /**
     * 消费者和生产者协同事务
     *
     * @param producerRecords
     * @param consumerTopic
     * @param consumerPartition
     * @param consumerOffset
     * @param consumerGroupId
     */
    public void sendWithConsumerTransaction(List<ProducerRecord<K, V>> producerRecords
            , String consumerTopic, int consumerPartition, long consumerOffset, String consumerGroupId) {
        if (!isTransaction) {
            throw new UnsupportedOperationException("事务未开启，不支持事务提交！");
        }
        // 记录record元数据, 及消费端消费的消息的offset
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        // 记录元数据
        offsets.put(new TopicPartition(consumerTopic, consumerPartition)
                // 返回下次需要消费的消息的offset, 注意+1  !!!, 返回当前消息offset会重复消费当前消息
                , new OffsetAndMetadata(consumerOffset + 1));
        try {
            // 启动事务
            producer.beginTransaction();
            for (ProducerRecord<K, V> producerRecord : producerRecords) {
                producer.send(producerRecord);
            }
            // 将指定偏移的列表发送给消费者组协调员，并将这些偏移标记为当前事务的一部分。
            // 只有当事务成功提交时，这些偏移才会被视为已提交。
            // 提交的偏移量应该是应用程序将使用的下一条消息，即lastProcessedMessageOffset+1。
            producer.sendOffsetsToTransaction(offsets, consumerGroupId);
            // 提交事务（消费者偏移量提交+生产者提交），保证原子性
            producer.commitTransaction();
        } catch (Exception e) {
            logger.error("提交事务异常，错误信息：" + e.getMessage(), e);
            // 强制终止事务
            producer.abortTransaction();
        }
    }

    /**
     * 强制刷新
     */
    public void flush() {
        producer.flush();
    }

    public void release() {
        if (producer != null) producer.close();
    }

    @Override
    public void close() {
        release();
    }

    public void newInstance() {
        producerRecordsBuilder = new ProducerRecordsBuilder();
    }

    public void addProducerRecord(String topic, K key, V value) {
        if (producerRecordsBuilder != null) producerRecordsBuilder.add(topic, key, value);
    }

    public List<ProducerRecord<K, V>> getProducerRecords() {
        if (producerRecordsBuilder != null) return producerRecordsBuilder.getProducerRecords();
        return null;
    }

    class ProducerRecordsBuilder {
        List<ProducerRecord<K, V>> producerRecords = new ArrayList<>();

        void add(String topic, K key, V value) {
            producerRecords.add(new ProducerRecord<>(topic, key, value));
        }

        public List<ProducerRecord<K, V>> getProducerRecords() {
            return producerRecords;
        }
    }
}
