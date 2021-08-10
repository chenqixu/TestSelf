package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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

    @Test
    public void sendUSER_PRODUCT_updatePks() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_TEST_USER_PRODUCT");
            avroLevelData.putVal("op_type", "U");
            String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
            avroLevelData.putVal("current_ts", now);
            avroLevelData.putVal("primary_keys", Arrays.asList("HOME_CITY",
                    "PRODUCT_TYPE",
                    "SUBSCRIPTION_ID",
                    "USER_ID",
                    "PRODUCT_ID"));
            // before
            avroLevelData.putChildVal("before", "HOME_CITY", 591L);
            avroLevelData.putChildVal("before", "PRODUCT_TYPE", 1002L);
            avroLevelData.putChildVal("before", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("before", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("before", "PRODUCT_ID", 1002160002L);
            // after
            avroLevelData.putChildVal("after", "HOME_CITY", 591L);
            avroLevelData.putChildVal("after", "PRODUCT_TYPE", 1004L);
            avroLevelData.putChildVal("after", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("after", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("after", "PRODUCT_ID", 1002160002L);
            avroLevelData.putChildVal("after", "STATUS", 3L);
            DefaultBean defaultBean = new DefaultBean();
            defaultBean.setDefault_boolean(false);
            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            kafkaProducerGRUtil.sendRandom(avroLevelData).get();
        }
    }

    @Test
    public void sendUSER_PRODUCT_insert() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_TEST_USER_PRODUCT");
            avroLevelData.putVal("op_type", "I");
            String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
            avroLevelData.putVal("current_ts", now);
            avroLevelData.putVal("primary_keys", Arrays.asList("HOME_CITY",
                    "PRODUCT_TYPE",
                    "SUBSCRIPTION_ID",
                    "USER_ID",
                    "PRODUCT_ID"));
            // after
            avroLevelData.putChildVal("after", "HOME_CITY", 591L);
            avroLevelData.putChildVal("after", "PRODUCT_TYPE", 1002L);
            avroLevelData.putChildVal("after", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("after", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("after", "PRODUCT_ID", 1002160002L);
            kafkaProducerGRUtil.sendRandom(avroLevelData).get();
        }
    }

    @Test
    public void sendUSER_PRODUCT_update() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_TEST_USER_PRODUCT");
            avroLevelData.putVal("op_type", "U");
            String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
            avroLevelData.putVal("current_ts", now);
            avroLevelData.putVal("primary_keys", Arrays.asList("HOME_CITY",
                    "PRODUCT_TYPE",
                    "SUBSCRIPTION_ID",
                    "USER_ID",
                    "PRODUCT_ID"));
            // before
            avroLevelData.putChildVal("before", "HOME_CITY", 591L);
            avroLevelData.putChildVal("before", "PRODUCT_TYPE", 1002L);
            avroLevelData.putChildVal("before", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("before", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("before", "PRODUCT_ID", 1002160002L);
            // after
            avroLevelData.putChildVal("after", "HOME_CITY", 591L);
            avroLevelData.putChildVal("after", "PRODUCT_TYPE", 1002L);
            avroLevelData.putChildVal("after", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("after", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("after", "PRODUCT_ID", 1002160002L);
            avroLevelData.putChildVal("after", "STATUS", 2L);
            DefaultBean defaultBean = new DefaultBean();
            defaultBean.setDefault_boolean(false);
            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            kafkaProducerGRUtil.sendRandom(avroLevelData).get();
        }
    }

    @Test
    public void sendUSER_PRODUCT_delete() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_TEST_USER_PRODUCT");
            avroLevelData.putVal("op_type", "D");
            String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
            avroLevelData.putVal("current_ts", now);
            avroLevelData.putVal("primary_keys", Arrays.asList("HOME_CITY",
                    "PRODUCT_TYPE",
                    "SUBSCRIPTION_ID",
                    "USER_ID",
                    "PRODUCT_ID"));
            // before
            avroLevelData.putChildVal("before", "HOME_CITY", 591L);
            avroLevelData.putChildVal("before", "PRODUCT_TYPE", 1004L);
            avroLevelData.putChildVal("before", "SUBSCRIPTION_ID", 206411412722L);
            avroLevelData.putChildVal("before", "USER_ID", 591305002979620L);
            avroLevelData.putChildVal("before", "PRODUCT_ID", 1002160002L);
            kafkaProducerGRUtil.sendRandom(avroLevelData).get();
        }
    }
}