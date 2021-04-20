package com.cqx.common.utils.kafka;

import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import java.util.HashMap;

/**
 * SimpleClientConfiguration
 *
 * @author chenqixu
 */
public class SimpleClientConfiguration extends Configuration {

    private static Logger logger = LoggerFactory.getLogger(SimpleClientConfiguration.class);
    private String username;
    private String password;

    public SimpleClientConfiguration(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        if ("KafkaClient".equalsIgnoreCase(name)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            AppConfigurationEntry configurationEntry
                    = new AppConfigurationEntry(PlainLoginModule.class.getName(), LoginModuleControlFlag.REQUIRED, map);
            logger.info("KafkaClient return：{}", configurationEntry);
            return new AppConfigurationEntry[]{configurationEntry};
        }
        logger.info("KafkaClient return:null");
        return null;
    }

}
