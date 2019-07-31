package com.cqx.jmx.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXClientFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger(JMXClientFactoryTest.class);

    @Test
    public void getJMXClientUtil() {
        String ip = "192.168.230.128";
        int port = 8998;

        IJMXClient OSjmxClientUtil = JMXClientFactory.startJMXClient(
                "java.lang:type=OperatingSystem", ip, port, false);
        Object FreePhysicalMemorySize = OSjmxClientUtil.getAttributeByName("FreePhysicalMemorySize");
        Object SystemCpuLoad = OSjmxClientUtil.getAttributeByName("SystemCpuLoad");
        Object ProcessCpuLoad = OSjmxClientUtil.getAttributeByName("ProcessCpuLoad");
        logger.info("FreePhysicalMemorySize：{}，SystemCpuLoad：{}，ProcessCpuLoad：{}",
                FreePhysicalMemorySize, SystemCpuLoad, ProcessCpuLoad);

        IJMXClient BytesInPerSecjmxClientUtil = JMXClientFactory.startJMXClient(
                "kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec", ip, port, false);
        Object BytesInPerSecCount = BytesInPerSecjmxClientUtil.getAttributeByName("Count");
        logger.info("BytesInPerSecCount：{}", BytesInPerSecCount);

        IJMXClient BytesOutPerSecjmxClientUtil = JMXClientFactory.startJMXClient(
                "kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec", ip, port, false);
        Object BytesOutPerSecCount = BytesOutPerSecjmxClientUtil.getAttributeByName("Count");
        logger.info("BytesOutPerSecCount：{}", BytesOutPerSecCount);

        IJMXClient MessagesInPerSecjmxClientUtil = JMXClientFactory.startJMXClient(
                "kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec", ip, port, false);
        Object MessagesInPerSecCount = MessagesInPerSecjmxClientUtil.getAttributeByName("Count");
        logger.info("MessagesInPerSecCount：{}", MessagesInPerSecCount);
    }
}