package com.cqx.common.utils.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.HashMap;

/**
 * ZkKeeperStates
 *
 * @author chenqixu
 */
public class ZkKeeperStates {

    private static HashMap<Watcher.Event.KeeperState, String> map;

    static {
        map = new HashMap<>();

        map.put(Watcher.Event.KeeperState.AuthFailed, ":auth-failed");
        map.put(Watcher.Event.KeeperState.SyncConnected, ":connected");
        map.put(Watcher.Event.KeeperState.Disconnected, ":disconnected");
        map.put(Watcher.Event.KeeperState.Expired, ":expired");
    }

    public static String getStateName(Watcher.Event.KeeperState state) {
        return map.get(state);
    }

}