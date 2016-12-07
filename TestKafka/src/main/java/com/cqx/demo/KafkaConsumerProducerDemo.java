package com.cqx.demo;

import com.cqx.kafka.KafkaConsumer;
import com.cqx.kafka.KafkaProducer;
import com.cqx.kafka.KafkaProperties;

/**
 * kafka��������
 * */
public class KafkaConsumerProducerDemo {
	public static void main(String[] args) {
		// ������
		KafkaProducer producerThread = new KafkaProducer(KafkaProperties.topic);
		producerThread.start();

		// ������
		KafkaConsumer consumerThread = new KafkaConsumer(KafkaProperties.topic);
		consumerThread.start();
	}
}
