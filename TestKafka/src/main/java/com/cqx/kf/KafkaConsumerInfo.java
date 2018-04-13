package com.cqx.kf;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class KafkaConsumerInfo implements Serializable {
	private static final long serialVersionUID = 5663221315432373642L;
	private String topic;
	private String lstZookeeper;
	private String lstBroker;
	private String groupId;
	private String keySerializerClass;
	private String serializerClass;
	private String autoCommitInterval;
	private String zookeeperSessionTimeout;
	private String zookeeperSyncTime;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getLstBroker() {
		return this.lstBroker;
	}

	public void setLstBroker(String lstBroker) {
		this.lstBroker = lstBroker;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getKeySerializerClass() {
		return this.keySerializerClass;
	}

	public void setKeySerializerClass(String keySerializerClass) {
		this.keySerializerClass = keySerializerClass;
	}

	public String getSerializerClass() {
		return this.serializerClass;
	}

	public void setSerializerClass(String serializerClass) {
		this.serializerClass = serializerClass;
	}

	public String getAutoCommitInterval() {
		return this.autoCommitInterval;
	}

	public void setAutoCommitInterval(String autoCommitInterval) {
		this.autoCommitInterval = autoCommitInterval;
	}

	public String getZookeeperSessionTimeout() {
		return this.zookeeperSessionTimeout;
	}

	public void setZookeeperSessionTimeout(String zookeeperSessionTimeout) {
		this.zookeeperSessionTimeout = zookeeperSessionTimeout;
	}

	public String getZookeeperSyncTime() {
		return this.zookeeperSyncTime;
	}

	public void setZookeeperSyncTime(String zookeeperSyncTime) {
		this.zookeeperSyncTime = zookeeperSyncTime;
	}

	public String getLstZookeeper() {
		return this.lstZookeeper;
	}

	public void setLstZookeeper(String lstZookeeper) {
		this.lstZookeeper = lstZookeeper;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
