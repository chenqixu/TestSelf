package com.cqx;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

/**
 * 数据来源spout<br>
 * Desc: spout essentially emits a stream containing 1 of 2 sentences 'Other
 * Random Word' or 'Hello World' based on random probability. It works by
 * generating a random number upon construction and then generating subsequent
 * random numbers to test against the original member variable's value. When it
 * matches "Hello World" is emitted, during the remaining executions the other
 * sentence is emitted.
 */
public class HelloWorldSpout extends BaseRichSpout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldSpout.class);
	private SpoutOutputCollector collector;
	private int referenceRandom;
	private static final int MAX_RANDOM = 10;

	public HelloWorldSpout() {
		final Random rand = new Random();
		referenceRandom = rand.nextInt(MAX_RANDOM);
		LOGGER.info("##############create HelloWorldSpout");
		
//		String zks = "";
//		String topic =  "";
//		String brokerZkPath = "";
//		String id = "";
//		BrokerHosts brokerHosts = new ZkHosts(zks);
//		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, brokerZkPath, id);
////		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
////		spoutConf.forceFromStart = false;
//		KafkaSpout kafkaSpout = new KafkaSpout(spoutConf);
	}

	/*
	 * declareOutputFields() => you need to tell the Storm cluster which fields
	 * this Spout emits within the declareOutputFields method.
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sentence"));
		LOGGER.info("##############declareOutputFields sentence");
	}

	/*
	 * open() => The first method called in any spout is 'open' TopologyContext
	 * => contains all our topology data SpoutOutputCollector => enables us to
	 * emit the data that will be processed by the bolts conf => created in the
	 * topology definition
	 */
	@Override
	public void open(Map conf, TopologyContext topologyContext,
			SpoutOutputCollector collector) {
		this.collector = collector;
		LOGGER.info("##############open collector");
	}

	/*
	 * nextTuple() => Storm cluster will repeatedly call the nextTuple method
	 * which will do all the work of the spout. nextTuple() must release the
	 * control of the thread when there is no work to do so that the other
	 * methods have a chance to be called.
	 */
	@Override
	public void nextTuple() {
		LOGGER.info("##############nextTuple");
		final Random rand = new Random();
		int instanceRandom = rand.nextInt(MAX_RANDOM);
		if (instanceRandom == referenceRandom) {
			LOGGER.info("##############emit Hello World");
			collector.emit(new Values("Hello World"));
		} else {
			LOGGER.info("##############emit Other Random Word");
			collector.emit(new Values("Other Random Word"));
		}
	}
}
