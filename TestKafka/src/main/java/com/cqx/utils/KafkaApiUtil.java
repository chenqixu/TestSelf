package com.cqx.utils;

import kafka.admin.AdminUtils;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
