package com.cqx.demo;

import kafka.admin.DeleteTopicCommand;
import kafka.admin.TopicCommand;

public class KafkaApiTest {
	public static final String zookeeper_ip_port = "192.168.230.128:2181";
	public static final String zookeeper_param = "--zookeeper";
	private String[] options = null;	
	public String[] getOptions() {
		return options;
	}
	public void getAllTopic(){
		options = new String[]{
			    "--list",
			    zookeeper_param,
			    zookeeper_ip_port
		};
		TopicCommand.main(options);
	}
	public void queryTopicByName(String topic_name){
		options = new String[]{  
			    "--describe",
			    zookeeper_param,
			    zookeeper_ip_port,
			    "--topic",
			    topic_name,
		};
		TopicCommand.main(options);
	}
	public void createTopicByName(String topic_name){
		String[] options = new String[]{
			    "--create",
			    "--zookeeper",
			    zookeeper_ip_port+"/chroot",
			    "--partitions",
			    "20",
			    "--topic",
			    topic_name,
			    "--replication-factor",
			    "3",
			    "--config",
			    "x=y"
		};
		TopicCommand.main(options);
	}
	public void deleteTopicByName(String topic_name){
		options = new String[]{
			    "--zookeeper",
			    zookeeper_ip_port+"/chroot",
			    "--topic",
			    topic_name
		};
		DeleteTopicCommand.main(options);
	}
	public static void main(String[] args) {
		KafkaApiTest kat = new KafkaApiTest();
		kat.getAllTopic();
		kat.queryTopicByName("test");
		kat.queryTopicByName("topic1");
//		kat.deleteTopicByName("test");
//		kat.getAllTopic();
	}
}
