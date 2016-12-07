package com.newland.bi.bigdata.mavendp;

import java.util.List;
import java.util.Vector;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

public class Dependencies {
	private List<Dependency> dependency = new Vector<Dependency>();
	public List<Dependency> getDependency() {
		return dependency;
	}
	public void setDependency(List<Dependency> dependency) {
		this.dependency = dependency;
	}
	public void addDependency(Dependency d){
		this.dependency.add(d);
	}
	public Dependency getLastDependency(){
		return this.dependency.get(this.dependency.size()>0?(this.dependency.size()-1):0);
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();	
		sb.append("<dependencies>").append(MavendpComm.tab);
		if(dependency!=null && dependency.size()>0){
			for(int i=0;i<dependency.size();i++){
				sb.append(dependency.get(i).toString());
			}
		}
		sb.append("</dependencies>").append(MavendpComm.tab);		
		return sb.toString();
	}
	public static Dependencies XmlToBean(String xmlstr){
		Dependencies ds = new Dependencies();
		ResultXML rx = new ResultXML();
		XMLData xd = new XMLData(xmlstr);
		rx.rtFlag = true;
		rx.bXmldata = true;
		rx.xmldata = xd;
		rx.setbFlag(false);
		rx.setRowFlagInfo("dependency");
		rx.First();
		while(!rx.isEof()){
			Dependency d = new Dependency();
			String groupId = rx.getColumnsValue("groupId");
			String artifactId = rx.getColumnsValue("artifactId");
			String version = rx.getColumnsValue("version");
			d.setGroupId(groupId);
			d.setArtifactId(artifactId);
			d.setVersion(version);
			if(rx.isExistNode("exclusions")){
				String exclusions_xmlstr = rx.getColumnsValue("exclusions");
				d.setExclusions(Dependency.XmlToBean(exclusions_xmlstr).getExclusions());
			}
			ds.addDependency(d);
			rx.Next();
		}
		return ds;
	}
}
