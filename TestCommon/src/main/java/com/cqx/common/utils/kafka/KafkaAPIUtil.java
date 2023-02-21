package com.cqx.common.utils.kafka;

import kafka.admin.AclCommand;
import kafka.admin.ConsumerGroupCommand;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.Configuration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * API工具
 *
 * @author chenqixu
 */
public class KafkaAPIUtil {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAPIUtil.class);
    private Properties properties;
    private String bootstrap_servers;
    private String kafka_username;
    private String kafka_password;
    private String kafkaSecurityProtocol;

    public KafkaAPIUtil(Map conf) {
        properties = KafkaPropertiesUtil.initConf(conf);
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
     * 调用API列出话题清单<br>
     * 执行需要加上-Djava.security.auth.login.config=I:\Document\Workspaces\Git\TestSelf\TestCommon\src\test\resources\jaas\kafka_server_scram_jaas.conf
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void listTopicByAPI() throws ExecutionException, InterruptedException {
        ListTopicsResult result = Admin.create(properties).listTopics();
        logger.info("{}", result.names().get());
    }

    /**
     * 调用scala命令列出话题清单<br>
     * 注意：需要使用--command-config加载认证方式<br>
     * 执行需要加上-Djava.security.auth.login.config=I:\Document\Workspaces\Git\TestSelf\TestCommon\src\test\resources\jaas\kafka_server_scram_jaas.conf
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void listTopicByCommand() throws ExecutionException, InterruptedException {
        String[] args = {
                "--bootstrap-server"
                , bootstrap_servers
                , "--list"
                , "--command-config"
                , "I:\\Document\\Workspaces\\Git\\TestSelf\\TestCommon\\src\\test\\resources\\consumer.properties"
        };
        kafka.admin.TopicCommand.main(args);
    }
}
