package com.cqx.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * zookeeper tool
 * */
public class CuratorTools {
    private static final Logger logger = LoggerFactory.getLogger(CuratorTools.class);
    private static final String lineSparator = System.getProperty("line.separator");
    private static final Charset charset = Charset.forName("utf-8");
    private ZkInfo zkInfo;
    private CuratorFramework zkClient;

    public CuratorTools(final ZkInfo zkInfo, int retriesCount, int sleepMsBetweenRetries) {
        // TODO 还要参数化
        zkClient = CuratorFrameworkFactory
                .builder()
                .connectString(zkInfo.getZkServer())
                .retryPolicy(new RetryNTimes(retriesCount, sleepMsBetweenRetries))
                .build();
        zkClient.start();

        // curator2.5.0 is not support blockUntilConnected
//		try {
//			zkClient.blockUntilConnected();
//		} catch (InterruptedException e) {
//			// logger.error(e.getMessage(), e);
//			logger.error("★★★ error info = {}", e);
//		}

//		zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
//			@Override
//			public void stateChanged(CuratorFramework client, ConnectionState newState) {
//				// newState
//				if (ConnectionState.CONNECTED == newState) {
//					logger.info("☆☆☆ zk connected success!");
//				} else if (ConnectionState.RECONNECTED == newState) {
//					logger.info("☆☆☆ zk reconnected success!");
//				}
//			}
//		});
    }

    public CuratorTools(final ZkInfo zkInfo) {
        this(zkInfo, 2000, 20000);
    }

