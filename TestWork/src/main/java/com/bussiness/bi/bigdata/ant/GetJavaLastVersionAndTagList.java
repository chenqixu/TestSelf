package com.bussiness.bi.bigdata.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.hadoop.fs.Path;

public class GetJavaLastVersionAndTagList extends GetJavaLastVersionList {
	
	protected String head = "head:";
	protected String PD = "EDC_BIGDATA_PD";
	protected String colon = ":";
	String date = "2016-05-03 00:59";
	
	/**
	 * 通过文件名查询CVS最新版本号，过滤已上线(有基线标签)
	 * */
	public String queryCvsLastVersionByFile(String filename){
		StringBuffer rb = new StringBuffer();
		Path p = new Path(filename);
		String path = p.getParent().toString();
		String name = p.getName().toString();
		String newpath = path.substring(cvsrootpath.length());
		String allpdversion = "";//所有的基线版本号
		String lastVersion = "";//最新版本号
//		System.out.println("[path]"+path);
//		System.out.println("[newpath]"+newpath);
//		System.out.println("[name]"+name);
		String cmd = "d:/Work/ETL/编译/cvs_log.bat "+name;
//		cmd = "cmd /c dir";
		Process process = null;
    	InputStreamReader isr = null;
    	BufferedReader br = null;
		try{
			threadname = Thread.currentThread().getName();
			System.out.println("["+threadname+"][cmd]"+cmd);			
			process = Runtime.getRuntime().exec(cmd, null , new File(path));
//			LogThread outputLog = new LogThread(process.getInputStream(), "output");
//			outputLog.start();
//        	LogThread errLog = new LogThread(process.getErrorStream(), "err");
//        	errLog.start();
			isr = new InputStreamReader(process.getInputStream());
            br = new BufferedReader(isr, 1024);            
            String line;
            while ((line = br.readLine()) != null) {
            	String show = "";
                if(line.indexOf(head)>=0){
                	int start = line.indexOf(head);
                	lastVersion = line.substring(start+head.length()).trim();
                	double ld = Double.parseDouble(lastVersion);
                	if(ld>1.1d){
                		show = "M ";
                	}else{
                		show = "A ";
                	}
                	show += date+space+changesize+space+cvs_user+space
                			+lastVersion+space+name+space+newpath+cvs_end;
//                	System.out.println(show);
                	rb.append(show);
                }
                // 如果有找到基线标签,获取版本号
                if(line.contains(PD)){
                	int start = line.indexOf(colon);
                	String PDVersion = line.substring(start+colon.length()).trim();
                	allpdversion += PDVersion + colon;
                }
            }
            // 如果基线版本号包含最新版本号，则过滤(删除输出)，否则输出
            if(allpdversion.contains(lastVersion)){
            	rb.delete(0, rb.length());
            }
            isr.close();
            br.close();
            process.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
        	if(isr != null){
        		try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(br != null){
        		try {
        			br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
			if(process != null){
				process.destroy();
			}
		}
		return rb.toString();
	}
	
	public static void main(String[] args) {
		GetJavaLastVersionAndTagList cc = new GetJavaLastVersionAndTagList();
		cc.setWritepath("d:/Work/ETL/编译/List/java");
		cc.setScan_rule(".*\\.java");
		cc.setRead_code("GBK");
		cc.setWrite_code("GBK");

		// 队列
		Queue<String> scanpathQueue = new ConcurrentLinkedQueue<String>();
		
		scanpathQueue.add("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-interface/bi_bigdata_svc");
		
		cc.setScanPathQueue(scanpathQueue);
		Thread c1 = new Thread(cc);
		c1.start();
	}
}
