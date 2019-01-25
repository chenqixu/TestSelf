package com.cqx.jstorm.spout.impl;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Values;
import com.cqx.jstorm.spout.ISpout;
import com.cqx.jstorm.util.Utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提交测试
 *
 * @author chenqixu
 */
public class EmitTestSpout extends ISpout {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    protected void open(Map conf, TopologyContext context) {
        logger.info("####open");
    }

    @Override
    protected void nextTuple() {
        while (atomicInteger.getAndIncrement() < 1) {
            for (int i = 0; i < 1000; i++) {
                this.collector.emit(new Values(this.toString() + "####" + i));
                logger.info("####emit：{}", i);
            }
            Utils.sleep(500);
        }
    }
}
