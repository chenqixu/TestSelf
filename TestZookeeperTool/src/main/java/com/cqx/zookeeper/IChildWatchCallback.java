package com.cqx.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;

public interface IChildWatchCallback {
	void childNodeAddAction(ChildData childData);
	void childNodeRemoveAction(ChildData childData);
	void childNodeUpdateAction(ChildData childData);
}
