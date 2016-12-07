package com.cqx.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * kafka�������߳�
 * */
public class KafkaConsumer extends Thread {
	private final ConsumerConnector consumer;
	private final String topic;

	/**
	 * ��ʼ�������뻰��(topic)
	 * */
	public KafkaConsumer(String topic) {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	/**
	 * ��������������
	 * */
	private static ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", KafkaProperties.zkConnect);
		props.put("group.id", KafkaProperties.groupId);
		props.put("zookeeper.session.timeout.ms", "40000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	@Override
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		/**
		 * createMessageStreams
		 * ����������Եõ�һ�������б�ÿ��������MessageAndMetadata�ĵ�����
		 * ͨ��MessageAndMetadata�����õ���Ϣ��������Ԫ���ݣ�Ŀǰ֮��topic��
		 * Input: a map of <topic, #streams>
		 * Output: a map of <topic, list of message streams>
		 */
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		// ��û���(topic)����Ϣ��
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		// ��������
		while (it.hasNext()) {
			System.out.println("receive��" + new String(it.next().message()));
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
