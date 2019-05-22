package com.cqx.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Properties;

public class KafkaApiUtilTest {

    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaApiUtil kafkaApiUtil;
    private String zookeeper_ip_port;
    private String zookeeper_path;

    @Before
    public void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(path + "common.vm.properties"));
        zookeeper_ip_port = properties.getProperty("zookeeper_ip_port");
        zookeeper_path = properties.getProperty("zookeeper_path");
        kafkaApiUtil = KafkaApiUtil.builder()
                .setZookeeper_ip_port(zookeeper_ip_port)
                .setZookeeper_path(zookeeper_path);
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
}