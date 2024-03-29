package com.cqx.common.utils.kafka;

import com.cqx.common.bean.kafka.AvroLevelData;
import com.cqx.common.bean.kafka.DefaultBean;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.string.StringUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void sendTest1() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            for (int i = 0; i < 1000000; i++)
                kafkaProducerGRUtil.send("test1", ("{\"message\":\"" + System.currentTimeMillis() + "\"}").getBytes());
        }
    }

    @Test
    public void sendTest1WithBtime() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            for (int i = 0; i < 5; i++) {
                // 时间戳
//                kafkaProducerGRUtil.send("test1", ("{\"btime\":\"" + System.currentTimeMillis() + "\"}").getBytes());
                // 字符串
                kafkaProducerGRUtil.send("test1", ("{\"msisdn\":\"13500000001\", \"btime\":\"" + Utils.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + "\"}").getBytes());
            }
        }
    }

    @Test
    public void sendTest1AvroWithBtime() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        param.put("kafkaconf.newland.schema.mode", "FILE");
        param.put("kafkaconf.newland.schema.file", "d:/tmp/data/avro/test1.avsc");
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("test1");
            for (int i = 0; i < 1000; i++) {
                // 时间戳
                Map<String, String> map = new HashMap<>();
//                map.put("btime", "" + System.currentTimeMillis());
                // 字符串
                map.put("btime", Utils.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss"));
                kafkaProducerGRUtil.sends(map);
            }
        }
    }

    @Test
    public void sendTest1WithWatermark() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.send("test1", ("{\"user_id\":\"12345\"" +
                    ",\"create_time\":\"" + Utils.formatTime(System.currentTimeMillis() - 10000, "yyyy-MM-dd HH:mm:ss") + "\"}").getBytes());
            kafkaProducerGRUtil.send("test1", ("{\"user_id\":\"12345\"" +
                    ",\"create_time\":\"" + Utils.getNow("yyyy-MM-dd HH:mm:ss") + "\"}").getBytes());
            kafkaProducerGRUtil.send("test2", ("{\"user_id\":\"12345\"" +
                    ",\"request_source\":\"34001\"" +
                    ",\"rec_create_time\":\"" + Utils.getNow("yyyy-MM-dd HH:mm:ss") + "\"}").getBytes());
        }
    }

    @Test
    public void sendTest1Avro() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        Random random = new Random(System.currentTimeMillis());
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("test1");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("ogg_schema");
            avroLevelData.putVal("city", "福州");
            avroLevelData.putVal("xdr_id", "12345");
            avroLevelData.putVal("imsi", "0x00");
            avroLevelData.putVal("imei", "0x01");
            avroLevelData.putVal("msisdn", "135" + StringUtil.fillZero(random.nextInt(99999999), 8));
            kafkaProducerGRUtil.sendRandom(avroLevelData);

            byte[] message = kafkaProducerGRUtil.getGenericRecordUtil()
                    .genericRandomRecordByAvroRecord("test1", avroLevelData);
            for (byte b : message) {
                System.out.println(b);
            }
        }
    }

    @Test
    public void sendTest1Json() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        Random random = new Random(System.currentTimeMillis());
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("test1");//设置话题
            // json
            for (int i = 0; i < 10; i++) {
                kafkaProducerGRUtil.send("test1", ("{\"xdr_id\":\"12345\",\"msisdn\":\"135"
                        + StringUtil.fillZero(random.nextInt(99999999), 8) + "\"}").getBytes());
            }
        }
    }

    @Test
    public void sendTest1OggJson() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("test1");//设置话题
            // ogg json
            kafkaProducerGRUtil.send("test1", "{\"table\":\"FRTBASE.TB_SER_OGG_USERS\",\"op_type\":\"I\",\"op_ts\":\"2023-08-01 01:17:17.063199\",\"current_ts\":\"2023-08-01T09:17:21.979003\",\"pos\":\"00000033390570320959\",\"after\":{\"HOME_CITY\":592,\"USER_ID\":592500345139683,\"NETWORK_TYPE\":3,\"CUSTOMER_ID\":592100448808481,\"TYPE\":1,\"SERVICE_TYPE\":1,\"MSISDN\":18950094952,\"IMSI\":460076524086874,\"USER_BRAND\":1000,\"HOME_COUNTY\":206,\"CREATOR\":149418,\"CREATE_TIME\":null,\"CREATE_SITE\":2060971,\"SERVICE_STATUS\":0,\"PASSWORD\":\"8EF7178E286B757C\",\"TRANSFER_TIME\":null,\"STOP_TIME\":null,\"MODIFY_ID\":149418,\"MODIFY_SITE\":2060971,\"MODIFY_TIME\":\"2023-08-01 09:17:11\",\"MODIFY_CONTENT\":\"用户创建\",\"RC_SN\":null,\"RC_EXPIRE_TIME\":\"2023-08-01 09:17:11\",\"ORDER_SEQ\":287989661259,\"BROKER_ID\":null,\"HISTORY_SEQ\":215437636818,\"LOCK_FLAG\":\"0\",\"BILL_TYPE\":5,\"BILL_CREDIT\":99999999,\"BILL_TIME\":null,\"EXPIRE_TIME\":null,\"ARCHIVES_CREATE_TIME\":\"2023-08-01 09:17:11\",\"PASSWORD_GET_TYPE\":0,\"PASSWORD_GET_TIME\":null,\"PASSWORD_RESET_TIME\":null,\"SUB_TYPE\":0}}".getBytes());
        }
    }

    @Test
    public void sendTest1CSV() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("test1");//设置话题
            // ogg json
            kafkaProducerGRUtil.send("test1", "123,test".getBytes());
            kafkaProducerGRUtil.send("test1", "1234,test1".getBytes());
        }
    }

    @Test
    public void sendRandomAndCheck() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");// 从配置文件解析参数
        param.put("kafkaconf.newland.schema.mode", "NOAVRO");
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            final String topic = "test1";
            StringBuilder value = new StringBuilder();
            for (int x = 0; x < 500; x++) {
                value.append("a");
            }

            AtomicInteger down = new AtomicInteger(0);
            Map<String, List<Future<RecordMetadata>>> map = new HashMap<>();
            TimeCostUtil tc = new TimeCostUtil();
            // 模拟fileNum个文件
            final int fileNum = 10;
            final int fileCount = 20000;
            for (int j = 0; j < fileNum; j++) {
                String fileName = "batch-" + j;
                map.put(fileName, new ArrayList<>());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Future<RecordMetadata>> list = map.get(fileName);
                        tc.start();
                        // 每个文件fileCount条记录
                        for (int i = 0; i < fileCount; i++) {
                            // 随机产生数据
                            Future<RecordMetadata> recordMetadataFuture = kafkaProducerGRUtil.send(topic, value.toString().getBytes(StandardCharsets.UTF_8));
                            list.add(recordMetadataFuture);
                        }
                        logger.info("fileName={}, cost={} ms", fileName, tc.stopAndGet());

                        // 启动校验线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int check = 0;
                                TimeCostUtil tc = new TimeCostUtil();
                                tc.start();
                                logger.info("启动校验线程, fileName={}", fileName);
                                for (Future<RecordMetadata> future : list) {
                                    try {
                                        RecordMetadata recordMetadata = future.get();
                                        check += recordMetadata.hasOffset() ? 1 : 0;
                                    } catch (InterruptedException | ExecutionException e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                                logger.info("校验线程完成, fileName={}, check={}, cost={} ms", fileName, check, tc.stopAndGet());
                                down.incrementAndGet();
                            }
                        }).start();
                    }
                }).start();
            }
            while (down.get() != fileNum) {
                SleepUtil.sleepMilliSecond(500);
            }
        }
    }

    @Test
    public void sendUSER_PRODUCT() throws Exception {
        Map param = (Map) getParam("kafka.yaml").get("param");//从配置文件解析参数
        try (KafkaProducerGRUtil kafkaProducerGRUtil = new KafkaProducerGRUtil(param)) {
            kafkaProducerGRUtil.setTopic("USER_PRODUCT");//设置话题
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT");
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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT");
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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT");
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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT");
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
            AvroLevelData avroLevelData = AvroLevelData.newInstance("TB_SER_OGG_USER_PRODUCT");
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
     * 往kafka_2.13-3.2.0版本scram认证模式下的kafka话题发送随机数据
     *
     * @throws Exception
     */
    @Test
    public void send213_320_ScramRandom() throws Exception {
        Map param = (Map) getParam("kafka_2.13-3.2.0-scram.yaml").get("param");//从配置文件解析参数
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