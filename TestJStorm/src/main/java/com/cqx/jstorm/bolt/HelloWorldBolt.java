package com.cqx.jstorm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;
import java.util.Vector;

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
	
	private static final Logger logger = LoggerFactory.getLogger(HelloWorldBolt.class);
//	private static Logger logger = Logger.getLogger(HelloWorldBolt.class);
	private int myCount = 0;
	private OutputCollector collector;
	private Vector<String> resultlist = null;

	/*
	 * prepare() => on create
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext,
			OutputCollector outputCollector) {
		logger.info("##############prepare##：{}", this);
		this.collector = outputCollector;
		resultlist = new Vector<String>();
		Runtime.getRuntime().addShutdownHook(
				new Thread("relase-shutdown-hook" + this) {
					@Override
					public void run() {
						// 释放连接池资源
						System.out.println("release：" + this);
					}
				}
		);
	}

	/*
	 * execute() => most important method in the bolt is execute(Tuple input),
	 * which is called once per tuple received the bolt may emit several tuples
	 * for each tuple received
	 */
	@Override
	public void execute(Tuple tuple) {
//		logger.info("##############execute");
//		random(tuple);// 随机
		order(tuple);// 顺序
	}
	
	private void random(Tuple tuple) {
		String test = tuple.getStringByField("sentence");
		if (test == "Hello World") {
			myCount++;
			logger.info("Found a Hello World! My Count is now: "
					+ Integer.toString(myCount));
			this.collector.ack(tuple);
		} else {
			logger.info("not Found!");
			this.collector.fail(tuple);
		}
	}
	
	private void order(Tuple tuple) {
		String test = tuple.getStringByField("sentence");
		resultlist.add(test);
		this.collector.ack(tuple);
//		logger.info("##resultlist##"+resultlist);
	}

	/*
	 * declareOutputFields => This bolt emits nothing hence no body for
	 * declareOutputFields()
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		logger.info("##############declareOutputFields");
	}
	
}
