package com.cqx.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Job {
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
	private String uberized;
	private String diagnostics;
	private String avgMapTime;
	private String avgReduceTime;
	private String avgShuffleTime;
	private String avgMergeTime;
	private String failedReduceAttempts;
	private String killedReduceAttempts;
	private String successfulReduceAttempts;
	private String failedMapAttempts;
	private String killedMapAttempts;
	private String successfulMapAttempts;
	private List<Acls> acls;
	public List<Acls> getAcls() {
		return acls;
	}
	public void setAcls(List<Acls> acls) {
		this.acls = acls;
	}
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
		return name;
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
	public String getUberized() {
		return uberized;
	}
	public void setUberized(String uberized) {
		this.uberized = uberized;
	}
	public String getDiagnostics() {
		return diagnostics;
	}
	public void setDiagnostics(String diagnostics) {
		this.diagnostics = diagnostics;
	}
	public String getAvgMapTime() {
		return avgMapTime;
	}
	public void setAvgMapTime(String avgMapTime) {
		this.avgMapTime = avgMapTime;
	}
	public String getAvgReduceTime() {
		return avgReduceTime;
	}
	public void setAvgReduceTime(String avgReduceTime) {
		this.avgReduceTime = avgReduceTime;
	}
	public String getAvgShuffleTime() {
		return avgShuffleTime;
	}
	public void setAvgShuffleTime(String avgShuffleTime) {
		this.avgShuffleTime = avgShuffleTime;
	}
	public String getAvgMergeTime() {
		return avgMergeTime;
	}
	public void setAvgMergeTime(String avgMergeTime) {
		this.avgMergeTime = avgMergeTime;
	}
	public String getFailedReduceAttempts() {
		return failedReduceAttempts;
	}
	public void setFailedReduceAttempts(String failedReduceAttempts) {
		this.failedReduceAttempts = failedReduceAttempts;
	}
	public String getKilledReduceAttempts() {
		return killedReduceAttempts;
	}
	public void setKilledReduceAttempts(String killedReduceAttempts) {
		this.killedReduceAttempts = killedReduceAttempts;
	}
	public String getSuccessfulReduceAttempts() {
		return successfulReduceAttempts;
	}
	public void setSuccessfulReduceAttempts(String successfulReduceAttempts) {
		this.successfulReduceAttempts = successfulReduceAttempts;
	}
	public String getFailedMapAttempts() {
		return failedMapAttempts;
	}
	public void setFailedMapAttempts(String failedMapAttempts) {
		this.failedMapAttempts = failedMapAttempts;
	}
	public String getKilledMapAttempts() {
		return killedMapAttempts;
	}
	public void setKilledMapAttempts(String killedMapAttempts) {
		this.killedMapAttempts = killedMapAttempts;
	}
	public String getSuccessfulMapAttempts() {
		return successfulMapAttempts;
	}
	public void setSuccessfulMapAttempts(String successfulMapAttempts) {
		this.successfulMapAttempts = successfulMapAttempts;
	}
	
	@XmlRootElement
	public static class Acls {
		private String name;
		private String value;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}
