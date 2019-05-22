package com.cqx.utils;

import org.apache.avro.Schema;
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
    private String topic = "nmc_tb_lte_http";
    private String conf = path + "consumer.properties";

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
}