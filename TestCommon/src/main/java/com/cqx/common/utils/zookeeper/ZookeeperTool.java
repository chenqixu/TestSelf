package com.cqx.common.utils.zookeeper;

import com.cqx.common.utils.Utils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.PromotedToLock;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * zk操作工具 。整个应用只会使用一个zookeeper地址服务，因此将zk连接作为应用级配置，只进行一次初始化。
 * 如果将来出现应用有业务需要连接多个不同地址的zookeeper集群时，需要对这个类进行改造
 */
public class ZookeeperTool {

    private static ZookeeperTool zookeeperTool;// 单例缓存
    private static Logger log = LoggerFactory.getLogger(ZookeeperTool.class);
    private CuratorFramework zk; // zk是线程安全的，作为应用级
    private Zookeeper zookeeper;
    private String connectString;

    private ZookeeperTool(String connectString) {
        // 不启用安全认证
        System.setProperty("zookeeper.sasl.client", "false");
        this.initZooKeeper(connectString, "", null);
    }

    /**
     * 获取实例，需要传入连接地址
     *
     * @return
     */
    public static ZookeeperTool getInstance() {
        int cnt = 10;
        if (zookeeperTool == null) {
            while (cnt > 0) {
                Utils.sleep(1000);
                cnt--;
                if (zookeeperTool != null) break;
            }
            if (zookeeperTool == null)
                throw new RuntimeException("ZookeeperTool 没有进行初始化设置");
        }
        return zookeeperTool;
    }

    /**
     * 构造一个另外的ZK连接。
     *
     * @param connectString
     * @return
     */
    public static ZookeeperTool getNewInstance(String connectString, String root, WatcherCallBack watcher) {
        ZookeeperTool zookeeperTool = null;
        if (connectString != null) {
            if (ZookeeperTool.zookeeperTool == null) {
                zookeeperTool = new ZookeeperTool(connectString);
            } else if (!connectString.equals(ZookeeperTool.zookeeperTool.connectString)) {
                zookeeperTool = new ZookeeperTool(connectString);
            } else // 新传入的这个地址与jvm唯一地址一致
            {
                zookeeperTool = ZookeeperTool.zookeeperTool;
            }
        } else {
            throw new RuntimeException("ZookeeperTool 连接地址connectString不能为空:" + connectString);
        }
        return zookeeperTool;
    }

    public static void init(String connectString, String root, WatcherCallBack watcher) {
        if (connectString != null) {
            if (zookeeperTool == null) {
                zookeeperTool = new ZookeeperTool(connectString);
            } else if (!connectString.equals(zookeeperTool.connectString)) {
                zookeeperTool = new ZookeeperTool(connectString);
            }
        } else {
            throw new RuntimeException("ZookeeperTool 连接地址connectString不能为空:" + connectString);
        }
    }

    public String getConnectString() {
        return connectString;
    }

    /**
     * 初始化ZooKeeper
     *
     * @throws Exception
     */
    private void initZooKeeper(String connectString, String root, WatcherCallBack watcher) {

        if (zk != null) {
            zk.close();
            zookeeper = null;
        }
        zookeeper = new Zookeeper();
        this.connectString = connectString;
        if (watcher == null) {
            this.zk = zookeeper.mkClient(connectString, root);
        } else {
            this.zk = zookeeper.mkClient(connectString, root, watcher);
        }

    }

    /**
     * 创建节点。不自动创建父节点。 如果节点存在会抛出NOExistException
     *
     * @param path
     * @param data
     * @param mode
     * @return
     * @throws Exception
     */
    public String createNodeWithOutBuildParent(String path, byte[] data, CreateMode mode)
            throws Exception {
        return zookeeper.createNode(zk, path, data, mode);

    }

    public String createNode(String path, byte[] data, CreateMode mode) throws Exception {
        int i = 0;
        return zookeeper.creatingParentsIfNeeded(zk, path, data, mode);

    }

    /**
     * 创建临时节点
     *
     * @param path
     * @param value
     * @return
     * @throws Exception
     */
    public String createTmpData(String path, String value) throws Exception {

        return createTmpData(path, value.getBytes());
    }

    /**
     * 创建临时节点
     *
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String createTmpData(String path, byte[] data) throws Exception {
        String result = null;

        result = zookeeper.creatingParentsIfNeeded(zk, path, data, CreateMode.EPHEMERAL);

        return result;
    }

    public String createNode(String path, byte[] data) throws Exception {
        String result = "";

        result = zk.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);

        return result;
    }

    /**
     * 创建并替换临时节点
     *
     * @param path 路径
     * @throws Exception
     */
    public void createOrReplaceTmpData(String path, String value) throws Exception {
        Stat stat = zk.checkExists().forPath(path);
        if (null != stat) {
            zk.delete().forPath(path);
        }
        this.createTmpData(path, value);
    }

    public String createNodeOrUpdate(String path, byte[] data) throws Exception {
        String result = "";

        try {
            if (exists(path)) {
                setData(path, data);
            } else {
                result = this.zookeeper.creatingParentsIfNeeded(zk, path, data, CreateMode.PERSISTENT);
            }
        } catch (KeeperException.NodeExistsException e) { // 如果节点已经存在，可能是由于并发导致的。此时为了防止冲突，等待后重做次逻辑
            Thread.sleep(10);
            setData(path, data);
        } catch (Exception e) {
            e.printStackTrace();
            // 判断是否由于根节点
            throw new Exception("znode create failed![" + path + "]", e);
        }
        return result;
    }

    public boolean existsNode(String path, boolean watch) throws Exception {

        return zookeeper.existsNode(zk, path, watch);
    }

    public void deleteNode(String path) throws Exception {
        zookeeper.deleteNode(zk, path);

    }

    public void mkdirs(String path) throws Exception {

        zookeeper.mkdirs(zk, path);

    }

    public byte[] getData(String path, boolean watch) throws Exception {

        return zookeeper.getData(zk, path, watch);
    }

    public byte[] getDataWithOutCheck(String path) throws Exception {

        return zookeeper.getDataWithOutCheck(zk, path);
    }

    public List<String> getChildren(String path, boolean watch) throws Exception {

        return zookeeper.getChildren(zk, path, watch);

    }

    public Stat setData(String path, byte[] data) throws Exception {

        return zookeeper.setData(zk, path, data);
    }

    public boolean exists(String path, boolean watch) throws Exception {
        return zookeeper.exists(zk, path, watch);
    }

    public boolean exists(String path) throws Exception {
        return zookeeper.exists(zk, path, false);
    }

    public void deletereRcursive(String path) throws Exception {
        zookeeper.deletereRcursive(zk, path);
    }

    public void close() {
        zk.close();
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
        return new DistributedAtomicLong(zk, path, retryPolicy, builder.build());
    }
}
