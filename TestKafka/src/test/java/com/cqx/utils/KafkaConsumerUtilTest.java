package com.cqx.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class KafkaConsumerUtilTest {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerUtilTest.class);
    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaConsumerUtil<String, byte[]> kafkaConsumerUtil;
    private RecordConvertor recordConvertor;
    private String topic = "nmc_tb_lte_http_test";
    private String conf = path + "consumer.properties";
    private String schemaUrl = "http://10.1.8.203:18061/SchemaService/getSchema?t=";
    private SchemaUtil schemaUtil;

    @Before
    public void setUp() throws Exception {
        Schema schema = SchemaUtil.getSchemaByTopic(topic);
        recordConvertor = new RecordConvertor(schema);
//        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "admin", "admin");
        kafkaConsumerUtil.subscribe(topic);
    }

    @After
    public void tearDown() throws Exception {
        kafkaConsumerUtil.close();
    }

    @Test
    public void poll() {
        List<byte[]> list = kafkaConsumerUtil.poll(1000);
        for (byte[] bytes : list) {
            GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
            logger.info("genericRecord：{}", genericRecord);
        }
    }

    @Test
    public void pollS1mme() throws Exception {
        String topic = "nmc_tb_lte_s1mme";
        schemaUtil = new SchemaUtil(schemaUrl);
        Schema schema = schemaUtil.getSchemaByUrlTopic(topic);
        recordConvertor = new RecordConvertor(schema);
        kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, "alice", "alice");
        kafkaConsumerUtil.subscribe(topic);
        List<byte[]> list = kafkaConsumerUtil.poll(1000);
        for (byte[] bytes : list) {
            GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
            logger.info("genericRecord：{}", genericRecord);
        }
    }
}