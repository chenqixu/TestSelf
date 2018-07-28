package com.cqx.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;

public interface INodeWatchCallback {
	void nodeAddAction(ChildData childData);
	void nodeUpdateAction(ChildData childData);
	void nodeRemoveAction(ChildData childData);
}
