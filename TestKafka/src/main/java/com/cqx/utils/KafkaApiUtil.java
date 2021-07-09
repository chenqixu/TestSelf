package com.cqx.utils;

import com.cqx.bean.PartitionAssignmentState;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import kafka.admin.AdminUtils;
import kafka.admin.ConsumerGroupCommand;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * KafkaApiUtil
 *
 * @author chenqixu
 */
public class KafkaApiUtil {
    public static final String zookeeper_param = "--zookeeper";
    private static final Logger logger = LoggerFactory.getLogger(KafkaApiUtil.class);
    private String zookeeper_ip_port;
    private String zookeeper_path;
    private String[] options = null;

    public static KafkaApiUtil builder() {
        return new KafkaApiUtil();
    }

    private void check() {
        if (zookeeper_ip_port == null || zookeeper_ip_port.length() == 0)
            throw new NullPointerException("zookeeper_ip_port is null，please init.");
        if (zookeeper_path == null || zookeeper_path.length() == 0)
            throw new NullPointerException("zookeeper_path is null，please init.");
    }

    public String[] getOptions() {
        return options;
    }

    public void getAllTopic() {
        options = new String[]{
                "--list",
                zookeeper_param,
                zookeeper_ip_port
        };
        check();
        TopicCommand.main(options);
    }

    public void queryTopicByName(String topic_name) {
        options = new String[]{
                "--describe",
                zookeeper_param,
                zookeeper_ip_port,
                "--topic",
                topic_name,
        };
        check();
        TopicCommand.main(options);
    }

    public void createTopicByName(String topic_name, int partitions, int replicationFactor) {
        String[] options = new String[]{
                "--create",
                "--zookeeper",
                zookeeper_ip_port + zookeeper_path,
                "--partitions",
                String.valueOf(partitions),
                "--topic",
                topic_name,
                "--replication-factor",
                String.valueOf(replicationFactor)
        };
        check();
        TopicCommand.main(options);
    }

