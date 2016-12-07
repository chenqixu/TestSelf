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
 * kafka消费者线程
 * */
public class KafkaConsumer extends Thread {
	private final ConsumerConnector consumer;
	private final String topic;

	/**
	 * 初始化，传入话题(topic)
	 * */
	public KafkaConsumer(String topic) {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig());
		this.topic = topic;
	}

	/**
	 * 创建消费者属性
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
		 * 这个方法可以得到一个流的列表，每个流都是MessageAndMetadata的迭代，
		 * 通过MessageAndMetadata可以拿到消息和其他的元数据（目前之后topic）
		 * Input: a map of <topic, #streams>
		 * Output: a map of <topic, list of message streams>
		 */
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		// 获得话题(topic)的消息流
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		// 进行消费
		while (it.hasNext()) {
			System.out.println("receive：" + new String(it.next().message()));
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
