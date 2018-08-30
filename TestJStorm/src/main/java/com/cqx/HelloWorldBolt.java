package com.cqx;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据处理单元bolt<br>
 * Desc: This bolt will consume the produced Tuples from HelloWorldSpout and
 * implement the required counting logic
 * */
public class HelloWorldBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldBolt.class);
	private int myCount = 0;

	/*
	 * prepare() => on create
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext,
			OutputCollector outputCollector) {
		LOGGER.info("##############prepare");
	}

	/*
	 * execute() => most important method in the bolt is execute(Tuple input),
	 * which is called once per tuple received the bolt may emit several tuples
	 * for each tuple received
	 */
	@Override
	public void execute(Tuple tuple) {
		LOGGER.info("##############execute");
		String test = tuple.getStringByField("sentence");
		if (test == "Hello World") {
			myCount++;
			LOGGER.info("Found a Hello World! My Count is now: "
					+ Integer.toString(myCount));
		} else {
			LOGGER.info("not Found!");
		}
	}

	/*
	 * declareOutputFields => This bolt emits nothing hence no body for
	 * declareOutputFields()
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		LOGGER.info("##############declareOutputFields");
	}
}
