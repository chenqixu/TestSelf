package com.newland.bi.bigdata.zookeeper;

import com.newland.bi.bigdata.utils.string.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZookeeperToolsTest {

    private ZookeeperTools zookeeperTools;
    private String connectionInfo;

    @Before
    public void setUp() throws Exception {
        connectionInfo = "10.1.4.186:2182";
        connectionInfo = "192.168.230.128:2181";
        zookeeperTools = ZookeeperTools.getInstance();
        zookeeperTools.init(connectionInfo);
    }

    @Test
    public void init() throws Exception {
        String path = "/test-1";
        List<String> list = zookeeperTools.listForPath("/");
        StringUtils.printList(list);
        zookeeperTools.createNode(path);
        zookeeperTools.deleteNode(path);
        System.out.println("#########################");
        list = zookeeperTools.listForPath("/");
        StringUtils.printList(list);
        zookeeperTools.close();
    }
}