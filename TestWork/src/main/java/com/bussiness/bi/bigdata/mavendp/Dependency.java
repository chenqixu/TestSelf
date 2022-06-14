package com.bussiness.bi.bigdata.mavendp;

import java.util.List;
import java.util.Vector;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

public class Dependency {
	private String groupId = "";
	private String artifactId = "";
	private String version = "";
	private String classifier = "";
	private boolean all_exclusions = true;
	private List<Exclusion> exclusions = new Vector<Exclusion>();
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getClassifier() {
		return classifier;
	}
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
	public List<Exclusion> getExclusions() {
		return exclusions;
	}
	public void setExclusions(List<Exclusion> exclusions) {
		this.exclusions = exclusions;
	}
	public void addExclusions(Exclusion e){
		this.exclusions.add(e);
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("<dependency>").append(MavendpComm.tab);
		sb.append("<groupId>").append(groupId).append("</groupId>").append(MavendpComm.tab);
		sb.append("<artifactId>").append(artifactId).append("</artifactId>").append(MavendpComm.tab);
		sb.append("<version>").append(version).append("</version>").append(MavendpComm.tab);
		if(classifier.length()>0){
			sb.append("<classifier>").append(classifier).append("</classifier>").append(MavendpComm.tab);
		}
		// 去除全部额外依赖
		if(all_exclusions){
			sb.append("<!-- 去除自带的其他包 -->").append(MavendpComm.tab);
			sb.append("<exclusions>").append(MavendpComm.tab);
			sb.append("<exclusion>").append(MavendpComm.tab);
			sb.append("<groupId>*</groupId>").append(MavendpComm.tab);
			sb.append("<artifactId>*</artifactId>").append(MavendpComm.tab);
			sb.append("</exclusion>").append(MavendpComm.tab);
			sb.append("</exclusions>").append(MavendpComm.tab);
		}else	if(exclusions!=null && exclusions.size()>0){
			sb.append("<!-- 去除自带的其他包 -->").append(MavendpComm.tab);
			sb.append("<exclusions>").append(MavendpComm.tab);
			for(int i=0;i<exclusions.size();i++){
				sb.append(exclusions.get(i).toString());
			}
			sb.append("</exclusions>").append(MavendpComm.tab);
		}
		sb.append("</dependency>").append(MavendpComm.tab);		
		return sb.toString();
	}
	public static Dependency XmlToBean(String exclusions_xmlstr){
		Dependency d = new Dependency();
		ResultXML exclusions_rx = new ResultXML();
		XMLData exclusions_xd = new XMLData(exclusions_xmlstr);
		exclusions_rx.rtFlag = true;
		exclusions_rx.bXmldata = true;
		exclusions_rx.xmldata = exclusions_xd;
		exclusions_rx.setbFlag(false);
		exclusions_rx.setRowFlagInfo("exclusion");
		exclusions_rx.First();
		while(!exclusions_rx.isEof()){
			Exclusion e = new Exclusion();
			String exclusion_groupId = exclusions_rx.getColumnsValue("groupId");
			String exclusion_artifactId = exclusions_rx.getColumnsValue("artifactId");
			e.setGroupId(exclusion_groupId);
			e.setArtifactId(exclusion_artifactId);
			d.addExclusions(e);
			exclusions_rx.Next();
		}
		return d;
	}
}
