package com.cqx.jstorm.base;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.apache.log4j.Logger;

import java.io.File;

import com.cqx.jstorm.bolt.HelloWorldBolt;
import com.cqx.jstorm.spout.HelloWorldSpout;
import edu.emory.mathcs.backport.java.util.Arrays;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Desc: setup the topology and submit it to either a local of remote Storm
 * cluster depending on the arguments passed to the main method.
 * */
public class HelloWorldTopology {
//	private static final Logger logger = LoggerFactory.getLogger(HelloWorldTopology.class);
//	private static Logger logger = Logger.getLogger(HelloWorldTopology.class);
	/*
	 * main class in which to define the topology and a LocalCluster object
	 * (enables you to test and debug the topology locally). In conjunction with
	 * the Config object, LocalCluster allows you to try out different cluster
	 * configurations.
	 * 
	 * Create a topology using 'TopologyBuilder' (which will tell storm how the
	 * nodes area arranged and how they exchange data) The spout and the bolts
	 * are connected using 'ShuffleGroupings'
	 * 
	 * Create a 'Config' object containing the topology configuration, which is
	 * merged with the cluster configuration at runtime and sent to all nodes
	 * with the prepare method
	 * 
	 * Create and run the topology using 'createTopology' and 'submitTopology'
	 */
	public static void main(String[] args) throws AlreadyAliveException,
			InvalidTopologyException, AuthorizationException {
		String Topology_name = "helloworld";
		
		// 创建topology的生成器
		TopologyBuilder builder = new TopologyBuilder();
		// 创建Spout，其中new HelloWorldSpout() 为真正spout对象
		// randomHelloWorld 为spout的名字，注意名字中不要含有空格
		// spout的并发设置，这里设置为1
		SpoutDeclarer spout = builder.setSpout("randomHelloWorld", new HelloWorldSpout(), 1);
		// 创建bolt，HelloWorldBolt为bolt名字
		// HelloWorldBolt 为bolt对象
		// 1为bolt并发数
		// shuffleGrouping（SequenceTopologyDef.SEQUENCE_SPOUT_NAME），
		// 表示接收SequenceTopologyDef.SEQUENCE_SPOUT_NAME的数据，并且以shuffle方式，
		// 即每个spout随机轮询发送tuple到下一级bolt中
		BoltDeclarer totalBolt =builder.setBolt("HelloWorldBolt", new HelloWorldBolt(), 10)
				.shuffleGrouping("randomHelloWorld");
		Config conf = new Config();
		// 允许debug
		conf.setDebug(true);
		// 表示整个topology将使用几个worker
		conf.setNumWorkers(1);
		// 设置ack为1
		conf.setNumAckers(1);
		
		// 远程提交集群模式
		if ( args != null && args.length == 6 ) {
			Topology_name = args[0];
			String NIMBUS_HOST = args[1];
			String NIMBUS_THRIFT_PORT = args[2];
			String[] STORM_ZOOKEEPER_SERVERS = new String[]{args[3]};
			String STORM_ZOOKEEPER_PORT = args[4];
			String STORM_ZOOKEEPER_ROOT = args[5];
			// nimbus地址
			conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
			// nimbus thrift端口
			conf.put(Config.NIMBUS_THRIFT_PORT, Integer.valueOf(NIMBUS_THRIFT_PORT));
			// zookeeper地址
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(STORM_ZOOKEEPER_SERVERS));
			// zookeeper端口
			conf.put(Config.STORM_ZOOKEEPER_PORT, STORM_ZOOKEEPER_PORT);
			// zookeeper上jstorm路径
			conf.put(Config.STORM_ZOOKEEPER_ROOT, STORM_ZOOKEEPER_ROOT);
			// 提交topology
			StormSubmitter.submitTopology(Topology_name, conf, builder.createTopology(), null,
					Arrays.asList(new File[]{new File("D:\\Document\\Workspaces\\Git\\TestSelf\\TestJStorm\\target\\TestJStorm-1.0.0.jar")}));
		}
		// 本地提交集群模式
		else if ( args != null && args.length == 3 ) {
			Topology_name = args[0];
			String NIMBUS_HOST = args[1];
			String NIMBUS_THRIFT_PORT = args[2];
			// nimbus地址
			conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
			// nimbus thrift端口
			conf.put(Config.NIMBUS_THRIFT_PORT, Integer.valueOf(NIMBUS_THRIFT_PORT));
			// 提交topology
			StormSubmitter.submitTopology(Topology_name, conf, builder.createTopology());
			}
		// 本地模式
		else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(Topology_name, conf, builder.createTopology());
			Utils.sleep(5000);
			cluster.shutdown();
		}
	}
}
