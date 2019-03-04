package com.cqx.write;

import java.util.ArrayList;
import java.util.List;

/**
 * RebuilderWatcher
 *
 * @author chenqixu
 */
public class RebuilderWatcher {
    private List<IWatcher> watcherList = new ArrayList<>();

    private RebuilderWatcher() {
    }

    public static RebuilderWatcher builder() {
        return new RebuilderWatcher();
    }

    public void addWatcher(IWatcher watcher) {
        watcherList.add(watcher);
    }

    public void commit() {
        for (IWatcher watcher : watcherList) {
            watcher.changeRebuilder();
        }
    }

    public List<IWatcher> getWatcherList() {
        return watcherList;
    }
}
