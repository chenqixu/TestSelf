package com.cqx;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.log4j.Logger;

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
//	private static Logger LOGGER = Logger.getLogger(HelloWorldBolt.class);
	private int myCount = 0;
	private OutputCollector collector;
	private Vector<String> resultlist = null;

	/*
	 * prepare() => on create
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext,
			OutputCollector outputCollector) {
		LOGGER.info("##############prepare");
		this.collector = outputCollector;
		resultlist = new Vector<String>();
	}

	/*
	 * execute() => most important method in the bolt is execute(Tuple input),
	 * which is called once per tuple received the bolt may emit several tuples
	 * for each tuple received
	 */
	@Override
	public void execute(Tuple tuple) {
//		LOGGER.info("##############execute");
//		random(tuple);// 随机
		order(tuple);// 顺序
	}
	
	private void random(Tuple tuple) {
		String test = tuple.getStringByField("sentence");
		if (test == "Hello World") {
			myCount++;
			LOGGER.info("Found a Hello World! My Count is now: "
					+ Integer.toString(myCount));
			this.collector.ack(tuple);
		} else {
			LOGGER.info("not Found!");
			this.collector.fail(tuple);
		}
	}
	
	private void order(Tuple tuple) {
		String test = tuple.getStringByField("sentence");
		resultlist.add(test);
		this.collector.ack(tuple);
//		LOGGER.info("##resultlist##"+resultlist);
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
