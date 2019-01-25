package com.cqx.jstorm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import com.cqx.jstorm.util.AppConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * 公共Spout接口
 *
 * @author chenqixu
 */
public abstract class ISpout implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(ISpout.class);
    protected SpoutOutputCollector collector;

    public static ISpout generate(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class cls = Class.forName(AppConst.SPOUT_IMPL_PACKAGE + name);
        return (ISpout) cls.newInstance();
    }

    public void setCollector(SpoutOutputCollector collector) {
        this.collector = collector;
    }

    protected abstract void open(Map conf, TopologyContext context);

    protected abstract void nextTuple();

    protected void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(AppConst.FIELDS));
    }

    protected void ack(Object object) {
    }

    protected void fail(Object object) {
    }

    protected void close() {
    }
}
