package com.cqx.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JobAttempts {
	private List<JobAttempt> jobAttempt;
	public List<JobAttempt> getJobAttempt() {
		return jobAttempt;
	}
	public void setJobAttempt(List<JobAttempt> jobAttempt) {
		this.jobAttempt = jobAttempt;
	}
	
	@XmlRootElement
	public static class JobAttempt {
		private String nodeHttpAddress;
		private String nodeId;
		private String id;
		private String startTime;
		private String containerId;
		private String logsLink;
		public String getNodeHttpAddress() {
			return nodeHttpAddress;
		}
		public void setNodeHttpAddress(String nodeHttpAddress) {
			this.nodeHttpAddress = nodeHttpAddress;
		}
		public String getNodeId() {
			return nodeId;
		}
		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getContainerId() {
			return containerId;
		}
		public void setContainerId(String containerId) {
			this.containerId = containerId;
		}
		public String getLogsLink() {
			return logsLink;
		}
		public void setLogsLink(String logsLink) {
			this.logsLink = logsLink;
		}
	}
}
