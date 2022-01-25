package com.cqx.common.utils.kafka;

import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
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
    private Set<TopicPartition> assignment = new HashSet<>();
    private Map<TopicPartition, Long> topicPartitionLastOffset = new HashMap<>();
    private String conf;
    private Map stormConf;
    private String kafka_username;
    private String kafka_password;
    private boolean isTransaction;// 事务开关

    /**
     * 走Properties文件，认证使用java.security.auth.login.config
     *
     * @param conf
     * @throws IOException
     */
    public KafkaConsumerUtil(String conf) throws IOException {
        init(conf, null, null);
    }

    /**
     * 走Properties文件，认证使用SimpleClientConfiguration
     *
     * @param conf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaConsumerUtil(String conf, String kafka_username, String kafka_password) throws IOException {
        init(conf, kafka_username, kafka_password);
    }

    /**
     * 走Map配置，用户密码在Map配置
     *
     * @param stormConf
     * @throws IOException
     */
    public KafkaConsumerUtil(Map stormConf) throws IOException {
        init(stormConf, null, null);
    }

    public KafkaConsumerUtil(Map stormConf, boolean isTransaction) throws IOException {
        init(stormConf, null, null, isTransaction);
    }

    /**
     * 走Map配置，用户密码显示输入
     *
     * @param stormConf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    public KafkaConsumerUtil(Map stormConf, String kafka_username, String kafka_password) throws IOException {
        init(stormConf, kafka_username, kafka_password);
    }

    private void init(Map stormConf, String kafka_username, String kafka_password) {
        init(stormConf, kafka_username, kafka_password, false);
    }

    /**
     * 初始化，使用Map的配置来替代每一台的配置文件
     *
     * @param stormConf
     */
    private void init(Map stormConf, String kafka_username, String kafka_password, boolean isTransaction) {
        this.isTransaction = isTransaction;
        this.stormConf = stormConf;
        this.kafka_username = kafka_username;
        this.kafka_password = kafka_password;
        Properties properties = KafkaPropertiesUtil.initConf(stormConf);
        if (kafka_username == null) {
            this.kafka_username = properties.getProperty("newland.kafka_username");
        }
        if (kafka_password == null) {
            this.kafka_password = properties.getProperty("newland.kafka_password");
        }
        String kafkaSecurityProtocol = properties.getProperty("sasl.mechanism");
        Configuration.setConfiguration(new SimpleClientConfiguration(this.kafka_username, this.kafka_password, kafkaSecurityProtocol));
        KafkaPropertiesUtil.removeNewlandProperties(properties);
        if (isTransaction) {
            // 设置事务隔离级别, 读已提交(仅仅消费有提交标记的消息)
            properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
            // 消费者&生产者事务, 必须关闭消费端的自动提交
            // 应避免消费失败同时更新了offset, 导致无法重新消费"消费端失败"的消息
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        }
        consumer = new KafkaConsumer<>(properties);
    }

    /**
     * 初始化，走Properties文件配置
     *
     * @param conf
     * @param kafka_username
     * @param kafka_password
     * @throws IOException
     */
    private void init(String conf, String kafka_username, String kafka_password) throws IOException {
        this.conf = conf;
        this.kafka_username = kafka_username;
        this.kafka_password = kafka_password;
        Properties properties = new Properties();
        properties.load(new FileInputStream(conf));
        String kafkaSecurityProtocol = properties.getProperty("sasl.mechanism");
        if (kafka_username != null && kafka_password != null) {
            Configuration.setConfiguration(new SimpleClientConfiguration(kafka_username, kafka_password, kafkaSecurityProtocol));
        } else {
            // java.security.auth.login.config 变量设置
            String propertyAuth = properties.getProperty("java.security.auth.login.config");
            if (propertyAuth != null && !"".equals(propertyAuth)) {
                logger.info("java.security.auth.login.config is not null，{}", propertyAuth);
                System.setProperty("java.security.auth.login.config", propertyAuth);
                properties.remove("java.security.auth.login.config");
                logger.info("java.security.auth.login.config remove from properties");
            }
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
     * @param topicPartitionOffsetMap
     * @throws Exception
     */
    public void fromOffset(Map<TopicPartition, Long> topicPartitionOffsetMap) throws Exception {
        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();
        // 指定分区从指定位置消费
        for (TopicPartition tp : assignment) {
            long seekOffset = topicPartitionOffsetMap.get(tp);
            logger.info("分区 {} 移动到指定位置：{}", tp, seekOffset);
            consumer.seek(tp, seekOffset);
        }
    }

    /**
     * 获取要获取的下一条记录的偏移量（如果存在具有该偏移量的记录）<br>
     * 谨慎使用，可能会丢数据，因为要取到消费者所分配到的分区消息，而kafka是懒操作模式，需要poll才有数据
     *
     * @return
     */
    public Map<TopicPartition, Long> getNextOffset() {
        Map<TopicPartition, Long> result = new HashMap<>();
        // 获取当前所有分区
        Set<TopicPartition> assignment = getConsumerAssignment();
        for (TopicPartition tp : assignment) {
            // 获取要获取的下一条记录的偏移量（如果存在具有该偏移量的记录）
            long position = consumer.position(tp);
            result.put(tp, position);
            logger.info("分区 {} 下一条记录的偏移量为：{}", tp, position);
        }
        return result.size() > 0 ? result : null;
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
     * 获取当前所有分区<br>
     * 注意这里有拉取动作，所以不能拉两次<br>
     * position方法会检查当前客户端是否分配消费的分区，所以这里使用getTopicPartitions是不合适的，因为还没分配消费的分区给当前客户端
     *
     * @return
     */
    private Set<TopicPartition> getConsumerAssignment() {
        // 在poll()方法内部执行分区分配逻辑，该循环确保分区已被分配。
        // 当分区消息为0时进入此循环，如果不为0，则说明已经成功分配到了分区。
        // assignment()方法是用来获取消费者所分配到的分区消息的
        // assignment的值为：topic-demo-3, topic-demo-0, topic-demo-2, topic-demo-1
        assignment = consumer.assignment();
        while (assignment.size() == 0) {
            // poll data from kafka server to prevent lazy operation
            ConsumerRecords<K, V> data = consumer.poll(100);
            logger.info("获取当前所有分区，lazy operation poll data：{}", data.count());
            // assignment()方法是用来获取消费者所分配到的分区消息的
            // assignment的值为：topic-demo-3, topic-demo-0, topic-demo-2, topic-demo-1
            assignment = consumer.assignment();
        }
        if (topicPartitionLastOffset.size() == 0) {
            for (TopicPartition tp : assignment) {
                topicPartitionLastOffset.put(tp, 0L);
            }
        }
        return assignment;
    }

    /**
     * 从元数据获取分区信息
     *
     * @return
     */
    public Collection<TopicPartition> getTopicPartitions() {
        // get consumer consume partitions
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            topicPartitions.add(topicPartition);
            logger.info("partitionInfo：{}", partitionInfo);
        }
        return topicPartitions;
    }

    /**
     * 消费，返回List&lt;ConsumerRecord&lt;K，V&gt;&gt;，可以设置commitSync
     *
     * @param timeout
     * @param isCommitSync
     * @return
     */
    public List<ConsumerRecord<K, V>> pollHasConsumerRecord(long timeout, boolean isCommitSync) {
        List<ConsumerRecord<K, V>> resultList = new ArrayList<>();
        ConsumerRecords<K, V> records = consumer.poll(timeout);
        for (ConsumerRecord<K, V> record : records.records(topic)) {
            logger.debug("######## offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
            resultList.add(record);
        }
        if (isCommitSync) {
            // 同步提交 消费偏移量，todo 已经设置了自动提交，其实这里可以不需要
            consumer.commitSync();
        }
        return resultList;
    }

    /**
     * 消费，返回List&lt;ConsumerRecord&lt;K，V&gt;&gt;
     *
     * @param timeout
     * @return
     */
    public List<ConsumerRecord<K, V>> pollHasConsumerRecord(long timeout) {
        return pollHasConsumerRecord(timeout, false);
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
        for (ConsumerRecord<K, V> record : pollHasConsumerRecord(timeout, isCommitSync)) {
            resultList.add(record.value());
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
        for (ConsumerRecord<K, V> record : pollHasConsumerRecord(timeout, isCommitSync)) {
            kvList.put(record.key(), record.value());
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
     * 针对所有分区进行提交同步
     */
    public void commitSync() {
        if (consumer != null) consumer.commitSync();
    }

    /**
     * 提交多个分区的 offset
     *
     * @param partitionAndOffset key 对应的分区id；value 对应的游标位置
     */
    public void commitSync(Map<Integer, Long> partitionAndOffset) {
        if (consumer != null) {
            for (Map.Entry<Integer, Long> partition : partitionAndOffset.entrySet()) {
                commitSync(partition.getKey(), partition.getValue());
            }
        }
    }

    /**
     * 提交某分区的 offset
     *
     * @param partitionId 分区 id
     * @param offset      目前已经消费的游标位置
     */
    public void commitSync(int partitionId, long offset) {
        if (consumer != null) {
            Map<TopicPartition, OffsetAndMetadata> map = new HashMap<>();
            TopicPartition partition = new TopicPartition(topic, partitionId);
            OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(offset + 1);
            map.put(partition, offsetAndMetadata);
            consumer.commitSync(map);
            logger.info("分区提交：{}", map);
        }
    }

    /**
     * 获取此分区最后提交的 offset
     *
     * @param partition kafka 分区id
     * @return 对应分区的游标位置
     */
    public long commited(int partition) {
        OffsetAndMetadata mt = consumer.committed(new TopicPartition(this.topic, partition));
        if (mt != null) {
            return mt.offset();
        }
        return -1;
    }

    /**
     * 返回对应分区最后提交的 offset
     *
     * @param partitions 需要查询的分区列表 分区id的list
     * @return key 分区id; value offset
     */
    public Map<Integer, Long> commited(List<Integer> partitions) {
        Map<Integer, Long> map = new HashMap<>();
        for (int partitionId : partitions) {
            map.put(partitionId, commited(partitionId));
        }
        return map;
    }

    /**
     * 获取当前 consumer已经分配到的分区<br>
     * 注意：kafka是懒加载模式，所以没有消费是取不到值的
     *
     * @return 分区 id
     */
    public List<Integer> assignedPartitions() {
        List<Integer> partitions = new ArrayList<>();
        Set<TopicPartition> partitionSet = consumer.assignment();
        if (partitionSet != null) {
            for (TopicPartition partition : partitionSet) {
                partitions.add(partition.partition());
            }
        }
        return partitions;
    }

    @Override
    public void close() {
        // 关闭
        if (consumer != null) consumer.close();
    }

    /**
     * 从Map中进行重载
     */
    public void reloadByMap() {
        // 关闭
        close();
        // 重新初始化
        init(stormConf, kafka_username, kafka_password);
        // 重新订阅
        subscribe(topic);
    }

    /**
     * 从Properties文件进行重载
     */
    public void reloadByFile() throws IOException {
        // 关闭
        close();
        // 重新初始化
        init(conf, kafka_username, kafka_password);
        // 重新订阅
        subscribe(topic);
    }
}
