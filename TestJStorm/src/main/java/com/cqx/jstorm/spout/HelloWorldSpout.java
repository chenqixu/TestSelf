package com.cqx.jstorm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.Vector;
//import org.apache.log4j.Logger;

//import storm.kafka.BrokerHosts;
//import storm.kafka.KafkaSpout;
//import storm.kafka.SpoutConfig;
//import storm.kafka.ZkHosts;

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

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldSpout.class);
    private static final int MAX_RANDOM = 10;
    //	private static Logger LOGGER = Logger.getLogger(HelloWorldSpout.class);
    private SpoutOutputCollector collector;
    private int referenceRandom;
    private long tupleId;
    private long succeedCount;
    private long failedCount;
    private Vector<String> orderacklist;

//	private HelloWorldStatus helloWorldStatus;

    public HelloWorldSpout() {
        final Random rand = new Random();
        referenceRandom = rand.nextInt(MAX_RANDOM);
        logger.info("##############create HelloWorldSpout##：{}", this);

//		helloWorldStatus = new HelloWorldStatus();
//		JMXFactory.startJMX("HelloWorldStatus", helloWorldStatus, 10999);
//		LOGGER.info("##############startJMX");
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
        logger.info("##############declareOutputFields sentence");
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
        orderacklist = new Vector<String>();
        logger.info("##############open collector");
        logger.info("##############telnumber：{}", conf.get("telnumber"));
    }

    /*
     * nextTuple() => Storm cluster will repeatedly call the nextTuple method
     * which will do all the work of the spout. nextTuple() must release the
     * control of the thread when there is no work to do so that the other
     * methods have a chance to be called.
     */
    @Override
    public void nextTuple() {
//		LOGGER.info("##############nextTuple");
//		collector.emit(random(), this.tupleId);// 随机
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Values values = order();
//		helloWorldStatus.add((String)values.get(0));
        collector.emit(values, "[emit.tupleId]" + this.tupleId);// 顺序
        this.tupleId += 1L;
        logger.info("##############hear##############");
//		// 发送心跳到服务器
//		Heart.hear(this.toString());
    }

    /**
     * 随机
     */
    private Values random() {
        final Random rand = new Random();
        int instanceRandom = rand.nextInt(MAX_RANDOM);
        if (instanceRandom == referenceRandom) {
            logger.info("##############emit Hello World [msgid]" + this.tupleId);
            return new Values("Hello World");
        } else {
            logger.info("##############emit Other Random Word [msgid]" + this.tupleId);
            return new Values("Other Random Word");
        }
    }

    /**
     * 顺序
     */
    private Values order() {
        return new Values("[order]" + this.tupleId);
    }

    @Override
    public void ack(Object id) {
//		this.succeedCount += 1L;
//		Long succeedId = (Long)id;
//		LOGGER.info("Succeed to handle " + succeedId);
        String ackstr = "" + id;
        int ackid = Integer.valueOf(ackstr.split("\\[emit.tupleId\\]")[1]);
        if (ackid % 5 == 0) {
            logger.info("ackid{}，sleep(500)", ackid);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        orderacklist.add(ackstr);
        logger.info("##orderacklist##" + orderacklist);
    }

    @Override
    public void fail(Object id) {
        this.failedCount += 1L;
        Long failId = (Long) id;
        logger.info("Failed to handle " + failId);
    }
}
