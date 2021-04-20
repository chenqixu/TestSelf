package com.cqx.common.utils.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * kafka属性工具
 *
 * @author chenqixu
 */
public class KafkaPropertiesUtil {
    private static final String KAFKA_HEADER = "kafkaconf.";
    private static final String KAFKA_NEWLAND_HEADER = "newland.";
    private static final Logger logger = LoggerFactory.getLogger(KafkaPropertiesUtil.class);

    /**
     * 根据内存中的内存拼接一个配置类
     *
     * @param stormConf
     * @return
     */
    public static Properties initConf(Map stormConf) {
        Properties properties = new Properties();
        for (Object entry : stormConf.entrySet()) {
            String key = ((Map.Entry<String, String>) entry).getKey();
            if (key.startsWith(KAFKA_HEADER)) {
                String _key = key.replace(KAFKA_HEADER, "");
                String value = ((Map.Entry<String, Object>) entry).getValue().toString();
                logger.info("从参数中获取到的kafka配置的key：{}，value：{}", _key, value);
                properties.setProperty(_key, value);
            }
        }
        return properties;
    }

    /**
     * 移除属性中的newland自定义参数
     *
     * @param properties
     */
    public static void removeNewlandProperties(Properties properties) {
        for (String name : properties.stringPropertyNames()) {
            if (name.startsWith(KAFKA_NEWLAND_HEADER)) {
                properties.remove(name);
            }
        }
    }
}
