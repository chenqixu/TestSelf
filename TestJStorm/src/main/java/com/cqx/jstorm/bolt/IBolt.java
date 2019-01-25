package com.cqx.jstorm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import com.cqx.jstorm.util.AppConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * 公共Bolt接口
 *
 * @author chenqixu
 */
public abstract class IBolt implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(IBolt.class);
    protected OutputCollector collector;

    public static IBolt generate(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class cls = Class.forName(AppConst.BOLT_IMPL_PACKAGE + name);
        return (IBolt) cls.newInstance();
    }

    public void setCollector(OutputCollector collector) {
        this.collector = collector;
    }

    protected abstract void prepare(Map stormConf, TopologyContext context);

    protected abstract void execute(Tuple input);

    protected void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    protected void cleanup() {
    }
}
