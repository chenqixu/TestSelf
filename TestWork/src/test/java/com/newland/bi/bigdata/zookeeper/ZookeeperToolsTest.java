package com.newland.bi.bigdata.zookeeper;

import com.cqx.common.utils.string.StringUtil;
import com.cqx.common.utils.zookeeper.ZookeeperTools;
import com.cqx.exception.TestSelfException;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ZookeeperToolsTest {

    private ZookeeperTools zookeeperTools;
    private String connectionInfo;

    @Before
    public void setUp() throws Exception {
        connectionInfo = "10.1.4.186:2183";
//        connectionInfo = "192.168.230.128:2181";
        zookeeperTools = ZookeeperTools.getInstance();
        zookeeperTools.init(connectionInfo);
    }

    @After
    public void setAfter() throws TestSelfException {
        if (zookeeperTools != null) zookeeperTools.close();
    }

    @Test
    public void init() throws Exception {
        String path = "/test-1";
        List<String> list = zookeeperTools.listForPath("/");
        StringUtil.printList(list);
//        zookeeperTools.createNode(path);
//        zookeeperTools.deleteNode(path);
//        System.out.println("#########################");
//        list = zookeeperTools.listForPath("/");
//        StringUtil.printList(list);
    }

    @Test
    public void getDistributedAtomicLong() throws Exception {
        DistributedAtomicLong idSeq = zookeeperTools.getDistributedAtomicLong("/test-1");
        AtomicValue<Long> result = idSeq.increment();
        if (result.succeeded()) {
            long seq = result.postValue();
            System.out.println("seq：" + seq);
        } else {
            System.out.println("没有获取到");
        }
    }
}