package com.bussiness.bi.bigdata.mavendp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;

/**
 * 对<b>[maven]dependency:tree</b>出来的依赖关系进行处理，去除不需要的额外依赖
 * <br>输入依赖关系树，输出pom依赖文件
 * */
public class DependencyExclusions {
	
	/**
	 * 解析依赖树文件，把去除依赖加入到Dependencies中
	 * */
	public void process(){
		BufferedReader reader = null;
		BufferedWriter writer = null;	
		Dependencies ds = new Dependencies();
		try{
			File readFile = new File(MavendpComm.programpath+MavendpComm.readfile);
			File writeFile = new File(MavendpComm.programpath+MavendpComm.writepom);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFile), MavendpComm.read_code));
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(writeFile), MavendpComm.write_code));
			String _tmp = null;
			while((_tmp=reader.readLine())!=null){
				String tt = "";
				tt = CutStr(_tmp);
				System.out.println(_tmp);
				Dependency d = StrDeal(tt);
				// 新的树节点
				if(_tmp.indexOf(MavendpComm.start_str)==0){
					ds.addDependency(d);
				}
				// 最后一个树节点
				else if(_tmp.indexOf(MavendpComm.end_str)==0){
					ds.addDependency(d);
				}
				// 旧的树节点
				else{
					Exclusion e = new Exclusion();
					e.setArtifactId(d.getArtifactId());
					e.setGroupId(d.getGroupId());
					ds.getLastDependency().addExclusions(e);
				}
			}
			writer.write(ds.toString());
			reader.close();
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 按要求截取字符
	 * */
	public String CutStr(String str){
		String tmp = "";
		if(str.indexOf(MavendpComm.other_str)>=0){
			tmp = str.substring(str.indexOf(MavendpComm.other_str)+MavendpComm.other_str.length());
		}
		return tmp;
	}
	
	/**
	 * 字符串处理成javabean
	 * */
	public Dependency StrDeal(String str){
		Dependency d = null;
		String[] arrstr = str.split(":");
		if(arrstr!=null && arrstr.length==5){
			d = new Dependency();
			d.setGroupId(arrstr[0]);
			d.setArtifactId(arrstr[1]);
			d.setVersion(arrstr[3]);
		}
		return d;
	}	
	
	/**
	 * xml文件转javabean
	 * */
	public void parserXml(){
		BufferedReader reader = null;
		Dependencies ds = new Dependencies();
		StringBuffer sb = new StringBuffer();
		try{
			File readFile = new File(MavendpComm.programpath+MavendpComm.writepom);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFile), MavendpComm.read_code));
			String _tmp = null;
			while((_tmp=reader.readLine())!=null){
				sb.append(_tmp);
			}
			reader.close();
			Dependencies.XmlToBean(sb.toString());
			System.out.println(ds.getDependency().size());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 给每个pom文件加上去除所有依赖
	 * <exclusions>
	 * <exclusion>
	 * <groupId>*</groupId>
	 * <artifactId>*</artifactId>
	 * </exclusion>
	 * </exclusions>
	 * */
	public void processAll(String scan_path){
		List<String> changelist = Scan(scan_path);
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try{
			for(int i=0;i<changelist.size();i++){
				Dependencies ds = new Dependencies();
				StringBuffer sb = new StringBuffer();
				StringBuffer sbold = new StringBuffer();
				File readFile = new File(changelist.get(i));
				File writeFile = new File(changelist.get(i)+"bak");
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(readFile), MavendpComm.read_code));
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), MavendpComm.write_code));
				String _tmp = null;
				int count = 0;
				while((_tmp=reader.readLine())!=null){
					if(_tmp.indexOf("<dependencies>")>=0 || _tmp.indexOf("</dependencies>")>=0){
						count++;
					}
					if(count==1)
						sb.append(_tmp).append(MavendpComm.tab);
					else
						sbold.append(_tmp).append(MavendpComm.tab);
				}
				reader.close();
				// 有依赖需要修改
				if(sb.length()>0){
					System.out.println("[有依赖需要修改]"+changelist.get(i));
					sb.append("</dependencies>");
//					System.out.println(sb.toString());
					ResultXML rx = new ResultXML();
					XMLData xd = new XMLData(sb.toString());
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
						String classifier = rx.getColumnsValue("classifier");
						d.setGroupId(groupId);
						d.setArtifactId(artifactId);
						d.setVersion(version);
						d.setClassifier(classifier);
						ds.addDependency(d);
						rx.Next();
					}
					String endstr = sbold.toString().replace("</dependencies>", ds.toString());
					writer.write(endstr);
					writer.flush();
					writer.close();
					if(readFile.exists()){
						boolean delete = readFile.delete();
						if(delete){
							writeFile.renameTo(readFile);
						}
					}
				}
				// 没有依赖需要修改
				else{
					System.out.println("[没有依赖需要修改]"+changelist.get(i));
					writer.close();
					if(writeFile.exists()){
						writeFile.delete();
					}
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 扫描过滤出文件列表
	 * */
	public List<String> Scan(String scanpath){
		List<String> result = new Vector<String>();
		File file = null;
		Pattern pat;
        Matcher mat;
        boolean matched = false;
		try{
			if(scanpath.trim().length()>0){
				file = new File(scanpath);
				if(file.isDirectory()){
					File[] fl = file.listFiles();
					for(int i=0;i<fl.length;i++){
						if(fl[i].isFile()){
							// 规则
							pat = Pattern.compile(MavendpComm.scan_rule);
							// 名称
					        mat = pat.matcher(fl[i].getName());
					        // 名称是否匹配规则
					        matched = mat.matches();
					        if(matched){
					        	// 不需要处理target下的pom.xml
					        	if(fl[i].getPath().indexOf("target")>=0){
					        		continue;
					        	}
//					        	System.out.println("[matched]"+fl[i].getPath());
					        	result.add(fl[i].getPath());
					        }
						}else if(fl[i].isDirectory()){
							result.addAll(Scan(fl[i].getPath()));
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
//		new DependencyExclusions().process();
//		new DependencyExclusions().Scan("h:/Work/WorkSpace/MyEclipse10");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-crawler");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-dataCollect");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-dataCombine");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-dataFileSort");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-fileToHbase");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-flume");		
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-join");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-other");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-statistic");
//		new DependencyExclusions().processAll("h:/Work/WorkSpace/MyEclipse10/edc-bigdata-xml");
	}
}
