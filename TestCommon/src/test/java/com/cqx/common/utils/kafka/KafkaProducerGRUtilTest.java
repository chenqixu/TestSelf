package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.system.SleepUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    /**
     * 构造NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1
     *
     * @param msisdn
     * @param exec_time
     * @return
     */
    private AvroLevelData buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(long msisdn, String exec_time) {
        AvroLevelData avroLevelData = AvroLevelData.newInstance("NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1");
        avroLevelData.putVal("op_type", "I");
        String now = Utils.getNow("yyyy-MM-dd'T'HH:mm:ss.SSS") + "000";
        avroLevelData.putVal("current_ts", now);
        avroLevelData.putVal("primary_keys", Arrays.asList("EXEC_TIME", "MSISDN"));
        // after
        avroLevelData.putChildVal("after", "MSISDN", msisdn);
        avroLevelData.putChildVal("after", "DUN_TYPE", 4L);
        avroLevelData.putChildVal("after", "EXEC_TIME", exec_time);
        avroLevelData.putChildVal("after", "USER_STATUS", 99L);
        avroLevelData.putChildVal("after", "BALANCE", 25500L);
        avroLevelData.putChildVal("after", "SUB_BALANCE", 2900L);
        avroLevelData.putChildVal("after", "TOTAL_OWING", 3900L);
        return avroLevelData;
    }

    /**
     * 模拟正常数据
     *
     * @throws Exception
     */
    @Test
    public void sendNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1_1() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1");//设置话题
            DefaultBean defaultBean = new DefaultBean();
            defaultBean.setDefault_boolean(false);
            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            // 模拟正常数据
//            for (int i = 0; i < 1600; i++) {
//                kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
//                        13500000001L + i, "2021-08-12 11:33:44"));
//            }
//            for (int i = 0; i < 1600; i++) {
//                kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
//                        13500000001L + i, "2021-08-12 11:33:45"));
//            }
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:51"));
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:51"));
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:54"));
        }
    }

    /**
     * 模拟重复数据
     *
     * @throws Exception
     */
    @Test
    public void sendNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1_2() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1");//设置话题
            DefaultBean defaultBean = new DefaultBean();
            defaultBean.setDefault_boolean(false);
            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            // 模拟重复数据
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:03")).get();
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000002L, "2021-08-12 11:33:03")).get();
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000001L, "2021-08-12 11:33:03")).get();
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:03")).get();
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:04")).get();
        }
    }

    /**
     * 模拟延迟数据
     *
     * @throws Exception
     */
    @Test
    public void sendNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1_3() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1");//设置话题
            DefaultBean defaultBean = new DefaultBean();
            defaultBean.setDefault_boolean(false);
            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            // 模拟延迟数据
            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
                    13500000000L, "2021-08-12 11:33:21")).get();
        }
    }

    /**
     * 模拟schema变更
     *
     * @throws Exception
     */
    @Test
    public void sendNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1_4() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            String topic = "NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1";
            // 模拟schema变更
            String schema = "{\"type\":\"record\",\"name\":\"NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1\",\"namespace\":\"FRTBASE\",\"fields\":[{\"name\":\"table\",\"type\":\"string\"},{\"name\":\"op_type\",\"type\":\"string\"},{\"name\":\"op_ts\",\"type\":\"string\"},{\"name\":\"current_ts\",\"type\":\"string\"},{\"name\":\"pos\",\"type\":\"string\"},{\"name\":\"primary_keys\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"tokens\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":{}},{\"name\":\"before\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"columns\",\"fields\":[{\"name\":\"MSISDN\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"MSISDN_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"DUN_TYPE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"DUN_TYPE_TYPE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"EXEC_TIME\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"EXEC_TIME_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"USER_STATUS\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"USER_STATUS_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"BALANCE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"BALANCE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"SUB_BALANCE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"SUB_BALANCE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"TOTAL_OWING\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"TOTAL_OWING_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null}]}],\"default\":null},{\"name\":\"after\",\"type\":[\"null\",\"columns\"],\"default\":null}]}";
