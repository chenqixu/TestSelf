package com.cqx.main;

import com.cqx.utils.GenericRecordUtil;
import com.cqx.utils.KafkaProducerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoadKafka
 *
 * @author chenqixu
 */
public class LoadKafka {

    private static Logger logger = LoggerFactory.getLogger(LoadKafka.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("args not enough.");
            System.exit(-1);
        }
        String path = args[0];
        System.out.println("path：" + path);
        KafkaProducerUtil<String, byte[]> kafkaProducerUtil;
        GenericRecordUtil genericRecordUtil;
        String topic = args[1];
        System.out.println("topic：" + topic);
        String schemaUrl = "http://10.48.137.217:8080/SchemaService/getSchema?t=";
        String conf = path + "producer.properties";

        kafkaProducerUtil = new KafkaProducerUtil<>(conf, "newland", "Bi-Newland");
        genericRecordUtil = new GenericRecordUtil(schemaUrl);
        genericRecordUtil.addTopic(topic);

//        for (int i = 0; i < 5; i++) {
//            String key = "13500000000";
//            Map<String, String> valueMap = new HashMap<>();
//            valueMap.put("city_1", "1");
//            valueMap.put("imsi", "1");
//            valueMap.put("imei", "1");
//            valueMap.put("msisdn", "13500000000");
//            valueMap.put("tac", "1");
//            valueMap.put("eci", "1");
//            valueMap.put("rat", "1");
//            valueMap.put("procedure_start_time", "1");
//            valueMap.put("app_class", "1");
//            valueMap.put("host", "1");
//            valueMap.put("uri", "1");
//            valueMap.put("apply_classify", "1");
//            valueMap.put("apply_name", "1");
//            valueMap.put("web_classify", "1");
//            valueMap.put("web_name", "1");
//            valueMap.put("search_keyword", "1");
//            valueMap.put("procedure_end_time", "1");
//            valueMap.put("upbytes", "1");
//            valueMap.put("downbytes", "1");
//            logger.info("valueMap：{}", valueMap);
//            byte[] value = genericRecordUtil.genericRecord(topic, valueMap);
//            kafkaProducerUtil.send(topic, key, value);
//            logger.info("send，topic：{}，key：{}，value：{}", topic, key, value);
//        }

        kafkaProducerUtil.release();
    }

    public void loadTest() {
        // 读取文件
        // 解析文件
        // 入kafka压力测试
    }
}
