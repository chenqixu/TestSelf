package com.bussiness.bi.bigdata.mavendp;

public class Exclusion {
	private String groupId = "";
	private String artifactId = "";
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("<exclusion>").append(MavendpComm.tab);
		sb.append("<groupId>").append(groupId).append("</groupId>").append(MavendpComm.tab);
		sb.append("<artifactId>").append(artifactId).append("</artifactId>").append(MavendpComm.tab);
		sb.append("</exclusion>").append(MavendpComm.tab);
		return sb.toString();
	}
}