    /**
     * 删除话题
     *
     * @param topic_name
     */
    public void deleteTopicByName(String topic_name) {
        logger.info("deleteTopicByName：{}", topic_name);
        check();
        ZkUtils zkUtils = ZkUtils.apply(zookeeper_ip_port, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        AdminUtils.deleteTopic(zkUtils, topic_name);
        zkUtils.close();
    }

    public void fetchAllTopicConfigs() {
        check();
        ZkUtils zkUtils = ZkUtils.apply(zookeeper_ip_port, 30000, 30000, JaasUtils.isZkSecurityEnabled());
        logger.info("{}", AdminUtils.fetchAllTopicConfigs(zkUtils));
        zkUtils.close();
    }

    public String getZookeeper_ip_port() {
        return zookeeper_ip_port;
    }

    public KafkaApiUtil setZookeeper_ip_port(String zookeeper_ip_port) {
        this.zookeeper_ip_port = zookeeper_ip_port;
        return this;
    }

    public String getZookeeper_path() {
        return zookeeper_path;
    }

    public KafkaApiUtil setZookeeper_path(String zookeeper_path) {
        this.zookeeper_path = zookeeper_path;
        return this;
    }

    /**
     * 查询所有话题
     *
     * @param brokerUrl
     * @param propertiesFile
     * @return
     */
    public Collection<TopicListing> listTopic(String brokerUrl, String propertiesFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //java.security.auth.login.config 变量设置
        String propertyAuth = properties.getProperty("java.security.auth.login.config");
        if (propertyAuth != null && !"".equals(propertyAuth)) {
            logger.info("java.security.auth.login.config is not null，{}", propertyAuth);
            System.setProperty("java.security.auth.login.config", propertyAuth);
            properties.remove("java.security.auth.login.config");
            logger.info("java.security.auth.login.config remove from properties");
        }
//        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        AdminClient adminClient = AdminClient.create(properties);
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        ListTopicsResult result = adminClient.listTopics(listTopicsOptions);
        Collection<TopicListing> list = null;
        try {
            list = result.listings().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        adminClient.close();
        for (TopicListing listing : list) {
            logger.info("listTopic：{}", listing.name());
        }
        return list;
    }

    public void createTopics(String brokerUrl, String topic_name) {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        AdminClient adminClient = AdminClient.create(properties);
        NewTopic newTopic = new NewTopic(topic_name, 1, (short) 1);
        Collection<NewTopic> newTopicList = new ArrayList<>();
        newTopicList.add(newTopic);
        adminClient.createTopics(newTopicList);
        adminClient.close();
    }

    /**
     * 根据key和分区数计算出当前分区
     *
     * @param key
     * @param numPartitions
     * @return
     */
    public int getPartition(byte[] key, int numPartitions) {
        return Utils.abs(Utils.murmur2(key)) % numPartitions;
    }

    /**
     * 消费组工具
     *
     * @throws IOException
     */
    public void consumerGroupCommand() throws IOException {
        String brokers = "10.1.8.200:9092,10.1.8.201:9092,10.1.8.202:9092";
        String groupId = "test";
        String commandConfig = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\consumergroups.properties";

        String[] args = {"--describe", "--bootstrap-server", brokers, "--group", groupId, "--command-config", commandConfig};
        Configuration.setConfiguration(new SimpleClientConfiguration("admin", "admin"));
//        ConsumerGroupCommand.main(args);


//        ConsumerGroupCommand.ConsumerGroupCommandOptions opts =
//                new ConsumerGroupCommand.ConsumerGroupCommandOptions(args);
//        ConsumerGroupCommand.KafkaConsumerGroupService kafkaConsumerGroupService =
//                new ConsumerGroupCommand.KafkaConsumerGroupService(opts);
//        scala.Tuple2<scala.Option<String>, scala.Option<scala.collection.Seq<ConsumerGroupCommand
//                .PartitionAssignmentState>>> res = kafkaConsumerGroupService.describeGroup();
//        kafkaConsumerGroupService.describeGroup();
//        scala.collection.Seq<ConsumerGroupCommand.PartitionAssignmentState> pasSeq = res._2.get();
//        scala.collection.Iterator<ConsumerGroupCommand.PartitionAssignmentState> iterable = pasSeq.iterator();
//        while (iterable.hasNext()) {
//            ConsumerGroupCommand.PartitionAssignmentState pas = iterable.next();
//            System.out.println(String.format("\n%-30s %-10s %-15s %-15s %-10s %-50s%-30s %s",
//                    pas.topic().get(), pas.partition().get(), pas.offset().get(),
//                    pas.logEndOffset().get(), pas.lag().get(), pas.consumerId().get(),
//                    pas.host().get(), pas.clientId().get()));
//        }


        ConsumerGroupCommand.ConsumerGroupCommandOptions options =
                new ConsumerGroupCommand.ConsumerGroupCommandOptions(args);
        ConsumerGroupCommand.KafkaConsumerGroupService kafkaConsumerGroupService =
                new ConsumerGroupCommand.KafkaConsumerGroupService(options);
        ObjectMapper mapper = new ObjectMapper();
        //1. 使用jackson-module-scala_2.12
        mapper.registerModule(new DefaultScalaModule());
        //2. 反序列化时忽略对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //3. 将Scala对象序列化成JSON字符串
        String source = mapper.writeValueAsString(kafkaConsumerGroupService.describeGroup()._2.get());
        //4. 将JSON字符串反序列化成Java对象
        List<PartitionAssignmentState> target = mapper.readValue(source,
                getCollectionType(mapper, List.class, PartitionAssignmentState.class));
        for (PartitionAssignmentState pas : target) {
            System.out.println(String.format("%s %s %s %s %s %s"
                    , pas.getTopic(), pas.getPartition(), pas.getHost(), pas.getOffset()
                    , pas.getLogEndOffset(), pas.getLag()));
        }
//        //5. 排序
//        target.sort((o1, o2) -> o1.getPartition() - o2.getPartition());
//        //6. 打印
//        printPasList(target);
    }

    /**
     * 将JSON字符串反序列化成Java对象
     *
     * @param mapper
     * @param collectionClass
     * @param elementClasses
     * @return
     */
    private JavaType getCollectionType(ObjectMapper mapper
            , Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 打印
     *
     * @param list
     */
    private void printPasList(List<PartitionAssignmentState> list) {
        System.out.println(String.format("%-40s %-10s %-15s %-15s %-10s %-50s%-30s %s",
                "TOPIC", "PARTITION", "CURRENT-OFFSET", "LOG-END-OFFSET", "LAG", "CONSUMER-ID", "HOST", "CLIENT-ID"));
        list.forEach(item -> {
            System.out.println(String.format("%-40s %-10s %-15s %-15s %-10s %-50s%-30s %s",
                    item.getTopic(), item.getPartition(), item.getOffset(), item.getLogEndOffset(), item.getLag(),
                    Optional.ofNullable(item.getConsumerId()).orElse("-"),
                    Optional.ofNullable(item.getHost()).orElse("-"),
                    Optional.ofNullable(item.getClientId()).orElse("-")));
        });
    }
}
