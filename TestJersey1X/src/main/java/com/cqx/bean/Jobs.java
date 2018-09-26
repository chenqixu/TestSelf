package com.cqx.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Jobs {
	private List<Job> job;
	public List<Job> getJob() {
		return job;
	}
	public void setJob(List<Job> job) {
		this.job = job;
	}
	
	@XmlRootElement
	public static class Job {
		private String submitTime;
		private String startTime;
		private String finishTime;
		private String id;
		private String name;
		private String queue;
		private String user;
		private String state;
		private String mapsTotal;
		private String mapsCompleted;
		private String reducesTotal;
		private String reducesCompleted;
		public String getSubmitTime() {
			return submitTime;
		}
		public void setSubmitTime(String submitTime) {
			this.submitTime = submitTime;
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getFinishTime() {
			return finishTime;
		}
		public void setFinishTime(String finishTime) {
			this.finishTime = finishTime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name.trim().replaceAll("\r|\n*", "");
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getQueue() {
			return queue;
		}
		public void setQueue(String queue) {
			this.queue = queue;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getMapsTotal() {
			return mapsTotal;
		}
		public void setMapsTotal(String mapsTotal) {
			this.mapsTotal = mapsTotal;
		}
		public String getMapsCompleted() {
			return mapsCompleted;
		}
		public void setMapsCompleted(String mapsCompleted) {
			this.mapsCompleted = mapsCompleted;
		}
		public String getReducesTotal() {
			return reducesTotal;
		}
		public void setReducesTotal(String reducesTotal) {
			this.reducesTotal = reducesTotal;
		}
		public String getReducesCompleted() {
			return reducesCompleted;
		}
		public void setReducesCompleted(String reducesCompleted) {
			this.reducesCompleted = reducesCompleted;
		}
	}
}
