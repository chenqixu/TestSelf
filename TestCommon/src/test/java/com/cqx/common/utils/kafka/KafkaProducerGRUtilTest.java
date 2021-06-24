package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import org.apache.avro.JsonProperties;
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
            for (int i = 0; i < 10; i++)
                kafkaProducerGRUtil.sendRandom();//随机产生数据
        }
    }

    @Test
    public void sendUSER_PRODUCT() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_TEST_USER_PRODUCT");
            avroLevelData.putVal("op_type", "U");
            String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
            avroLevelData.putVal("current_ts", now);
            avroLevelData.putChildVal("after", "HOME_CITY", 591L);
            avroLevelData.putChildVal("after", "STATUS", 1L);
//            for (int i = 0; i < 10; i++) {
//                kafkaProducerGRUtil.sendRandom();// 随机产生数据
            kafkaProducerGRUtil.sendRandom(avroLevelData);
//            }
        }
    }
}