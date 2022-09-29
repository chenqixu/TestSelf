package com.cqx.common.utils.kafka;

import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.security.scram.ScramLoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import java.util.HashMap;

/**
 * SimpleClientConfiguration<br>
 * <pre>
 *     GSSAPI：使用的Kerberos认证，可以集成目录服务，比如AD。Kafka最小版本 0.9
 *     PLAIN：使用简单用户名和密码形式，Kafka最小版本 0.10
 *     SCRAM：主要解决PLAIN动态更新问题以及安全机制，Kafka最小版本 0.10.2
 *     OAUTHBEARER：基于OAuth 2认证框架，Kafka最小版本 2.0
 * </pre>
 *
 * @author chenqixu
 */
public class SimpleClientConfiguration extends Configuration {
    public static final String PlainProtocol = "PLAIN";
    public static final String ScramProtocol = "SCRAM";
    private static final Logger logger = LoggerFactory.getLogger(SimpleClientConfiguration.class);
    private String username;
    private String password;
    private String loginModeName;

    public SimpleClientConfiguration(String username, String password) {
        this(username, password, PlainProtocol);
    }

    public SimpleClientConfiguration(String username, String password, String kafkaSecurityProtocol) {
        this.username = username;
        this.password = password;
        if (kafkaSecurityProtocol != null && kafkaSecurityProtocol.startsWith(ScramProtocol)) {
            loginModeName = ScramLoginModule.class.getName();
        } else {// 默认是简单认证
            loginModeName = PlainLoginModule.class.getName();
        }
    }

    /**
     * 返回sasl.jaas.config的配置，默认ACL认证
     *
     * @param username
     * @param password
     * @return
     */
    public static String getSaslJaasConfig(String username, String password) {
        return getSaslJaasConfig(username, password, PlainProtocol);
    }

    /**
     * 返回sasl.jaas.config的配置
     *
     * @param username
     * @param password
     * @param kafkaSecurityProtocol
     * @return
     */
    public static String getSaslJaasConfig(String username, String password, String kafkaSecurityProtocol) {
        String sasl_jaas_config;
        if (kafkaSecurityProtocol != null && kafkaSecurityProtocol.startsWith(SimpleClientConfiguration.ScramProtocol)) {
            sasl_jaas_config = "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                    "username=\"" + username + "\" " +
                    "password=\"" + password + "\";";
        } else {// 默认是简单认证
            sasl_jaas_config = "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                    "username=\"" + username + "\" " +
                    "password=\"" + password + "\";";
        }
        return sasl_jaas_config;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if ("KafkaClient".equalsIgnoreCase(name)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            AppConfigurationEntry configurationEntry
                    = new AppConfigurationEntry(loginModeName, LoginModuleControlFlag.REQUIRED, map);
            logger.info("KafkaClient return：{}", configurationEntry);
            return new AppConfigurationEntry[]{configurationEntry};
        }
        logger.info("KafkaClient return:null");
        return null;
    }

}
