package com.cqx.common.utils.kafka;

import com.cqx.common.test.TestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaProducerGRUtilTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerGRUtilTest.class);

    @Test
    public void sendRandom() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            String topic = (String) param.get("topic");//获取话题
            kafkaProducerGRUtil.setTopic(topic);//设置话题
            kafkaProducerGRUtil.sendRandom();//随机产生数据
        }
    }
}