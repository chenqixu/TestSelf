package com.cqx;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.SpoutDeclarer;
import backtype.storm.topology.TopologyBuilder;

/**
 * Desc: setup the topology and submit it to either a local of remote Storm
 * cluster depending on the arguments passed to the main method.
 * */
public class HelloWorldTopology {
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
		if ( args != null && args.length == 3 ) {
			String Topology_name = args[0];
			String NIMBUS_HOST = args[1];
			String NIMBUS_THRIFT_PORT = args[2];
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
			BoltDeclarer totalBolt =builder.setBolt("HelloWorldBolt", new HelloWorldBolt(), 1)
					.shuffleGrouping("randomHelloWorld");
			Config conf = new Config();
			// nimbus地址
			conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
			// nimbus thrift端口
			conf.put(Config.NIMBUS_THRIFT_PORT, Integer.valueOf(NIMBUS_THRIFT_PORT));
			// 允许debug
			conf.setDebug(true);
			// 表示整个topology将使用几个worker
			conf.setNumWorkers(1);
			// 提交topology
			StormSubmitter.submitTopology(Topology_name, conf,
					builder.createTopology());
		} else {
			System.out.println("You need input Topology name、NIMBUS_HOST、NIMBUS_THRIFT_PORT.");
		}
	}
}
