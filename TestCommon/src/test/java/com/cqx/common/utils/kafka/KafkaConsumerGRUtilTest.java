package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.list.IKVList;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaConsumerGRUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGRUtilTest.class);

    @Test
    public void polls() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            String topic = (String) param.get("topic");//获取话题
            kafkaConsumerUtil.subscribe(topic);//订阅
            for (IKVList.Entry<String, GenericRecord> entry : kafkaConsumerUtil.pollsHasKey(1000L).entrySet()) {
                Object value = entry.getValue();
                logger.info("【key】{}，【value】{}，【value.class】{}",
                        entry.getKey(), value, value != null ? value.getClass() : null);
            }
            kafkaConsumerUtil.commitSync();
        }
    }

    @Test
    public void pollsUSER_PRODUCT() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        param.put("kafkaconf.enable.auto.commit", "false");//不进行自动提交
        logger.info("{}", param);
        try (KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param)) {
            kafkaConsumerUtil.subscribe("USER_PRODUCT");//订阅
            for (IKVList.Entry<String, GenericRecord> entry : kafkaConsumerUtil.pollsHasKey(1000L).entrySet()) {
                Object value = entry.getValue();
                logger.info("【key】{}，【value】{}，【value.class】{}",
                        entry.getKey(), value, value != null ? value.getClass() : null);
            }
        }
    }
}