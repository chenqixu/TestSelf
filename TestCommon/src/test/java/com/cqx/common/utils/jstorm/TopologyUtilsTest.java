package com.cqx.common.utils.jstorm;

import org.apache.thrift.TException;
import org.junit.Test;

public class TopologyUtilsTest {

    @Test
    public void getTopologies() throws TException {
        try (TopologyUtils topologyUtils = new TopologyUtils("d:\\tmp\\etc\\jstorm\\storm.yaml")) {
            System.out.println(topologyUtils.getTopologies());
        }
    }
}