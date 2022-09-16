package com.cqx.common.utils.zookeeper;

import com.cqx.common.utils.string.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZookeeperToolsTest {

    private ZookeeperTools zookeeperTools;
    private String connectionInfo;

    @Before
    public void setUp() throws Exception {
        connectionInfo = "10.1.12.79:24002,10.1.12.78:24002,10.1.12.75:24002";
        zookeeperTools = ZookeeperTools.getInstance();
        zookeeperTools.init(connectionInfo);
    }

    @After
    public void tearDown() throws Exception {
        if (zookeeperTools != null) zookeeperTools.close();
    }

    @Test
    public void init() throws Exception {
        List<String> list = zookeeperTools.listForPath("/");
        StringUtil.printList(list);
    }
}