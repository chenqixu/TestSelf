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
			// ����topology��������
			TopologyBuilder builder = new TopologyBuilder();
			// ����Spout������new HelloWorldSpout() Ϊ����spout����
			// randomHelloWorld Ϊspout�����֣�ע�������в�Ҫ���пո�
			// spout�Ĳ������ã���������Ϊ1
			SpoutDeclarer spout = builder.setSpout("randomHelloWorld", new HelloWorldSpout(), 1);
			// ����bolt��HelloWorldBoltΪbolt����
			// HelloWorldBolt Ϊbolt����
			// 1Ϊbolt������
			// shuffleGrouping��SequenceTopologyDef.SEQUENCE_SPOUT_NAME����
			// ��ʾ����SequenceTopologyDef.SEQUENCE_SPOUT_NAME�����ݣ�������shuffle��ʽ��
			// ��ÿ��spout�����ѯ����tuple����һ��bolt��
			BoltDeclarer totalBolt =builder.setBolt("HelloWorldBolt", new HelloWorldBolt(), 1)
					.shuffleGrouping("randomHelloWorld");
			Config conf = new Config();
			// nimbus��ַ
			conf.put(Config.NIMBUS_HOST, NIMBUS_HOST);
			// nimbus thrift�˿�
			conf.put(Config.NIMBUS_THRIFT_PORT, Integer.valueOf(NIMBUS_THRIFT_PORT));
			// ����debug
			conf.setDebug(true);
			// ��ʾ����topology��ʹ�ü���worker
			conf.setNumWorkers(1);
			// �ύtopology
			StormSubmitter.submitTopology(Topology_name, conf,
					builder.createTopology());
		} else {
			System.out.println("You need input Topology name��NIMBUS_HOST��NIMBUS_THRIFT_PORT.");
		}
	}
}
