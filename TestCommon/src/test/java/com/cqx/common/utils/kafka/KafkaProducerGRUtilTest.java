package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.system.SleepUtil;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;

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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT1");
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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT1");
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
//            avroLevelData.putChildVal("after", "CREATE_TIME", "2021-08-12 11:33:00");
            avroLevelData.putChildVal("after", "CREATE_TIME", "null");
            avroLevelData.putChildVal("after", "CREATE_TIME_isMissing", true);
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

    @Test
    public void sendUSER_PRODUCT_schema() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("topology.receiver.buffer.size", "8");
        params.put("ogg_topic_name", "ogg_test");
        params.put("storm.principal.tolocal", "backtype.storm.security.auth.DefaultPrincipalToLocal");
        // 去掉key中带点的
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.contains(".")) {
                keys.add(key);
            }
        }
        logger.info("keys：{}", keys);
        for (String k : keys) {
            params.remove(k);
        }
        logger.info("params：{}", params);

        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        String topic = "USER_PRODUCT";
        String schema = "{\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"TB_SER_OGG_TEST_USER_PRODUCT\",\n" +
                "  \"namespace\": \"FRTBASE\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"table\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"op_type\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"op_ts\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"current_ts\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"pos\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"primary_keys\",\n" +
                "      \"type\": {\n" +
                "        \"type\": \"array\",\n" +
                "        \"items\": \"string\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"tokens\",\n" +
                "      \"type\": {\n" +
                "        \"type\": \"map\",\n" +
                "        \"values\": \"string\"\n" +
                "      },\n" +
                "      \"default\": {}\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"before\",\n" +
                "      \"type\": [\n" +
                "        \"null\",\n" +
                "        {\n" +
                "          \"type\": \"record\",\n" +
                "          \"name\": \"columns\",\n" +
                "          \"fields\": [\n" +
                "            {\n" +
                "              \"name\": \"HOME_CITY\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"HOME_CITY_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"PRODUCT_TYPE\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"PRODUCT_TYPE_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"SUBSCRIPTION_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"SUBSCRIPTION_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"USER_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"USER_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"PRODUCT_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"PRODUCT_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"STATUS\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"STATUS_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"INURE_TIME\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"string\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"INURE_TIME_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"EXPIRE_TIME\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"string\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"EXPIRE_TIME_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"OPERATION_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"OPERATION_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"HISTORY_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"HISTORY_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_TIME\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"string\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_TIME_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"MODIFY_TIME\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"string\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"MODIFY_TIME_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"MODIFY_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"MODIFY_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"REQUEST_SOURCE\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"REQUEST_SOURCE_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"TEST_FLAG\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"TEST_FLAG_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"REC_UPDATE_TIME\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"string\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"REC_UPDATE_TIME_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_REQUEST_SOURCE\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_REQUEST_SOURCE_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_ACCEPT_ID\",\n" +
                "              \"type\": [\n" +
                "                \"null\",\n" +
                "                \"long\"\n" +
                "              ],\n" +
                "              \"default\": null\n" +
                "            },\n" +
                "            {\n" +
                "              \"name\": \"CREATE_ACCEPT_ID_isMissing\",\n" +
                "              \"type\": \"boolean\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"default\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"after\",\n" +
                "      \"type\": [\n" +
                "        \"null\",\n" +
                "        \"columns\"\n" +
                "      ],\n" +
                "      \"default\": null\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic(topic);//设置话题
            for (int i = 0; i < 5; i++) {
                kafkaProducerGRUtil.send(topic, schema.getBytes());
                SleepUtil.sleepMilliSecond(200);
            }
        }
    }

    /**
     * 往scram认证模式下的kafka话题发送随机数据
     *
     * @throws Exception
     */
    @Test
    public void sendScramRandom() throws Exception {
        Map param = (Map) getParam("kafka_scram.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerUtil<String, byte[]> kafkaProducerGRUtil = new KafkaProducerUtil<>(param)) {
            String topic = (String) param.get("topic");//获取话题
            String value = System.currentTimeMillis() + "";
            String key = String.valueOf(value.hashCode());
            Future<RecordMetadata> metadataFuture = kafkaProducerGRUtil.send(topic, key, value.getBytes());//随机产生数据
            RecordMetadata recordMetadata = metadataFuture.get();
            logger.info("recordMetadata:{}", recordMetadata);
        }
    }
}