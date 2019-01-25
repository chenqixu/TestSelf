package com.newland.bi.bigdata.zookeeper;

import com.cqx.exception.TestSelfErrorCode;
import com.cqx.exception.TestSelfException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * zookeeper工具类
 */
public class ZookeeperTools {

    public static ZookeeperTools zookeeperTools = new ZookeeperTools();
    private CuratorFramework client;
    private RetryPolicy retryPolicy;

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
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
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
        client.close();
    }

    public CuratorFramework getClient() {
        return client;
    }

    public boolean createNode(String path, byte[] data) throws Exception {
        checkClient();
        if (client.checkExists().forPath(path) == null) {
            if (data != null) {
                if (client.create().forPath(path, data) != null) return true;
            } else {
                if (client.create().forPath(path) != null) return true;
            }
        }
        return false;
    }

    public boolean createNode(String path) throws Exception {
        return createNode(path, null);
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
}
