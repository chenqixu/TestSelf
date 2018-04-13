package com.cqx.kf;

import java.util.ArrayList;
import java.util.List;

import com.newland.bd.model.NLMessage;

public class TaskSpout {
	private KafkaMsgTool kafkaMsgTool;
	ArrayList<NLMessage> msgs;
	String topic;
	String groupId;
	String zooKeeper;
	String brokerList;
	String rdzk;

	public TaskSpout() {
		this.topic = "TASK_TOPIC";
		this.groupId = "udap";
		this.rdzk = "10.1.8.78:2182,10.1.8.81:2182,10.1.4.186:2182/udap/kafka";
		this.brokerList = "10.1.4.185:9092,10.1.4.186:9092";
	}

	public void open() {
		this.kafkaMsgTool = new KafkaMsgTool(this.topic, this.groupId,
				this.rdzk, this.brokerList);
		this.kafkaMsgTool.initConsumer();

		List msgs = null;
		try {
			msgs = this.kafkaMsgTool.getNLMsgs();
		} catch (Exception e2) {
			System.out.println("kafka消息读取异常"+e2.toString());
		} finally {
			System.out.println("消息读取返回==");
		}
	}

	public static void main(String[] args) {
		TaskSpout ts = new TaskSpout();
		ts.open();
	}
}