    /**
     * 创建节点
     *
     * @throws Exception
     */
    public void create(String path) throws Exception {
        zkClient.create() // 创建一个路径
                .creatingParentsIfNeeded() // 如果指定的节点的父节点不存在，递归创建父节点
                .withMode(CreateMode.PERSISTENT) // 存储类型（临时的还是持久的）
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) // 访问权限
                .forPath(path, null); // 创建的路径
        logger.info("Create node '{}' successfully.", path);
    }

    public void create(String path, byte[] data) throws Exception {
        zkClient.create() // 创建一个路径
                .creatingParentsIfNeeded() // 如果指定的节点的父节点不存在，递归创建父节点
                .withMode(CreateMode.PERSISTENT) // 存储类型（临时的还是持久的）
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) // 访问权限
                .forPath(path, data); // 创建的路径
        logger.info("Create node '{}' successfully.", path);
    }

    public void create(String path, String data) throws Exception {
        this.create(path, data.getBytes());
    }

    public void create(String path, String data, final Charset charset) throws Exception {
        this.create(path, data.getBytes(charset));
    }

    /**
     * 创建并替换节点
     *
     * @param path 路径
     * @throws Exception
     */
    public void createOrReplace(String path) throws Exception {
        Stat stat = zkClient.checkExists().forPath(path);
        if (null != stat) {
            zkClient.delete().forPath(path);
        }
        this.create(path);
    }

    /**
     * 创建节点, 节点存在时不抛出异常.
     *
     * @param path 创建的路径.
     */
    public void createNotExist(String path) {
        try {
            Stat stat = zkClient.checkExists().forPath(path);
            if (null == stat) {
                this.create(path);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 判断节点是否存在.
     *
     * @throws Exception
     */
    public boolean isExist(String path) throws Exception {
        Stat stat = zkClient.checkExists().forPath(path);
        return stat != null ? true : false;
    }

    /**
     * 获取节点的值.
     *
     * @throws Exception
     */
    public String getData(String path) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            byte[] bytes = zkClient.getData().forPath(path);
            if (bytes == null) {
                return "";
            } else {
                return new String(bytes, charset);
            }
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return "";
        }
    }

    public byte[] getByteData(String path) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            byte[] bytes = zkClient.getData().forPath(path);
            return bytes;
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return null;
        }
    }

    /**
     * 给节点赋值.
     *
     * @param path 路径
     * @param data 字符串的数据
     * @return
     * @throws Exception
     */
    public boolean setData(String path, String data) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            zkClient.setData().forPath(path, data.getBytes(charset));
            return true;
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return false;
        }
    }

    /**
     * 给节点赋值.
     *
     * @param path  路径
     * @param bytes 字节数组的数据
     * @return
     * @throws Exception
     */
    public boolean setData(String path, byte[] bytes) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            zkClient.setData().forPath(path, bytes);
            return true;
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return false;
        }
    }

    /**
     * 设置node的值, 没有node节点时, 先创建
     *
     * @param path  路径
     * @param bytes 字节数组
     * @throws Exception
     */
    public void setDataForce(String path, byte[] bytes) throws Exception {
        boolean isExist = this.isExist(path);
        if (!isExist) {
            this.create(path);
        }
        zkClient.setData().forPath(path, bytes);
    }

    /**
     * 设置node的值, 没有node节点时, 先创建
     *
     * @param path 路径
     * @param data 字符串
     * @throws Exception
     */
    public void setDataForce(String path, String data) throws Exception {
        boolean isExist = this.isExist(path);
        if (!isExist) {
            this.create(path);
        }
        zkClient.setData().forPath(path, data.getBytes(charset));
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @throws Exception
     */
    public boolean delete(String path, boolean isGuaranted) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            if (isGuaranted) {
                zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            } else {
                zkClient.delete().deletingChildrenIfNeeded().forPath(path);
            }
            return true;
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return false;
        }
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @throws Exception
     */
    public boolean delete(String path) throws Exception {
        return this.delete(path, false);
    }

    /**
     * 删除节点的子节点
     *
     * @param path 路径
     * @throws Exception
     */
    public boolean deleteChildren(String path) throws Exception {
        boolean isExist = this.isExist(path);

        if (isExist) {
            ZKPaths.deleteChildren(zkClient.getZookeeperClient().getZooKeeper(), path, false);
            return true;
        } else {
            logger.error("Znode '{}' is not exist!", path);
            return false;
        }
    }

    public List<String> getChildren(String path) throws Exception {
        List<String> lstNodes = zkClient.getChildren().forPath(path);
        return lstNodes;
    }

    /**
     * 列表节点下的节点
     *
     * @throws Exception
     */
    public void list(String path) throws Exception {
        boolean isExist = this.isExist(path);
        if (isExist) {
            List<String> lstNodes = zkClient.getChildren().forPath(path);

            StringBuilder sb = new StringBuilder(100);
            sb.append("Path '").append(path).append("' Children Znode Info below: ").append(lineSparator);

            for (String strNode : lstNodes) {
                sb.append("\t[").append(strNode).append("]").append(lineSparator);
            }
            logger.info(sb.toString());
        } else {
            logger.error("Znode '{}' is not exist!", path);
        }
    }

    /**
     * 打印ZNode信息
     *
     * @param path 路径
     * @throws Exception 异常
     */
    public void printStat(String path) throws Exception {
        Stat stat = zkClient.checkExists().forPath(path);
        StringBuilder sb = new StringBuilder(100);

        sb.append("ZNode Stat Info below: ").append(path).append(lineSparator);
        // 创建该ZNode的zxid
        sb.append("\t").append("czxid=0x").append(Long.toHexString(stat.getCzxid())).append(lineSparator);
        // 最近一次修改该ZNode的zxid
        sb.append("\t").append("mzxid=0x").append(Long.toHexString(stat.getMzxid())).append(lineSparator);
        // 最新修改的pzxid
        sb.append("\t").append("pzxid=0x").append(Long.toHexString(stat.getPzxid())).append(lineSparator);
        // 该ZNode的创建时间，从epoch起的毫秒数
        sb.append("\t").append("ctime=").append(DateUtilsEx.formatUTC(stat.getCtime())).append(lineSparator);
        // 最近一次修改该ZNode的时间，从epoch起的毫秒数
        sb.append("\t").append("mtime=").append(DateUtilsEx.formatUTC(stat.getMtime())).append(lineSparator);
        // 该ZNode的数据的修改次数
        sb.append("\t").append("version=").append(stat.getVersion()).append(lineSparator);
        // 该ZNode的子节点的修改次数
        sb.append("\t").append("cversion=").append(stat.getCversion()).append(lineSparator);
        // 该ZNode的ACL的修改次数
        sb.append("\t").append("aversion=").append(stat.getAversion()).append(lineSparator);
        // 该ZNode的数据长度
        sb.append("\t").append("dataLength=").append(stat.getDataLength()).append(lineSparator);
        // 该ZNode的子节点个数
        sb.append("\t").append("numChildren=").append(stat.getNumChildren()).append(lineSparator);
        // 如果该ZNode是暂态节点，则这个值为拥有者的会话id。否则，这个值为0
        sb.append("\t").append("ephemeralOwner=").append(stat.getEphemeralOwner()).append(lineSparator);
        sb.trimToSize();
        logger.info(sb.toString());
    }

    /**
     * 关闭客户端连接
     *
     * @throws Exception
     */
    public void close() throws Exception {
        CloseableUtils.closeQuietly(zkClient);
    }

    /**
     * 开启事务
     *
     * @return Curator的事务实例
     */
    public CuratorTransaction startTransaction() {
        CuratorTransaction curatorTransaction = zkClient.inTransaction();
        return curatorTransaction;
    }

    /**
     * 提交事务
     *
     * @param transaction 业务
     * @throws Exception
     */
    public void commitTransaction(CuratorTransactionFinal transaction) throws Exception {
        transaction.commit();
    }

    /**
     * 在事务中创建节点
     *
     * @param transaction 事件实例
     * @param path        路径
     * @return
     * @throws Exception
     */
    public void CreateInTransaction(CuratorTransaction transaction, String path, byte[] data) throws Exception {
        transaction.create()
                .withMode(CreateMode.PERSISTENT) // 存储类型（临时的还是持久的）
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) // 访问权限
                .forPath(path, data); // 创建的路径
    }

    /**
     * 设置zk节点的值
     *
     * @param transaction 事件实例
     * @param path        路径
     * @param data        数据
     * @return
     * @throws Exception
     */
    public void SetDataInTransaction(CuratorTransaction transaction, String path, byte[] data) throws Exception {
        transaction.setData().forPath(path, data);
    }

    /**
     * 删除zk节点
     *
     * @param transaction 事件实例
     * @param path        路径
     * @return
     * @throws Exception
     */
    public void deleteInTransaction(CuratorTransaction transaction, String path) throws Exception {
        transaction.delete().forPath(path);
    }

    /**
     * 侦听节点
     *
     * @param path              路径
     * @param nodeWatchCallback 节点侦听接口
     * @throws Exception
     */
    public void watchNode(String path, final INodeWatchCallback nodeWatchCallback) throws Exception {
        // 设置节点的cache
        final NodeCache nodeCache = new NodeCache(zkClient, path, false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                ChildData childData = nodeCache.getCurrentData();

                if (childData != null) {
                    Stat stat = childData.getStat();
                    // check add or update by ctime and mtime
                    if (stat.getCtime() == stat.getMtime()) {
                        nodeWatchCallback.nodeAddAction(childData);
                    } else {
                        nodeWatchCallback.nodeUpdateAction(childData);
                    }
                } else {
                    nodeWatchCallback.nodeRemoveAction(childData);
                }
            }
        });
        nodeCache.start(true);
    }

    /**
     * 侦听子节点<br>
     * 注意: 启动时, 所有子节点都会遍历一次.
     *
     * @param path              路径
     * @param pathWatchCallback 子节点侦听接口
     * @throws Exception
     */
    public void watchPath(String path, final IChildWatchCallback pathWatchCallback) throws Exception {
        // 设置节点的cache
        PathChildrenCache childrenCache = new PathChildrenCache(zkClient, path, true);
        // 设置监听器和处理过程
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                ChildData childData = event.getData();

                if (childData != null) {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            pathWatchCallback.childNodeAddAction(childData);
                            break;
                        case CHILD_REMOVED:
                            pathWatchCallback.childNodeRemoveAction(childData);
                            break;
                        case CHILD_UPDATED:
                            pathWatchCallback.childNodeUpdateAction(childData);
                            break;
                        default:
                            logger.info("data is other type: {}", event.getType());
                            break;
                    }
                } else {
                    logger.error("data is null: {}", event.getType());
                }
            }
        };

        // 开始侦听
        childrenCache.getListenable().addListener(childrenCacheListener);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    /**
     * 侦听节点下的所有子节点<br>
     * 注意: 启动时, 所有子节点都会遍历一次.
     *
     * @param path              路径
     * @param treeWatchCallback 子节点侦听接口
     * @throws Exception 异常
     */
    public void watchTree(String path, final IChildWatchCallback treeWatchCallback) throws Exception {
        // 设置节点的cache
        TreeCache treeCache = new TreeCache(zkClient, path);
        // 设置监听器和处理过程
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData childData = event.getData();
                if (childData != null) {
                    switch (event.getType()) {
                        case NODE_ADDED:
                            treeWatchCallback.childNodeAddAction(childData);
                            break;
                        case NODE_REMOVED:
                            treeWatchCallback.childNodeRemoveAction(childData);
                            break;
                        case NODE_UPDATED:
                            treeWatchCallback.childNodeUpdateAction(childData);
                            break;
                        default:
                            logger.info("data is other type: {}", event.getType());
                            break;
                    }
                } else {
                    logger.debug("data is null: {}", event.getType());
                }
            }
        };

        // 开始侦听
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }
}
