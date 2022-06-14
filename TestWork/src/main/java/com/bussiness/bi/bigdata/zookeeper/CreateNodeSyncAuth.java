package com.bussiness.bi.bigdata.zookeeper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

public class CreateNodeSyncAuth implements Watcher {
	private static ZooKeeper zookeeper;
	private static boolean somethingDone = false;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		zookeeper = new ZooKeeper("10.1.8.1:2181", 5000,
				new CreateNodeSyncAuth());
		System.out.println(zookeeper.getState());
		zookeeper.close();

		// 让mainThread无限的睡眠
		// 让子进程来控制程序的结束
		Thread.sleep(Integer.MAX_VALUE);
	}

	/*
	 * 权限模式(scheme): ip, digest 授权对象(ID)ip权限模式:具体的ip地址digest权限模式:
	 * username:Base64(SHA-1(username:password)) 权限(permission): create(C),
	 * DELETE(D),READ(R), WRITE(W), ADMIN(A)注：单个权限，完全权限，复合权限
	 * 
	 * 权限组合: scheme + ID + permission
	 */

	private void doSomething() {
		try {
			// 创建节点的时候要求权限验证
			// 基于IP
			ACL aclIp = new ACL(Perms.READ, new Id("ip", "10.1.8.1"));
			// 基于用户名密码
			ACL aclDigest = new ACL(Perms.READ | Perms.WRITE, new Id("digest",
					DigestAuthenticationProvider.generateDigest("jike:123456")));
			ArrayList<ACL> acls = new ArrayList<ACL>();
			acls.add(aclDigest);
			acls.add(aclIp);
			// zookeeper.addAuthInfo("digest", "jike:123456".getBytes());
			String path = zookeeper.create("/node_10", "123".getBytes(), acls,
					CreateMode.PERSISTENT);
			System.out.println("return path:" + path);

			somethingDone = true;

		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("收到事件：" + event);
		if (event.getState() == KeeperState.SyncConnected) {
			if (!somethingDone && event.getType() == EventType.None
					&& null == event.getPath()) {
				doSomething();
			}
		}
	}
}
