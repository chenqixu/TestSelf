package com.cqx.common.utils.zookeeper;

import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * WatcherCallBack
 *
 * @author chenqixu
 */
public interface WatcherCallBack {
    public void execute(KeeperState state, EventType type, String path);
}
