package com.newland.bi.bigdata.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookerTest {
	// 会话超时时间，设置为与系统默认时间一致
	private static final int SESSION_TIMEOUT = 30000;

	// 创建Zookeeper实例
	private ZooKeeper zk;
//	private CountDownLatch countDownLatch=new CountDownLatch(1);

	// 创建Watcher实例
	Watcher wh = new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println("==========="+event.toString());
//			if(event.getState()==KeeperState.SyncConnected){
//				countDownLatch.countDown();
//			}
		}
	};

	public void createZKInstance() throws IOException {
//		zk = new ZooKeeper("10.1.4.54:2181", ZookerTest.SESSION_TIMEOUT, this.wh);
		zk = new ZooKeeper("10.1.8.1:2181", ZookerTest.SESSION_TIMEOUT, this.wh);		
//		try {
//			countDownLatch.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

	private void ZKOperations() throws Exception, InterruptedException {
//		System.out.println("/n1. 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限：OPEN_ACL_UNSAFE ，节点类型： Persistent");
//		zk.create("/zoo", "myData2".getBytes(), Ids.OPEN_ACL_UNSAFE,
//				CreateMode.PERSISTENT);
//		System.out.println("/n2. 查看是否创建成功： ");
//		System.out.println(new String(zk.getData("/zoo2", false, null)));
//		System.out.println("/n3. 修改节点数据 ");
//		zk.setData("/zoo2", "shenlan211314".getBytes(), -1);
//		System.out.println("/n4. 查看是否修改成功： ");
//		System.out.println(new String(zk.getData("/zoo2", false, null)));
//		System.out.println("/n5. 删除节点 ");
//		zk.delete("/zoo2", -1);
//		System.out.println("/n6. 查看节点是否被删除： ");
		System.out.println(" 节点状态： [" + zk.exists("/hbase", false) + "]");
		String path = "/";
		try{
			List<String> list=this.zk.getChildren(path, false);
			if(list.isEmpty()){
				System.out.println(path+"中没有节点");
			}else{
				System.out.println(path+"中存在节点");
				for(String child:list){
					System.out.println("节点为："+child);
				}
			}
		}catch (KeeperException.NoNodeException e) {
			e.printStackTrace();
		}
	}

	private void ZKClose() throws InterruptedException {
		System.out.println("zk.getSessionId:"+zk.getSessionId());
		System.out.println("zk.getSessionTimeout:"+zk.getSessionTimeout());
		System.out.println("zk.getState:"+zk.getState());
		zk.close();
		System.out.println("zk.close()");
	}

	public static void main(String[] args) throws Exception {
		ZookerTest dm = new ZookerTest();
		dm.createZKInstance();
		dm.ZKOperations();
		dm.ZKClose();
//		String a = "1851.2959999999987";
//		double d = 0 ;
//		long b;
//		d = Double.valueOf(a);
////		b = Math.round(d);
//		b = (long)d;
//		System.out.println(d);
//		System.out.println(b);
	}
}
