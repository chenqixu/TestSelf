package com.cqx.jstorm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 公共Spout
 *
 * @author chenqixu
 */
public class CommonSpout extends BaseRichSpout {

    private static final Logger logger = LoggerFactory.getLogger(CommonSpout.class);
    private ISpout iSpout;

    public CommonSpout(String spout_name) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        this.iSpout = ISpout.generate(spout_name);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.iSpout.setCollector(collector);
        this.iSpout.open(conf, context);
    }

    @Override
    public void nextTuple() {
        this.iSpout.nextTuple();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.iSpout.declareOutputFields(declarer);
    }

    @Override
    public void ack(Object object) {
        this.iSpout.ack(object);
    }

    @Override
    public void fail(Object object) {
        this.iSpout.fail(object);
    }

    @Override
    public void close() {
        this.iSpout.close();
    }
}
