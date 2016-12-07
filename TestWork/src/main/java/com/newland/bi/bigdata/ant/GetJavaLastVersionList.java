package com.newland.bi.bigdata.ant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.hadoop.fs.Path;

import com.newland.bi.bigdata.changecode.ChangeCode;

/**
 * 获得输入工程CVS清单
 * */
public class GetJavaLastVersionList extends ChangeCode implements Runnable {

	protected String revision = "Repository revision:";
	protected String cvsrootpath = "j:/Work/CVS/BI/";
	protected String writepath = "";
	protected Queue<String> scanPathQueue = null;
	protected String threadname = "";	

	protected String date = "2016-08-24 11:59";
	protected String changesize = "+0000";
	protected String cvs_user = "chenqx";
	protected String space = " ";
	protected String cvs_end = " == <remote>";

	public Queue<String> getScanPathQueue() {
		return scanPathQueue;
	}

	public void setScanPathQueue(Queue<String> scanPathQueue) {
		this.scanPathQueue = scanPathQueue;
	}

	public String getWritepath() {
		return writepath;
	}

	public void setWritepath(String writepath) {
		this.writepath = writepath;
	}
	
	/**
	 * 写入CVS清单到文件，带换行符
	 * */
	protected void writeCVSInfo(BufferedWriter writer, String _tmp) {
		synchronized(writer){
			try {
				if(writer!=null && _tmp!=null && _tmp.trim().length()>0) {				
					writer.write(_tmp);
					writer.write("\r\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 扫描JAVA文件及JAR文件<br>
	 * 通过文件名查询CVS版本号<br>
	 * 输出CVS清单<br>
	 * */
	@Override
	public void Change(){
		String _scanpath = "";
		while((_scanpath=scanPathQueue.poll())!=null){
			String projectname = new Path(_scanpath).getName();
			List<String> javalist = Scan(_scanpath);	
			List<String> liblist = Scan(_scanpath, ".*\\.jar");
			List<String> xmllist = Scan(_scanpath, ".*\\.xml");
			List<String> jsplist = Scan(_scanpath, ".*\\.jsp");
			List<String> propertieslist = Scan(_scanpath, ".*\\.properties");
			BufferedWriter writer = null;	
			try{
				File writeFile = new File(this.writepath+"/"+projectname+".list");
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), this.getWrite_code()));
				// javalist
				for(int i=0;i<javalist.size();i++){
					String path = javalist.get(i).replace("\\", "/");
//					System.out.println("[java]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writeCVSInfo(writer, _tmp);
				}
				// liblist
				for(int i=0;i<liblist.size();i++){
					String path = liblist.get(i).replace("\\", "/");
//					System.out.println("[jar]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writeCVSInfo(writer, _tmp);
				}
				// xmllist
				for(int i=0;i<xmllist.size();i++){
					String path = xmllist.get(i).replace("\\", "/");
//					System.out.println("[xml]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writeCVSInfo(writer, _tmp);
				}
				// jsplist
				for(int i=0;i<jsplist.size();i++){
					String path = jsplist.get(i).replace("\\", "/");
//					System.out.println("[jsp]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writeCVSInfo(writer, _tmp);
				}
				// propertieslist
				for(int i=0;i<propertieslist.size();i++){
					String path = propertieslist.get(i).replace("\\", "/");
//					System.out.println("[properties]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writeCVSInfo(writer, _tmp);
				}
				writer.flush();
				writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(writer!=null){
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 通过文件名查询CVS最新版本号
	 * */
	public String queryCvsLastVersionByFile(String filename){
		StringBuffer rb = new StringBuffer();
		Path p = new Path(filename);
		String path = p.getParent().toString();
		String name = p.getName().toString();
		String newpath = path.substring(cvsrootpath.length());
//		System.out.println("[path]"+path);
//		System.out.println("[newpath]"+newpath);
//		System.out.println("[name]"+name);
		String cmd = "d:/Work/ETL/编译/cvs_status.bat "+name;
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
                if(line.indexOf(revision)>=0){
                	int start = line.indexOf(revision);
                	int end = line.indexOf("/");
                	String lastVersion = line.substring(start+revision.length(), end).trim();
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
                	break;
                }
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

	/**
	 * JAVA调用外部命令打印日志线程
	 * */
	protected class LogThread extends Thread {
        InputStream is;

        String type;

        LogThread(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
        	InputStreamReader isr = null;
        	BufferedReader br = null;
            try {
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr, 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    if (type.equals("err")) {
                        System.out.println("err:"+line);
                    } else {
                    	System.out.println("info:"+line);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
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
            }
        }
    }

	@Override
	public void run() {
		Change();
	}
	
	public static void main(String[] args) {
		GetJavaLastVersionList cc = new GetJavaLastVersionList();
		cc.setWritepath("j:/Work/ETL/编译/List/java");
		cc.setScan_rule(".*\\.java");
		cc.setRead_code("GBK");
		cc.setWrite_code("GBK");		
//		cc.queryCvsLastVersionByFile("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-xml/edc-bigdata-XmlParser/src/main/java/com/newland/bi/xmlparser/XmlParserInfo.java");
		
		// 队列
		Queue<String> scanpathQueue = new ConcurrentLinkedQueue<String>();
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-crawler/edc-bigdata-searchCrawler");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-crawler/edc-bigdata-SNsearchCrawler");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-crawler/edc-bigdata-TXDMsearchCrawler");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectAll");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectAllRT");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectMrDt");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectorHWLte");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectorHWLteNew");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCollect/edc-bigdata-dataCollectorSimultaneously");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCombine/edc-bigdata-dataLoadCombine-Shell");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCombine/edc-bigdata-dataLoadCombineNew");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCombine/edc-bigdata-dataLoadCombineNew1");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCombine/edc-bigdata-dataLoadCombineNew2");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataCombine/edc-bigdata-dataLoadCombineHDFS");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-dataFileSort/edc-bigdata-fileSort");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-fileToHbase/edc-bigdata-fileToHbase");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-fileToHbase/edc-bigdata-fileToHbase_HW");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-fileToHbase/edc-bigdata-fileToHbase_NX");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-fileToHbase/edc-bigdata-giToHbase");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-flume/edc-bigdata-flumecollect");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-flume/edc-bigdata-flumedistribution");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-detailToHbase");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-getHWLteDataToHbase");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-getNXLteDataToHbase");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-joinGiAndLTE");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-joinGiGpfs");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-joinGnXdr");
		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-join/edc-bigdata-joinHwLte");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-other/edc-bigdata-dataFilterExtract");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-other/edc-bigdata-getKeyWord");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-other/edc-bigdata-getMovementTrackData");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-other/edc-bigdata-getMovementTrackDataDay");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-other/edc-bigdata-getMovementTrackDataHour");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-statistic/edc-bigdata-statisticGnxdrIpHttp");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-statistic/edc-bigdata-statisticHuaweiLte");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-statistic/edc-bigdata-statisticIpHttp");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-statistic/edc-bigdata-statisticLte");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-xml/edc-bigdata-XmlParser");
//		scanpathQueue.add("j:/Work/CVS/BI/JavaSourceCode/product/EDC-CODE/edc-bigdata/edc-bigdata-interface/bi_bigdata_svc");
		
		cc.setScanPathQueue(scanpathQueue);
		Thread c1 = new Thread(cc);
		c1.start();
//		Thread c2 = new Thread(cc);
//		c2.start();
//		Thread c3 = new Thread(cc);
//		c3.start();
//		Thread c4 = new Thread(cc);
//		c4.start();
//		Thread c5 = new Thread(cc);
//		c5.start();
	}
}
