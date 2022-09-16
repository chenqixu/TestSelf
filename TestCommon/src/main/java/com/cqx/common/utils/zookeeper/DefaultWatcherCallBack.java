package com.cqx.common.utils.zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

/**
 * DefaultWatcherCallBack
 *
 * @author chenqixu
 */
public class DefaultWatcherCallBack implements WatcherCallBack {

    private static Logger LOG = Logger.getLogger(DefaultWatcherCallBack.class);

    public void execute(Watcher.Event.KeeperState state, Watcher.Event.EventType type, String path) {
        LOG.info("Zookeeper state update:" + ZkKeeperStates.getStateName(state)
                + "," + ZkEventTypes.getStateName(type) + "," + path);
    }

}
