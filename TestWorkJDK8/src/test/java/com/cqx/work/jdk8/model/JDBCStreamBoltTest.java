package com.cqx.work.jdk8.model;

import com.cqx.common.model.stream.StreamSend;
import com.cqx.common.utils.config.YamlParser;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class JDBCStreamBoltTest {
    private StreamSend<BoltBean> streamSend;
    private BoltBeanProducer boltBeanProducer;
    private LinkedBlockingQueue<BoltBean> data;

    @Before
    public void setUp() throws Exception {
        data = new LinkedBlockingQueue<>(100);
        Map params = YamlParser.builder().parserConfToMap("jdbc.yaml");

        //发送者
        streamSend = new StreamSend<>(data);
        streamSend.add(new JDBCStreamBolt("62-cluster"));
        streamSend.add(new JDBCStreamBolt("83-cluster"));
        streamSend.init(params);
        streamSend.start();

        //生产者
        boltBeanProducer = new BoltBeanProducer(data);
        boltBeanProducer.start();
    }

    @After
    public void tearDown() throws Exception {
        if (boltBeanProducer != null) boltBeanProducer.stop();
        if (streamSend != null) streamSend.stop();
    }

    @Test
    public void execute() {
        SleepUtil.sleepSecond(10);
    }
}