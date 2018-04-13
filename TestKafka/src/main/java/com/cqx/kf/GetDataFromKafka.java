package com.cqx.kf;

/**
 *������Ϣ����
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class GetDataFromKafka implements Runnable {

	private String topic;

	public GetDataFromKafka(String topic) {
		this.topic = topic;
	}

	public static void main(String[] args) {
		String topic_str = "TASK_TOPIC";
		GetDataFromKafka gdkast = new GetDataFromKafka(topic_str);
		new Thread(gdkast).start();
	}

	@Override
	public void run() {
		System.out.println("start runing consumer");

		Properties properties = new Properties();
		properties.put("bootstrap.servers", "10.1.4.185:9092,10.1.4.186:9092");
		// zookeeper ����
		properties.put("zookeeper.connect", "10.1.4.186:2182/udap/kafka");// ����zk
		// ������������
		properties.put("group.id", "udap");// ����Ҫʹ�ñ�������ƣ� //
												// ��������ߺ������߶���ͬһ�飬���ܷ���ͬһ���ڵ�topic����
		// zk���ӳ�ʱ
		properties.put("zookeeper.session.timeout.ms", "10000");
		properties.put("zookeeper.sync.time.ms", "2000");
		properties.put("auto.commit.interval.ms", "2000");
		properties.put("compression.type", "none");
		properties.put("auto.offset.reset", "smallest");//largest/smallest
//		// ���л��� 
//		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "com.newland.bd.utils.jms.kafka.serializer.ObjectDeserializer4ProtoBuf");
		ConsumerConnector consumer = Consumer
				.createJavaConsumerConnector(new ConsumerConfig(properties));

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1)); // һ�δ������л�ȡһ������
		Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer
				.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// ��ȡÿ�ν��յ����������
		ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
		while (iterator.hasNext()) {
			String message = new String(iterator.next().message());
			// hostName+";"+ip+";"+commandName+";"+res+";"+System.currentTimeMillis();
			// ����ָ��ע�⣬���û�������������ִ�к��п��ܻش�ͷ������Ϣ��
			consumer.commitOffsets();
			System.out.println(message);
		}
	}
}
