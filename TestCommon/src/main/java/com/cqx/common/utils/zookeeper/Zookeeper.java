package com.cqx.common.utils.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Zookeeper
 *
 * @author chenqixu
 */
public class Zookeeper {
    private static Logger LOG = LoggerFactory.getLogger(Zookeeper.class);

    public static Integer getInt(Object o) {
        if (o instanceof Long) {
            return Integer.valueOf(((Long) o).intValue());
        } else if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Short) {
            return Integer.valueOf(((Short) o).intValue());
        } else if (o instanceof String) {
            return Integer.valueOf(Integer.parseInt((String) o));
        } else {
            throw new IllegalArgumentException("Don\'t know how to convert " + o + " to int");
        }
    }

    public CuratorFramework mkClient(List<String> servers, Object port, String root) {
        return mkClient(servers, port, root, new DefaultWatcherCallBack());
    }

    /**
     * connect ZK, register Watch/unhandle Watch
     *
     * @return CuratorFramework
     */
    public CuratorFramework mkClient(List<String> servers, Object port, String root, final WatcherCallBack watcher) {

        // ArrayList<String> serverPorts = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();
        for (String str : servers) {
            builder.append(str + ":" + getInt(port));
            builder.append(",");
        }
        builder.setLength(builder.length() - 1);
        String zkStr1 = builder.toString();
        return mkClient(zkStr1, root, watcher);
    }

    public CuratorFramework mkClient(String servers, String root) {
        return mkClient(servers, root, new DefaultWatcherCallBack());
    }

    public CuratorFramework mkClient(String servers, String root, final WatcherCallBack watcher) {
        CuratorFramework fk;

        String zkStr1 = servers + root;

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(zkStr1).connectionTimeoutMs(10000)
                .sessionTimeoutMs(100000).retryPolicy(new BoundedExponentialBackoffRetry(1000, 3, 1000));
        fk = builder.build();

        fk.getCuratorListenable().addListener(new CuratorListener() {
            public void eventReceived(CuratorFramework _fk, CuratorEvent e) throws Exception {
                if (e.getType().equals(CuratorEventType.WATCHED)) {
                    WatchedEvent event = e.getWatchedEvent();

                    watcher.execute(event.getState(), event.getType(), event.getPath());
                }

            }
        });

        fk.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            public void unhandledError(String msg, Throwable error) {
                String errmsg = "Unrecoverable Zookeeper error: " + msg;
                LOG.error(errmsg, error);
            }
        });
        fk.start();
        return fk;
    }

    public String creatingParentsIfNeeded(CuratorFramework zk, String path, byte[] data, org.apache.zookeeper.CreateMode mode) throws Exception {
        String npath = PathUtils.normalize_path(path);
        return zk.create().creatingParentsIfNeeded().withMode(mode).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(npath, data);
    }

    public String createNode(CuratorFramework zk, String path, byte[] data, org.apache.zookeeper.CreateMode mode) throws Exception {

        String npath = PathUtils.normalize_path(path);

        return zk.create().withMode(mode).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(npath, data);
    }

    public String createNode(CuratorFramework zk, String path, byte[] data) throws Exception {
        return createNode(zk, path, data, org.apache.zookeeper.CreateMode.PERSISTENT);
    }

    public boolean existsNode(CuratorFramework zk, String path, boolean watch) throws Exception {
        Stat stat = null;
        if (watch) {
            stat = zk.checkExists().watched().forPath(PathUtils.normalize_path(path));
        } else {
            stat = zk.checkExists().forPath(PathUtils.normalize_path(path));
        }
        return stat != null;
    }

    public void deleteNode(CuratorFramework zk, String path) throws Exception {
        zk.delete().forPath(PathUtils.normalize_path(path));
    }

    public void mkdirs(CuratorFramework zk, String path) throws Exception {

        String npath = PathUtils.normalize_path(path);

        // the node is "/"
        if (npath.equals("/")) {
            return;
        }

        // the node exist
        if (existsNode(zk, npath, false)) {
            return;
        }

        mkdirs(zk, PathUtils.parent_path(npath));
        try {
            createNode(zk, npath, new byte[] { 7 }, org.apache.zookeeper.CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            // ;// this can happen when multiple clients doing mkdir at same
            // time
            LOG.warn("Exists zookeeper mkdirs for path" + path);

        }

    }

    public byte[] getDataWithOutCheck(CuratorFramework zk, String path) throws Exception {
        String npath = PathUtils.normalize_path(path);
        return zk.getData().forPath(npath);

    }

    public byte[] getData(CuratorFramework zk, String path, boolean watch) throws Exception {
        String npath = PathUtils.normalize_path(path);
        try {
            if (existsNode(zk, npath, watch)) {
                if (watch) {
                    return zk.getData().watched().forPath(npath);
                } else {
                    return zk.getData().forPath(npath);
                }
            }
        } catch (NoNodeException e) {
            return null;
        } catch (KeeperException e) {
            throw e;
        }

        return null;
    }

    public List<String> getChildren(CuratorFramework zk, String path, boolean watch) throws Exception {

        String npath = PathUtils.normalize_path(path);

        if (watch) {
            return zk.getChildren().watched().forPath(npath);
        } else {
            return zk.getChildren().forPath(npath);
        }
    }

    public Stat setData(CuratorFramework zk, String path, byte[] data) throws Exception {
        String npath = PathUtils.normalize_path(path);
        return zk.setData().forPath(npath, data);
    }

    public boolean exists(CuratorFramework zk, String path, boolean watch) throws Exception {
        return existsNode(zk, path, watch);
    }

    public void deletereRcursive(CuratorFramework zk, String path) throws Exception {

        String npath = PathUtils.normalize_path(path);
        if (existsNode(zk, npath, false)) {
            zk.delete().deletingChildrenIfNeeded().forPath(path);
            // List<String> childs = getChildren(zk, npath, false);
            // for (String child : childs) {
            // String childFullPath = PathUtils.full_path(npath, child);
            // deletereRcursive(zk, childFullPath);
            // }
            // deleteNode(zk, npath);
        }
    }
}