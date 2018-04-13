package com.cqx.kf;

import com.newland.bd.model.NLMessage;
import com.newland.bd.utils.jms.kafka.NKafkaConsumer;
import com.newland.bd.utils.jms.kafka.NKafkaLogProducer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMsgTool {
	Logger log = LoggerFactory.getLogger(KafkaMsgTool.class);
	String topic;
	String groupId;
	String zooKeeper;
	String broker_address;
	private NKafkaConsumer<NLMessage, NLMessage> cons;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getZooKeeper() {
		return this.zooKeeper;
	}

	public void setZooKeeper(String zooKeeper) {
		this.zooKeeper = zooKeeper;
	}

	public void initConsumer() {
		KafkaConsumerInfo logInfo = new KafkaConsumerInfo();
		logInfo.setLstZookeeper(this.zooKeeper);

		this.cons = new NKafkaConsumer(this.topic);
		Properties props = new Properties();
		props.setProperty("bootstrap.servers", this.broker_address);

		props.setProperty("group.id", this.groupId);
		props.setProperty("zookeeper.session.timeout.ms", "10000");
		props.setProperty("zookeeper.sync.time.ms", "2000");
		props.setProperty("auto.commit.interval.ms", "2000");
		props.setProperty("compression.type", "none");

		props.setProperty("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.setProperty("value.deserializer",
				"com.newland.bd.utils.jms.kafka.serializer.ObjectDeserializer4ProtoBuf");
		this.cons.setProps(props);
		this.cons.init();
	}

	public void initProducer() {
		NKafkaLogProducer.init(this.topic, this.broker_address);
	}

	public KafkaMsgTool(String topic, String groupId, String zooKeeper,
			String broker_address) {
		this.topic = topic;
		this.groupId = groupId;
		this.zooKeeper = zooKeeper;
		this.broker_address = broker_address;
	}

	public List<NLMessage> getNLMsgs() throws Exception {
		if (this.cons == null) {
			throw new Exception("please init initConsumer");
		}

		List msgs = this.cons.consume(2000L);
		if (msgs != null) {
			return msgs;
		}
		return null;
	}

	public void sendNLMsg(NLMessage msg) throws Exception {
		NKafkaLogProducer.send(msg, this.topic);
	}

	public void sendNLMsg(String topic, NLMessage msg) throws Exception {
		NKafkaLogProducer.send(msg, topic);
	}

	public static String exceptionFormat(Throwable e) {
		String errorInfo = null;
		if (e == null) {
			return "null";
		}

		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		printWriter.close();
		errorInfo = result.toString();
		if (errorInfo.length() > 1700) {
			errorInfo = e.getMessage() + "\n"
					+ errorInfo.substring(errorInfo.length() - 1500);
		}

		return errorInfo;
	}

	public void close() {
		if (this.cons != null)
			this.cons.close();
	}
}
