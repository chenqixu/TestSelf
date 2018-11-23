package com.newland.bi.bigdata.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.alibaba.fastjson.JSON;
import com.newland.bi.bigdata.bean.FtpHostInfo;
import com.newland.bi.bigdata.thread.WriterBean;

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

	public void ZKOperations() throws Exception, InterruptedException {
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
		System.out.println(" 节点状态/hbase： [" + zk.exists("/hbase", false) + "]");
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
	
	public void ZKList(String path) throws Exception, InterruptedException {
		try {
			List<String> list=this.zk.getChildren(path, false);
			if(list.isEmpty()){
				System.out.println(path+"中没有节点");
			}else{
				System.out.println(path+"中存在节点");
				for(String child:list){
					System.out.println("节点为："+child);
				}
			}
		} catch (KeeperException.NoNodeException e) {
			e.printStackTrace();
		}
	}
	
	public String ZKgetData(String path) {
		String data = null;
		try {
			data = new String(this.zk.getData(path, false, null));
			System.out.println("路径："+path+" 数据："+data);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void ZKsetData(String path, byte[] data) {
		try {
			this.zk.setData(path, data, -1);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void ZKdel(String path) {
		try {
			this.zk.delete(path, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * path =节点路径 
	 * data=要存放的资料 
	 * acl = 权限控制 {OPEN_ACL_UNSAFE，公开，无需权限模式；CREATOR_ALL_ACL，创建者们拥有全部的权限，auth需要我们在new的时候自行设置对应的模式；READ_ACL_UNSAFE，只读模式}
	 * CreateMode = 创建模式
	 * @param path
	 */
	public void ZKcreate(String path) {
		try {
			this.zk.create(path, new byte[]{}, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void ZKClose() throws InterruptedException {
		System.out.println("zk.getSessionId:"+zk.getSessionId());
		System.out.println("zk.getSessionTimeout:"+zk.getSessionTimeout());
		System.out.println("zk.getState:"+zk.getState());
		zk.close();
		System.out.println("zk.close()");
	}

	public static void main(String[] args) throws Exception {
		String data = null;
		ZookerTest dm = new ZookerTest();
		dm.createZKInstance();
//		dm.ZKOperations();
//		dm.ZKcreate("/computecenter/taskcontext/100833124645/src/10.1.8.81/scanner");
//		dm.ZKList("/computecenter/taskcontext/100833124645/src/10.1.8.81");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node536/send-hdfs-file_v2_2");
//		data = dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node536/send-hdfs-file_v2_2/tmpdata");
		data = dm.ZKgetData("/computecenter/taskcontext/103659171521/src/10.1.8.78");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node536/send-hdfs-file_v2_1/tmpdata");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node536/send-hdfs-file_v2_2/tmpdata");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/src/10.1.8.78/info");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/src/10.1.8.81/info");
		
		List<WriterBean> tmplist = JSON.parseArray(data, WriterBean.class);
		System.out.println(data);
		
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node536/send-hdfs-file_v2_2");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node7143/send-hdfs-file_v2_0");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node7143/send-hdfs-file_v2_1");
//		dm.ZKgetData("/computecenter/taskcontext/103659171521/step/node7143/send-hdfs-file_v2_2");
//		dm.ZKList("/computecenter/taskcontext/103659171521/step/node2084");
//		dm.ZKList("/computecenter/taskcontext/103093739236/step/node2084");
//		dm.ZKList("/computecenter/taskcontext/103093739236/step/node9503");
//		dm.ZKList("/computecenter/taskcontext/102956747802/step");
//		dm.ZKgetData("/computecenter/taskcontext/102956747802/step/node7143/send-hdfs-file_v2_0");
//		dm.ZKgetData("/computecenter/taskcontext/102956747802/step/node2084/stream-ftp-input2_v2_0");
//		dm.ZKgetData("/computecenter/taskcontext/102956747802/src/10.1.8.3/info");
//		data = dm.ZKgetData("/computecenter/taskcontext/102956747802/step/node7143/send-hdfs-file_v2_0");
//		JSON.parseArray(data, WriterBean.class);
//		dm.ZKsetData("/computecenter/taskcontext/102956747802/step/node7143/send-hdfs-file_v2_0", "".getBytes());
//		dm.ZKList("/computecenter/taskcontext/10000/step/node0002");
//		dm.ZKdel("/computecenter/taskcontext/10000/step/node0002/test_component_1");
//		dm.ZKgetData("/computecenter/taskcontext/10000/step/node0002/test_component_0");
//		dm.ZKgetData("/computecenter/taskcontext/10000/step/node0002/test_component_1");
//		dm.ZKcreate("/computecenter/taskcontext/10000/step/node0002/test_component_1");
//		dm.ZKsetData("/computecenter/taskcontext/10000/step/node0002/test_component_0", "".getBytes());
		
//		WriterBean wb = new WriterBean("/usr/test/data/rc_hw/x2/2018010100/data1/x2_data1_2018010100_0_000111", "2018010100");
//		List<WriterBean> tmplist = new ArrayList<WriterBean>();
//		tmplist.add(wb);
//		dm.ZKsetData("/computecenter/taskcontext/10000/step/node0002/test_component_1", JSON.toJSONString(tmplist).getBytes());		

//		FtpHostInfo ftpHostInfo = new FtpHostInfo();
//		ftpHostInfo.setCurrentFirstFileTime("2018020100");
//		dm.ZKList("/computecenter/taskcontext/10000");
//		dm.ZKcreate("/computecenter/taskcontext/10000/src");
//		dm.ZKcreate("/computecenter/taskcontext/10000/src/10.1.8.3");
//		dm.ZKcreate("/computecenter/taskcontext/10000/src/10.1.8.3/info");
//		dm.ZKsetData("/computecenter/taskcontext/102956747802/src/10.1.8.3/info", JSON.toJSONString(ftpHostInfo).getBytes());
		
//		dm.ZKList("/computecenter/taskcontext/10000/src/10.1.8.3/info");
//		dm.ZKList("/computecenter/taskcontext/10000/src/10.1.8.3/scanner");
//		dm.ZKgetData("/computecenter/taskcontext/10000/src/10.1.8.3/info");
//		dm.ZKgetData("/computecenter/taskcontext/10000/src/10.1.8.3/scanner");
//		dm.ZKdel("/computecenter/taskcontext/10000/step/node0002/test_component_0");
//		dm.ZKList("/computecenter/taskcontext/10000/step/node0002");
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
