package com.cqx.common.utils.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.PromotedToLock;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * zookeeper工具类
 */
public class ZookeeperTools {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperTools.class);
    private static volatile ZookeeperTools zookeeperTools = new ZookeeperTools();
    private CuratorFramework client;
    private RetryPolicy retryPolicy;
//    private List<PersistentEphemeralNode> persistentEphemeralNodeList = new ArrayList<>();

    private ZookeeperTools() {
    }

    public static ZookeeperTools getInstance() {
        if (zookeeperTools == null) {
            synchronized (ZookeeperTools.class) {
                if (zookeeperTools == null) {
                    zookeeperTools = new ZookeeperTools();
                }
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

    public void checkClient() throws NullPointerException {
        if (!isStarted()) throw new NullPointerException("客户端没有连接！");
    }

    public boolean isStarted() {
        return client != null && (client.getState() == CuratorFrameworkState.STARTED);
    }

    public void close() throws NullPointerException {
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
        throw new NullPointerException(path + "路径不存在！");
    }

    public boolean exists(String path) throws Exception {
        checkClient();
        return client.checkExists().forPath(path) != null;
    }

    /**
     * 获取分布式序列号
     *
     * @param path
     * @return
     */
    public DistributedAtomicLong getDistributedAtomicLong(String path) {
        RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(1000, 3, 1000);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        PromotedToLock.Builder builder = PromotedToLock.builder().lockPath(path + "/lock").retryPolicy(retryPolicy);
        return new DistributedAtomicLong(client, path, retryPolicy, builder.build());
    }
}
