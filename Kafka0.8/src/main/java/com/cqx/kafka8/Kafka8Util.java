package com.cqx.kafka8;

import com.cqx.common.utils.kafka.GenericRecordUtil;
import com.cqx.common.utils.kafka.KafkaConsumerGRUtil;
import com.cqx.common.utils.kafka.RecordConvertor;
import com.cqx.common.utils.system.SleepUtil;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.*;

/**
 * KafkaConsumer8
 *
 * @author chenqixu
 */
public class Kafka8Util {
    private GenericRecordUtil genericRecordUtil;
    private RecordConvertor recordConvertor = null;
    private Schema schema;

    public void init(String topic, String avsc_file) throws IOException {
        String avscStr = KafkaConsumerGRUtil.avscFromFile(avsc_file);
        genericRecordUtil = new GenericRecordUtil(null);
        // schema从本地文件获取
        genericRecordUtil.addTopicBySchemaString(topic, avscStr);
        schema = genericRecordUtil.getSchema(topic);
        // 记录转换工具类
        recordConvertor = new RecordConvertor(schema);
    }

    public byte[] buildAvsc(String topic, Map<String, String> kafkaValue) throws IOException {
        return genericRecordUtil.genericRecord(topic, kafkaValue);
    }

    public void producer(String topic) throws IOException {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.1.8.201:9195,10.1.8.202:9195");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("serializer.class", "kafka.serializer.StringEncoder");
//        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(properties);
        for (int iCount = 0; iCount < 10; iCount++) {
            Map<String, String> kafkaValue = new HashMap<>();
            SleepUtil.sleepMilliSecond(1);
            kafkaValue.put("btime", System.currentTimeMillis() + "");
            byte[] message = buildAvsc(topic, kafkaValue);
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, message);
            producer.send(record);
        }
        producer.close();
    }

    public void consumer(String topic) {
        Properties props = new Properties();
        props.put("zookeeper.connect", "10.1.8.200:2181,10.1.8.201:2181,10.1.8.202:2181/kafka-0.8");
        props.put("group.id", "test1");
        // "smallest" : "largest"
        props.put("auto.offset.reset", "smallest");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        //设置ConsumerIterator的hasNext的超时时间,不设置则永远阻塞直到有新消息来
        props.put("consumer.timeout.ms", "1000");
//        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY, "range");
        // 序列化类
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("serializer.class", "kafka.serializer.DefaultEncoder");

        // 设置kafka配置
        ConsumerConfig consumerConfig = new kafka.consumer.ConsumerConfig(props);
        // 根据配置创建消费连接器
        ConsumerConnector consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, 1);
        //StringDecoder
        //指定key的编码格式
        Decoder keyDecoder = new kafka.serializer.DefaultDecoder(new VerifiableProperties());
        //指定value的编码格式
        Decoder valueDecoder = new kafka.serializer.DefaultDecoder(new VerifiableProperties());
        //获取topic 和 接受到的stream 集合
        Map<String, List<KafkaStream<String, byte[]>>> map = consumerConnector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        //根据指定的topic 获取 stream 集合
        List<KafkaStream<String, byte[]>> kafkaStreams = map.get(topic);
//        ExecutorService executor = Executors.newFixedThreadPool(1);
        //因为是多个 message组成 message set ， 所以要对stream 进行拆解遍历
        for (final KafkaStream<String, byte[]> kafkaStream : kafkaStreams) {
            // 拆解每个的 stream
            ConsumerIterator<String, byte[]> iterator = kafkaStream.iterator();
            while (iterator.hasNext()) {
                //messageAndMetadata 包括了 message ， topic ， partition等metadata信息
                MessageAndMetadata<String, byte[]> messageAndMetadata = iterator.next();
                long offset = messageAndMetadata.offset();
                int partition = messageAndMetadata.partition();
                byte[] message = messageAndMetadata.message();
                System.out.println(String.format("[partition]=%s, [offset]=%s, [message]=%s, [GenericRecord]=%s"
                        , partition, offset, Arrays.toString(message), getGenericRecord(message)));
            }
        }
    }

    /**
     * 字节数组转换成GenericRecord
     *
     * @param bytes
     * @return
     */
    public GenericRecord getGenericRecord(byte[] bytes) {
        GenericRecord record;
        try {
            record = recordConvertor.binaryToRecord(bytes);
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("GenericRecord转换异常，%s", new String(bytes)), e);
        }
        return record;
    }
}
