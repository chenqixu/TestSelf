package com.cqx.common.utils.kafka;

import kafka.admin.AclCommand;
import kafka.admin.ConsumerGroupCommand;
import kafka.admin.TopicCommand;

import javax.security.auth.login.Configuration;
import java.util.Map;
import java.util.Properties;

/**
 * API工具
 *
 * @author chenqixu
 */
public class KafkaAPIUtil {
    private String bootstrap_servers;
    private String kafka_username;
    private String kafka_password;
    private String kafkaSecurityProtocol;

    public KafkaAPIUtil(Map conf) {
        Properties properties = KafkaPropertiesUtil.initConf((Map) conf.get("param"));
        bootstrap_servers = properties.getProperty("bootstrap.servers");
        kafka_username = properties.getProperty("newland.kafka_username");
        kafka_password = properties.getProperty("newland.kafka_password");
        kafkaSecurityProtocol = properties.getProperty("sasl.mechanism");
    }

    /**
     * 查看消费组偏移量
     *
     * @param group_id
     * @param consumergroups_properties
     */
    public void ConsumerGroupCommand(String group_id, String consumergroups_properties) {
        String[] args = {
                "--bootstrap-server"
                , bootstrap_servers
                , "--group"
                , group_id
                , "--describe"
                , "--command-config"
                , consumergroups_properties
        };
        Configuration.setConfiguration(new SimpleClientConfiguration(
                kafka_username
                , kafka_password
                , kafkaSecurityProtocol));
        ConsumerGroupCommand.main(args);
    }

    /**
     * 查看offset，只能在2.13使用，2.11用不了<br>
     * 示例：bin/kafka-run-class.sh kafka.tools.GetOffsetShell --bootstrap-server edc-mqc-01:9094 --topic nl_sip_test_v1 --time -1 --command-config config/consumer-test.properties<br>
     * --time <String: <timestamp> / -1 or      timestamp of the offsets before that.
     *   latest / -2 or earliest / -3 or max-     [Note: No offset is returned, if the
     *   timestamp>                               timestamp greater than recently
     *                                            committed record timestamp is
     *                                            given.] (default: latest)
     * @param bootstrap_servers
     * @param topic
     * @param time
     */
    public void GetOffsetShell(String bootstrap_servers, String topic, String time, String command_config) {
        String[] args = {
                "--bootstrap-server"
                , bootstrap_servers
                , "--topic"
                , topic
                , "--time"
                , time
                , "--command-config"
                , command_config
        };
        Configuration.setConfiguration(new SimpleClientConfiguration(
                kafka_username
                , kafka_password
                , kafkaSecurityProtocol));
        kafka.tools.GetOffsetShell.main(args);
    }

    /**
     * 查看话题权限
     *
     * @param zookeeper
     * @param topic
     */
    public void AclCommandByTopic(String zookeeper, String topic) {
        String[] args = {
                "--authorizer-properties"
                , "zookeeper.connect=" + zookeeper
                , "--list"
                , "--topic"
                , topic
        };
        AclCommand.main(args);
    }

    /**
     * 查询消费组权限
     *
     * @param zookeeper
     * @param group
     */
    public void AclCommandByGroup(String zookeeper, String group) {
        String[] args = {
                "--authorizer-properties"
                , "zookeeper.connect=" + zookeeper
                , "--list"
                , "--group"
                , group
        };
        AclCommand.main(args);
    }

    /**
     * 查看所有话题清单
     *
     * @param bootstrap_servers
     */
    public void listTopic(String bootstrap_servers) {
        String[] args = {
                "--bootstrap-server"
                , bootstrap_servers
                , "--list"
        };
        TopicCommand.main(args);
    }
}
