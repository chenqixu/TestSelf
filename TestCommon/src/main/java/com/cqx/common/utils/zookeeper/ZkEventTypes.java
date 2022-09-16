package com.cqx.common.utils.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.HashMap;

/**
 * ZkEventTypes
 *
 * @author chenqixu
 */
public class ZkEventTypes {

    private static HashMap<Watcher.Event.EventType, String> map;

    static {
        map = new HashMap<>();

        map.put(Watcher.Event.EventType.None, ":none");
        map.put(Watcher.Event.EventType.NodeCreated, ":node-created");
        map.put(Watcher.Event.EventType.NodeDeleted, ":node-deleted");
        map.put(Watcher.Event.EventType.NodeDataChanged, ":node-data-changed");
        map.put(Watcher.Event.EventType.NodeChildrenChanged,
                ":node-children-changed");
    }

    public static String getStateName(Watcher.Event.EventType type) {
        return map.get(type);
    }

}