//            schema = "{\"type\":\"record\",\"name\":\"NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1\",\"namespace\":\"FRTBASE\",\"fields\":[{\"name\":\"table\",\"type\":\"string\"},{\"name\":\"op_type\",\"type\":\"string\"},{\"name\":\"op_ts\",\"type\":\"string\"},{\"name\":\"current_ts\",\"type\":\"string\"},{\"name\":\"pos\",\"type\":\"string\"},{\"name\":\"primary_keys\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"tokens\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":{}},{\"name\":\"before\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"columns\",\"fields\":[{\"name\":\"MSISDN\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"MSISDN_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"DUN_TYPE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"DUN_TYPE_TYPE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"EXEC_TIME\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"EXEC_TIME_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"USER_STATUS\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"USER_STATUS_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"BALANCE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"BALANCE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"SUB_BALANCE\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"SUB_BALANCE_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"TOTAL_OWING\",\"type\":[\"null\",\"long\"],\"default\":null},{\"name\":\"TOTAL_OWING_isMissing\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"TEST_FIELD1\",\"type\":[\"null\",\"long\"],\"default\":null}]}],\"default\":null},{\"name\":\"after\",\"type\":[\"null\",\"columns\"],\"default\":null}]}";
            kafkaProducerGRUtil.send(topic, schema.getBytes()).get();
//            DefaultBean defaultBean = new DefaultBean();
//            defaultBean.setDefault_boolean(false);
//            kafkaProducerGRUtil.setDefaultBean(defaultBean);
            // 发送一条变更后的数据
//            kafkaProducerGRUtil.setTopic(topic, schema);// 设置话题
//            kafkaProducerGRUtil.sendRandom(buildNMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1(
//                    13500000003L, "2021-08-12 11:33:07")).get();
        }
    }

    /**
     * 消费者 && 生产者 协同事务测试
     *
     * @throws Exception
     */
    @Test
    public void transactionConsumerProducer() throws Exception {
        String consumerTopic = "NMC_TB_B_DUN_NOTIFY_RESULT_R_I_V1";
        String consumerGroupId = "grpid_nl_dun_notify_result_v1";// 设置消费组
        String producerTopic = "NMC_FLAT_B_DUN_NOTIFY_RESULT_R_I_V1";
        Map param = (Map) getParam("kafka.yaml").get("param");// 从配置文件解析参数
        param.put("kafkaconf.group.id", "grpid_nl_dun_notify_result_v1");// 设置消费组
        try (KafkaProducerGRUtil kafkaProducerUtil = new KafkaProducerGRUtil(param, true);
             KafkaConsumerGRUtil kafkaConsumerUtil = new KafkaConsumerGRUtil(param, true)) {
            kafkaConsumerUtil.subscribe(consumerTopic);
            int i = 0;
            while (i < 10) {
                List<ConsumerRecord<String, byte[]>> consumerRecords = kafkaConsumerUtil.pollHasConsumerRecord(1000L);
                if (consumerRecords.size() > 0) {
                    kafkaProducerUtil.newInstance();
                    boolean isFirst = true;
                    long firstOffset = 0L;
                    long lastOffset = 0L;
                    for (ConsumerRecord<String, byte[]> consumerRecord : consumerRecords) {
                        if (isFirst) {
                            firstOffset = consumerRecord.offset();
                            isFirst = false;
                        }
                        lastOffset = consumerRecord.offset();
                        kafkaProducerUtil.addProducerRecord(producerTopic, consumerRecord.key(), consumerRecord.value());
                    }
                    logger.info("消费到记录：{}，起始偏移量：{}，最后偏移量：{}", consumerRecords.size(), firstOffset, lastOffset);
                    kafkaProducerUtil.sendWithConsumerTransaction(kafkaProducerUtil.getProducerRecords()
                            , consumerTopic, 0, lastOffset, consumerGroupId);
                    logger.info("提交事务完成");
                }
                logger.info("i：{}", i);
                i++;
                SleepUtil.sleepSecond(3);
            }
        }
    }
}