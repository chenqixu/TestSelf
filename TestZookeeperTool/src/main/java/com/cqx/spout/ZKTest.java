package com.cqx.spout;

import java.util.HashMap;
import java.util.Map;

import com.cqx.zookeeper.CuratorTools;
import com.cqx.zookeeper.SerializationUtils;
import com.cqx.zookeeper.ZkInfo;

public class ZKTest {
	private CuratorTools curatorTools = null;
	
	public ZKTest(String zkservers){
		curatorTools = new CuratorTools(new ZkInfo(zkservers), 1, 500);
	}
	
	public void list(String path){
		try {
			curatorTools.list(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void create(String path){
		try {
			curatorTools.create(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setData(String path, Object obj){
		try {
			curatorTools.setData(path, SerializationUtils.serialize(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(String path){
		try {
			curatorTools.delete(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			curatorTools.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ZKTest zt = new ZKTest("192.168.230.128:2181");
//		zt.list("/");
//		zt.create("/zktest");
//		zt.list("/");
		zt.delete("/zktest/1");
//		zt.delete("/zktest/2");
		zt.create("/zktest/1");
//		TestBean tb = new TestBean();
//		tb.setId("123");
//		tb.setName("test");
//		Map<String, String> tb = new HashMap<String, String>();
//		tb.put("id", "123");
//		tb.put("name", "test");
//		zt.setData("/zktest/1", tb);
		zt.close();
	}
}
