package com.cqx.demo;

import com.cqx.kafka.KafkaConsumer;
import com.cqx.kafka.KafkaProducer;
import com.cqx.kafka.KafkaProperties;

/**
 * kafka测试用例
 * */
public class KafkaConsumerProducerDemo {
	public static void main(String[] args) {
		// 生产者
		KafkaProducer producerThread = new KafkaProducer(KafkaProperties.topic);
		producerThread.start();

		// 消费者
		KafkaConsumer consumerThread = new KafkaConsumer(KafkaProperties.topic);
		consumerThread.start();
	}
}
