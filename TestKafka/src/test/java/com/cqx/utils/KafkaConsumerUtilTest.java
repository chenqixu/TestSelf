package com.cqx.utils;

import com.cqx.common.utils.system.TimeCostUtil;
import org.apache.avro.generic.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KafkaConsumerUtilTest {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerUtilTest.class);
    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaConsumerUtil<String, byte[]> kafkaConsumerUtil;
    private RecordConvertor recordConvertor;
    private String topic = "nmc_tb_lte_http_test";
    private String conf = path + "consumer.properties";
    private String schemaUrl = "http://10.1.8.203:18061/SchemaService/getSchema?t=";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        if (kafkaConsumerUtil != null) kafkaConsumerUtil.close();
    }

    @Test
    public void poll() throws Exception {
        String topic = "nmc_tb_lte_http_test";
        recordConvertor = new RecordConvertor(topic);
//        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "admin", "admin");
        kafkaConsumerUtil.subscribe(topic);
        List<byte[]> list = kafkaConsumerUtil.poll(1000);
        for (byte[] bytes : list) {
            GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
            logger.info("genericRecord：{}", genericRecord);
        }
    }

    @Test
    public void pollS1mme() throws Exception {
        String topic = "nmc_tb_lte_s1mme";
        recordConvertor = new RecordConvertor(schemaUrl, topic);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "admin", "admin");
        kafkaConsumerUtil.subscribe(topic);
        //只消费一次
        List<byte[]> list = kafkaConsumerUtil.poll(2000);
        for (byte[] bytes : list) {
            GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
            logger.info("genericRecord：{}", genericRecord);
        }
        //2秒消费一次
//        while (true) {
//            SleepUtils.sleepMilliSecond(2000);
//            kafkaConsumerUtil.poll(1000);
//        }
    }

    @Test
    public void pollMcCdr() throws Exception {
        String topic = "nmc_tb_mc_cdr";
        recordConvertor = new RecordConvertor(schemaUrl, topic);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "admin", "admin");
        kafkaConsumerUtil.subscribe(topic);
        //只消费一次
        List<byte[]> list = kafkaConsumerUtil.poll(2000);
        for (byte[] bytes : list) {
            GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
            logger.info("genericRecord：{}", genericRecord);
        }
        //2秒消费一次
//        while (true) {
//            SleepUtils.sleepMilliSecond(2000);
//            kafkaConsumerUtil.poll(1000);
//        }
    }

    @Test
    public void pollOgg() throws Exception {
        String topic = "ogg_to_kafka";
//        String url = FileUtil.getClassResourcePath(KafkaConsumerUtilTest.class);
//        String schemaStr = FileUtil.readConfFile(url + "oper_history.avsc");
//        String schemaStr = FileUtil.readConfFile(url + "syncos_100000.avsc");
//        logger.info("{}", schemaStr);
//        SchemaUtil.addSchema(topic, schemaStr);
//        Schema schema = SchemaUtil.getSchemaByTopic(topic);
        recordConvertor = new RecordConvertor(topic);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "admin", "admin");
        kafkaConsumerUtil.subscribe(topic);
        int consumerNum = 0;
        TimeCostUtil<Integer> timeCostUtil = new TimeCostUtil<>(consumerNum);
        long limitTime = 3000;
        while (true) {
            List<byte[]> list = kafkaConsumerUtil.poll(2000);
            for (byte[] bytes : list) {
//                logger.info("Record：{}", new String(bytes));
                GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
                int id = (int) genericRecord.get("id");
                byte[] names = genericRecord.get("name").toString().getBytes("UTF-8");
                String name = genericRecord.get("name").toString();
                logger.info("genericRecord：{}，id：{}，name：{}，{}", genericRecord, id, new String(names, "UTF-8"), name);
                consumerNum++;
            }
            //limitTime秒检测一次，检测limitTime秒内没有变化就退出循环
            boolean tag = timeCostUtil.tag(limitTime, consumerNum);
            logger.info("consumerNum：{}，tag：{}", consumerNum, tag);
            if (tag) {
                break;
            }
            if (consumerNum % 1000 == 0) {
                logger.info("{} % 1000 == 0", consumerNum);
                SleepUtils.sleepMilliSecond(200);
            }
        }
    }

}