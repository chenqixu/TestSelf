package com.cqx.utils;

import com.cqx.bean.KafkaTuple;
import org.apache.avro.generic.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KafkaProducerUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtilTest.class);
    private final String path = "D:\\Document\\Workspaces\\Git\\TestSelf\\TestKafka\\src\\test\\resources\\";
    private KafkaProducerUtil<String, byte[]> kafkaProducerUtil;
    private GenericRecordUtil genericRecordUtil;
    private String topic = "nmc_tb_lte_http";
    private String schemaUrl = "http://10.1.8.203:19090/nl-edc-cct-sys-ms-dev/SchemaService/getSchema?t=";
    private String conf = path + "producer.properties";

    @Before
    public void setUp() throws Exception {
//        kafkaProducerUtil = new KafkaProducerUtil<>(conf);
//        kafkaProducerUtil = new KafkaProducerUtil<>(conf, "admin", "admin");
        Map map = new HashMap();
        map.put(KafkaProducerUtil.KAFKA_HEADER + "bootstrap.servers", "edc-mqc-01:9092,edc-mqc-02:9092,edc-mqc-03:9092");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "acks", "0");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "batch.size", "1048576");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "security.protocol", "SASL_PLAINTEXT");
        map.put(KafkaProducerUtil.KAFKA_HEADER + "sasl.mechanism", "PLAIN");
        kafkaProducerUtil = new KafkaProducerUtil<>(map, "admin", "admin");
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
    public void sendMcCdr() {
        topic = "nmc_tb_mc_cdr";
        genericRecordUtil.addTopic(topic);
        for (int i = 0; i < 1; i++) {
            String key = "17859626986";
            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("BTIME", "1");
            valueMap.put("ETIME", "1");
            valueMap.put("GLOBALID", "1");
            valueMap.put("PROTOCOLID", "1");
            valueMap.put("EVENTID", "1");
            valueMap.put("MSCCODE", "1");
            valueMap.put("LAC", "1");
            valueMap.put("CI", "1");
            valueMap.put("OLAC", "1");
            valueMap.put("OCI", "1");
            valueMap.put("DLAC", "1");
            valueMap.put("DCI", "1");
            valueMap.put("FIRSTLAC", "1");
            valueMap.put("FIRSTCI", "1");
            valueMap.put("LASTLAC", "1");
            valueMap.put("LASTCI", "1");
            valueMap.put("CALLINGNUM", "1");
            valueMap.put("CALLEDNUM", "1");
            valueMap.put("CALLINGIMSI", "1");
            valueMap.put("CALLEDIMSI", "1");
            valueMap.put("CALLINGIMEI", "1");
            valueMap.put("CALLEDIMEI", "1");
            valueMap.put("CALLINGTMSI", "1");
            valueMap.put("CALLEDTMSI", "1");
            valueMap.put("EVENTRESULT", "1");
            valueMap.put("ALERTOFFSET", "1");
            valueMap.put("CONNOFFSET", "1");
            valueMap.put("DISCONDIRECT", "1");
            valueMap.put("DISCONNOFFSET", "1");
            valueMap.put("ANSWERDUR", "1");
            valueMap.put("PAGINGRESPTYPE", "1");
            valueMap.put("ALERTSTATUS", "1");
            valueMap.put("CONSTATUS", "1");
            valueMap.put("DISCONNSTATUS", "1");
            valueMap.put("DISCONNCAUSE", "1");
            valueMap.put("RELCAUSE", "1");
            valueMap.put("HOFLAG", "1");
            valueMap.put("Callingnumnature", "1");
            valueMap.put("Callednumnature", "1");
            valueMap.put("CALLING_CITY", "1");
            valueMap.put("CALLING_COUNTY", "1");
            valueMap.put("CALLED_CITY", "1");
            valueMap.put("CALLED_COUNTY", "1");
            valueMap.put("CALL_COUNTY", "1");
            valueMap.put("FIRST_CALL_COUNTY", "1");
            valueMap.put("LAST_CALL_COUNTY", "1");
            valueMap.put("CDRID", "1");
            valueMap.put("SESSIONID", "1");
            valueMap.put("SPCKIND", "1");
//            logger.info("valueMap：{}", valueMap);
            byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
            kafkaProducerUtil.send(topic, key, value);//无回调
//            kafkaProducerUtil.sendCallBack(topic, key, value);//有回调，较久
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
                while (true) {
                    while ((kafkaTuple = kafkaTupleBlockingQueue.poll()) != null) {
                        System.out.println("poll：" + kafkaTuple);
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

    @Test
    public void sendOgg() throws UnsupportedEncodingException {
        topic = "ogg_to_kafka";
//        byte[] value = "test".getBytes();
        genericRecordUtil = new GenericRecordUtil();
        genericRecordUtil.addTopic(topic);
        String key = "17859626986";
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("id", "1");
        valueMap.put("name", new String("你好abc123".getBytes("GBK"), "GBK"));
        valueMap.put("sex", "false");
        byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
        kafkaProducerUtil.send(topic, key, value);

        valueMap = new HashMap<>();
        valueMap.put("id", "1");
        valueMap.put("name", new String("你好abc123".getBytes("UTF-8"), "UTF-8"));
        valueMap.put("sex", "false");
        value = genericRecordUtil.genericRecord(topic, valueMap);
        kafkaProducerUtil.send(topic, key, value);
    }

    @Test
    public void genericRecordTest() throws Exception {
        topic = "ogg_to_kafka";
        genericRecordUtil = new GenericRecordUtil();
        genericRecordUtil.addTopic(topic);
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("id", "1");
        valueMap.put("name", new String("你好abc123".getBytes("GBK"), "GBK"));
//        valueMap.put("sex", "false");
        byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
        logger.info("{}", value);

        RecordConvertor recordConvertor = new RecordConvertor("new_ogg_to_kafka", true);
        GenericRecord genericRecord = recordConvertor.binaryToRecord(value);
        logger.info("{}", genericRecord);
    }
}