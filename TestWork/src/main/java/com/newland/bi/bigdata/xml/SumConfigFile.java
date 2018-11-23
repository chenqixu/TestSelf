package com.newland.bi.bigdata.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;
import com.newland.bi.bigdata.changecode.ChangeCode;

/**
 * 汇总pom.xml，提取出公共部分
 * 步骤1：把文件加入文件列表
 * 步骤2：读取文件列表中的文件
 * 步骤3：解析文件，把groupId和artifactId加入Map，计数器加一
 * 步骤4：直到所有文件都处理完成，取出Map中计数器的值和文件列表大小相等的对象
 * 步骤5：把所需对象拼装成pom.xml格式
 * */
public class SumConfigFile extends ChangeCode {
	private Map<PomBean, String> confmap = new HashMap<PomBean, String>();
	
	public Map<PomBean, String> getConfmap() {
		return confmap;
	}

	/**
	 * 读取xml文件，返回内容
	 * */
	protected String readXml(String xmlPath){
		StringBuffer xml = new StringBuffer();
		FileReader fr = null;
		BufferedReader br = null;		
		try{
			fr = new FileReader(xmlPath);
			br = new BufferedReader(fr);
			String tmp = "";
			while((tmp=br.readLine())!=null){
				xml.append(tmp);
			}
			br.close();
			fr.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(fr!=null){
				try{
					fr.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return xml.toString();
	}
	
	/**
	 * 解析XML内容
	 * */
	protected void dealXml(String xmlPath){
		String xml = readXml(xmlPath);
		String xmlhead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		ResultXML rx = new ResultXML();
		XMLData xd = new XMLData(xmlhead+xml);
		rx.rtFlag = true;
		rx.bXmldata = true;
		rx.xmldata = xd;
		rx.setbFlag(false);
		rx.resetParent().node("dependencies").setParentPointer();
		rx.setRowFlagInfo("dependency");
		rx.First();
		while(!rx.isEof()){
			String groupId = rx.getColumnsValue("groupId");
			String artifactId = rx.getColumnsValue("artifactId");
			String version = rx.getColumnsValue("version");
			// 加入到map
			addBean(groupId, artifactId, version);
			rx.Next();
		}		
	}
	
	protected void addBean(String groupId, String artifactId, String version){
		boolean flag = true;		
		for(Map.Entry<PomBean, String> bean : confmap.entrySet()){
			if(bean.getKey().equals(groupId, artifactId, version)){	
				int cnt = Integer.parseInt(bean.getValue());
				if(cnt==0)cnt=1;
				bean.setValue(String.valueOf(cnt+1));
				flag = false;
				break;
			}
		}
		if(flag){
			confmap.put(new PomBean(groupId, artifactId, version), "1");
		}
	}
	
	public void process(String path){
		List<String> xmlpathlist = scan(path, "pom.xml");
		// 剔除相同工程重复pom.xml
		Map<String, String> xmlfilelist = new HashMap<String, String>();
		for(String xmlpath : xmlpathlist){
			File _tmpfile = new File(xmlpath);
			xmlfilelist.put(_tmpfile.getName(), xmlpath);
		}
		// 处理xml
		for(Map.Entry<String, String> xmlpath : xmlfilelist.entrySet()){
			dealXml(xmlpath.getValue());
		}
	}

	public static void main(String[] args) {
		SumConfigFile scf = new SumConfigFile();
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestDataCollector\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestHbaseTool\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestHdfsTool\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestJStorm\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestKafka\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestSFTPTool\\");
		scf.process("h:\\Work\\WorkSpace\\MyEclipse10\\self\\TestSelf\\TestShareClass\\");
		// 取出Map中计数器的值和文件列表大小相等的对象
		for(Map.Entry<PomBean, String> bean : scf.getConfmap().entrySet()){
			if(Integer.valueOf(bean.getValue())>=3){
				System.out.println(bean.getKey().toString());				
			}
		}
	}
}

class PomBean {
	private String groupId;
	private String artifactId;
	private String version;
	public PomBean(String _groupId, String _artifactId, String _version){
		groupId = _groupId;
		artifactId = _artifactId;
		version = _version;
	}
	public String toString(){
		return groupId+" "+artifactId+" "+version;
	}
	public boolean equals(String _groupId, String _artifactId, String _version){
		return groupId.equals(_groupId)&&artifactId.equals(_artifactId)&&version.equals(_version);
	}
}
