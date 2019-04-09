package com.newland.bi.bigdata.zookeeper;

import com.cqx.exception.TestSelfErrorCode;
import com.cqx.exception.TestSelfException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * zookeeper工具类
 */
public class ZookeeperTools {

    public static ZookeeperTools zookeeperTools = new ZookeeperTools();
    private static Logger logger = LoggerFactory.getLogger(ZookeeperTools.class);
    private CuratorFramework client;
    private RetryPolicy retryPolicy;
//    private List<PersistentEphemeralNode> persistentEphemeralNodeList = new ArrayList<>();

    private ZookeeperTools() {
    }

    public static ZookeeperTools getInstance() {
        synchronized (zookeeperTools) {
            if (zookeeperTools == null)
                synchronized (zookeeperTools) {
                    zookeeperTools = new ZookeeperTools();
                }
        }
        return zookeeperTools;
    }

    public void init(String connectionInfo) {
        //策略：重试3次，间隔1秒
        retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //创建客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionInfo)
                .sessionTimeoutMs(5000)//会话超时
                .connectionTimeoutMs(5000)//连接超时
                .retryPolicy(retryPolicy)//重试策略
                .build();
        //启动客户端
        client.start();
    }

    public void checkClient() throws TestSelfException {
        if (!isStarted()) throw new TestSelfException(TestSelfErrorCode.ZK_CLIENT_NULL, "客户端没有连接！");
    }

    public boolean isStarted() {
        return client == null ? false : (client.getState() == CuratorFrameworkState.STARTED);
    }

    public void close() throws TestSelfException {
        checkClient();
//        if (persistentEphemeralNodeList != null && persistentEphemeralNodeList.size() > 0)
//            for (PersistentEphemeralNode persistentEphemeralNode : persistentEphemeralNodeList)
//                CloseableUtils.closeQuietly(persistentEphemeralNode);
//        CloseableUtils.closeQuietly(client);
//        or
        client.close();
    }

    public CuratorFramework getClient() {
        return client;
    }

    public boolean createNode(String path, byte[] data) throws Exception {
        checkClient();
        if (client.checkExists().forPath(path) == null) {
            if (data != null) {
                if (client.create().creatingParentsIfNeeded().forPath(path, data) != null) return true;
            } else {
                if (client.create().creatingParentsIfNeeded().forPath(path) != null) return true;
            }
        }
        return false;
    }

    public boolean createNode(String path) throws Exception {
        return createNode(path, null);
    }

    public boolean createPersistentEphemeralNode(String path, byte[] data) throws Exception {
        checkClient();
//        PersistentEphemeralNode persistentEphemeralNode = new PersistentEphemeralNode(client,
//                PersistentEphemeralNode.Mode.EPHEMERAL, path, data);
//        persistentEphemeralNode.start();
//        persistentEphemeralNode.waitForInitialCreate(3, TimeUnit.SECONDS);
//        String actualPath = persistentEphemeralNode.getActualPath();
////        logger.info("actualPath：{}，getNodeInfo：{}", actualPath, getNodeInfo(actualPath));
//        persistentEphemeralNodeList.add(persistentEphemeralNode);
//        return actualPath != null ? true : false;
        if (client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, data) != null)
            return true;
        return false;
    }

    public boolean createPersistentEphemeralNode(String path) throws Exception {
        return createPersistentEphemeralNode(path, "".getBytes());
    }

    public List<String> listForPath(String path) throws Exception {
        checkClient();
        return client.getChildren().forPath(path);
    }

    public void deleteNode(String path) throws Exception {
        checkClient();
        if (client.checkExists().forPath(path) != null) {
            client.delete().forPath(path);
        }
    }

    public byte[] getNodeInfo(String path) throws Exception {
        checkClient();
        if (client.checkExists().forPath(path) != null) {
            return client.getData().forPath(path);
        }
        throw new TestSelfException(TestSelfErrorCode.ZK_NOT_EXIST_PATH, path + "路径不存在！");
    }

    public boolean exists(String path) throws Exception {
        checkClient();
        return client.checkExists().forPath(path) != null ? true : false;
    }
}
