package com.cqx.utils;

import com.cqx.bean.KafkaTuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KafkaProducerUtilTest {

    private static Logger logger = LoggerFactory.getLogger(KafkaProducerUtilTest.class);
    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaProducerUtil<String, byte[]> kafkaProducerUtil;
    private GenericRecordUtil genericRecordUtil;
    private String topic = "nmc_tb_lte_http";
    private String schemaUrl = "http://10.1.8.203:18061/SchemaService/getSchema?t=";
    private String conf = path + "producer.properties";

    @Before
    public void setUp() throws Exception {
//        kafkaProducerUtil = new KafkaProducerUtil<>(conf);
        kafkaProducerUtil = new KafkaProducerUtil<>(conf, "admin", "admin");
        genericRecordUtil = new GenericRecordUtil(schemaUrl);
    }

    @After
    public void tearDown() throws Exception {
        if (kafkaProducerUtil != null) kafkaProducerUtil.release();
    }

    @Test
    public void sendHttp() {
        topic = "nmc_tb_lte_http";
        genericRecordUtil.addTopic(topic);
        for (int i = 0; i < 1; i++) {
            String key = "13500000000";
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("city_1", "1");
            valueMap.put("imsi", "1");
            valueMap.put("imei", "1");
            valueMap.put("msisdn", "13500000000");
            valueMap.put("tac", "1");
            valueMap.put("eci", "1");
            valueMap.put("rat", "1");
            valueMap.put("procedure_start_time", "1");
            valueMap.put("app_class", "1");
            valueMap.put("host", "1");
            valueMap.put("uri", "1");
            valueMap.put("apply_classify", "1");
            valueMap.put("apply_name", "1");
            valueMap.put("web_classify", "1");
            valueMap.put("web_name", "1");
            valueMap.put("search_keyword", "1");
            valueMap.put("procedure_end_time", "1");
            valueMap.put("upbytes", "1");
            valueMap.put("downbytes", "1");
//            logger.info("valueMap：{}", valueMap);
            byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
            kafkaProducerUtil.send(topic, key, value);
            logger.info("send，topic：{}，key：{}，value：{}", topic, key, value);
//            logger.info("send {}", i);
        }
    }

    @Test
    public void sendS1mme() {
        topic = "nmc_tb_lte_s1mme";
        genericRecordUtil.addTopic(topic);
        for (int i = 0; i < 1; i++) {
            String key = "17859626986";
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("city", "");
            valueMap.put("xdr_id", "130680baf9cde200");
            valueMap.put("imsi", "460075596011657");
            valueMap.put("imei", "865253039943989");
            valueMap.put("msisdn", "17859626986");
            valueMap.put("procedure_type", "4");
            valueMap.put("subprocedure_type", "");
            valueMap.put("procedure_start_time", "20190603110035");
            valueMap.put("procedure_delay_time", "322");
            valueMap.put("procedure_end_time", "20190603110035");
            valueMap.put("procedure_status", "0");
            valueMap.put("old_mme_group_id", "450");
            valueMap.put("old_mme_code", "236");
            valueMap.put("lac", "");
            valueMap.put("tac", "22819");
            valueMap.put("cell_id", "57668779");
            valueMap.put("other_tac", "");
            valueMap.put("other_eci", "");
            valueMap.put("home_code", "");
            valueMap.put("msisdn_home_code", "");
            valueMap.put("old_mme_group_id_1", "");
            valueMap.put("old_mme_code_1", "");
            valueMap.put("old_m_tmsi", "");
            valueMap.put("old_tac", "5923");
            valueMap.put("old_eci", "");
            valueMap.put("cause", "");
            valueMap.put("keyword", "1");
            valueMap.put("mme_ue_s1ap_id", "2417444237");
            valueMap.put("request_cause", "");
            valueMap.put("keyword_2", "1");
            valueMap.put("keyword_3", "");
            valueMap.put("keyword_4", "");
//            logger.info("valueMap：{}", valueMap);
            byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
            kafkaProducerUtil.send(topic, key, value);
            logger.info("send，topic：{}，key：{}，value：{}", topic, key, value);
        }
    }

    @Test
    public void mapTest() {
        Map<String, Object> map = new HashMap<>();
        String a = (String) map.get("test");
        System.out.println(a);
    }

    @Test
    public void listTest() throws InterruptedException {
        List<KafkaTuple> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++)
            values.add(new KafkaTuple());
        List<KafkaTuple> tmpKafkaTuples = new ArrayList<>();
        tmpKafkaTuples.addAll(values);
        values.clear();
        System.out.println(tmpKafkaTuples);
        final BlockingQueue<KafkaTuple> kafkaTupleBlockingQueue = new LinkedBlockingQueue<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                KafkaTuple kafkaTuple;
                while (true){
                    while ((kafkaTuple = kafkaTupleBlockingQueue.poll()) != null) {
                        System.out.println("poll："+kafkaTuple);
                    }
                    SleepUtils.sleepMilliSecond(50);
                }
            }
        }).start();
        SleepUtils.sleepMilliSecond(200);
        for (KafkaTuple kafkaTuple1 : tmpKafkaTuples)
            kafkaTupleBlockingQueue.put(kafkaTuple1);
        SleepUtils.sleepMilliSecond(200);
        for (KafkaTuple kafkaTuple1 : tmpKafkaTuples)
            kafkaTupleBlockingQueue.put(kafkaTuple1);
        SleepUtils.sleepMilliSecond(5000);
    }
}