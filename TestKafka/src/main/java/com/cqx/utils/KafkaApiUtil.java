package com.cqx.utils;

import kafka.admin.AdminUtils;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * KafkaApiUtil
 *
 * @author chenqixu
 */
public class KafkaApiUtil {

    public static final String zookeeper_param = "--zookeeper";
    private static Logger logger = LoggerFactory.getLogger(KafkaApiUtil.class);
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

    public void deleteTopicByName(String topic_name) {
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

    public void listTopic(String brokerUrl, String propertiesFile) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(list);
        adminClient.close();
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
}
