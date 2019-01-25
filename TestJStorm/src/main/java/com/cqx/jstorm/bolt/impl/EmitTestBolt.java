package com.cqx.jstorm.bolt.impl;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.cqx.jstorm.bolt.IBolt;
import com.cqx.jstorm.util.AppConst;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提交测试
 *
 * @author chenqixu
 */
public class EmitTestBolt extends IBolt {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void prepare(Map stormConf, TopologyContext context) {
        logger.info("####prepare");
    }

    @Override
    protected void execute(Tuple input) {
        logger.info("####{} to execute，input：{}，count：{}", this, input.getStringByField(AppConst.FIELDS), count.incrementAndGet());
    }

    protected void cleanup() {
        logger.info("####{} to cleanup，count：{}",
                this, count.get());
    }
}
