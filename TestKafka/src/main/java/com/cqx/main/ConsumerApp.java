package com.cqx.main;

import com.cqx.utils.KafkaConsumerUtil;
import com.cqx.utils.PropertyUtil;
import com.cqx.utils.RecordConvertor;
import com.cqx.utils.SchemaUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ConsumerApp
 *
 * @author chenqixu
 */
public class ConsumerApp {
    private static Logger logger = LoggerFactory.getLogger(ConsumerApp.class);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            logger.info("args not enough.");
            System.exit(-1);
        }
        String path = args[0];
        PropertyUtil propertyUtil = new PropertyUtil(path);
        String topic = propertyUtil.getProperty("topic");
        String schemaUrl = propertyUtil.getProperty("schemaUrl");
        String conf = propertyUtil.getProperty("conf");
        String user = propertyUtil.getProperty("user");
        String pass = propertyUtil.getProperty("pass");
        logger.info("topic：{}，schemaUrl：{}，conf：{}，user：{}，pass：{}", topic, schemaUrl, conf, user, pass);
        new ConsumerApp().poll(topic, schemaUrl, conf, user, pass);
    }

    private void poll(String topic, String schemaUrl, String conf, String user, String pass) throws Exception {
        KafkaConsumerUtil<String, byte[]> kafkaConsumerUtil = new KafkaConsumerUtil<>(conf, user, pass);
        try {
//            SchemaUtil schemaUtil = new SchemaUtil(schemaUrl);
//            Schema schema = schemaUtil.getSchemaByUrlTopic(topic);
//            RecordConvertor recordConvertor = new RecordConvertor(schema);
            kafkaConsumerUtil.subscribe(topic);
            //只消费一次
            List<byte[]> list = kafkaConsumerUtil.poll(2000);
            for (byte[] bytes : list) {
                logger.info("Record：{}", new String(bytes));
//                GenericRecord genericRecord = recordConvertor.binaryToRecord(bytes);
//                logger.info("genericRecord：{}", genericRecord);
            }
        } finally {
            kafkaConsumerUtil.close();
        }
    }
}
