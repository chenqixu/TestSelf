package com.cqx.utils;

import org.apache.kafka.clients.admin.TopicListing;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class KafkaApiUtilTest {

    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaApiUtil kafkaApiUtil;
    private String zookeeper_ip_port;
    private String zookeeper_path;
    private String brokerUrl;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(path + "single.properties"));
        zookeeper_ip_port = properties.getProperty("zookeeper_ip_port");
        zookeeper_path = properties.getProperty("zookeeper_path");
        kafkaApiUtil = KafkaApiUtil.builder()
                .setZookeeper_ip_port(zookeeper_ip_port)
                .setZookeeper_path(zookeeper_path);
        brokerUrl = "edc-mqc-01:9092";
    }

    @Test
    public void getAllTopic() {
        kafkaApiUtil.getAllTopic();
    }

    @Test
    public void queryTopicByName() {
        kafkaApiUtil.queryTopicByName("nmc_tb_lte_http");
    }

    @Test
    public void createTopicByName() {
        kafkaApiUtil.createTopicByName("nmc_tb_lte_http", 1, 1);
    }

    @Test
    public void deleteTopicByName() {
        kafkaApiUtil.deleteTopicByName("topic1");
    }

    @Test
    public void fetchAllTopicConfigs() {
        kafkaApiUtil.fetchAllTopicConfigs();
    }

    @Test
    public void listTopic() {
        kafkaApiUtil.listTopic(brokerUrl, path + "producer.properties");
    }

    @Test
    public void createTopics() {
        kafkaApiUtil.createTopics(brokerUrl, "test123_topic");
    }

    @Test
    public void getPartition() {
        byte[] key = "13509323824".getBytes();
        System.out.println(String.format("get %s", kafkaApiUtil.getPartition(key, 18)));
    }

    @Test
    public void deleteAllTopic() {
        Collection<TopicListing> topicListings = kafkaApiUtil.listTopic(brokerUrl, path + "single.properties");
        for (TopicListing listing : topicListings) {
            String name = listing.name();
            if (name.startsWith("59")) {
                kafkaApiUtil.deleteTopicByName(name);
            }
        }
    }

    @Test
    public void consumerGroupCommand() throws IOException {
        kafkaApiUtil.consumerGroupCommand();
    }
}