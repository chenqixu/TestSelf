package com.cqx.jstorm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 公共bolt
 *
 * @author chenqixu
 */
public class CommonBolt extends BaseRichBolt {

    private static final Logger logger = LoggerFactory.getLogger(CommonBolt.class);
    private IBolt iBolt;

    public CommonBolt(String bolt_name) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.iBolt = IBolt.generate(bolt_name);
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.iBolt.setCollector(collector);
        this.iBolt.prepare(stormConf, context);
    }

    @Override
    public void execute(Tuple input) {
        this.iBolt.execute(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.iBolt.declareOutputFields(declarer);
    }

    @Override
    public void cleanup() {
        this.iBolt.cleanup();
    }
}
