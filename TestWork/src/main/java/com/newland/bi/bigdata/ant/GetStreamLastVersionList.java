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
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.hadoop.fs.Path;

import com.newland.bi.bigdata.changecode.ChangeCode;

/**
 * 获得输入工程CVS清单
 * */
public class GetStreamLastVersionList extends ChangeCode implements Runnable {

	private String revision = "Repository revision:";
	private String cvsrootpath = "d:/Work/CVS/BI/";
	private String writepath = "";
	private Queue<String> scanPathQueue = null;
	private String threadname = "";
	private List<String> cvsfilterlist = null;
	
	public GetStreamLastVersionList(){
		cvsfilterlist = new Vector<String>();
		cvsfilterlist.add("CVS/Entries");
		cvsfilterlist.add("CVS/Repository");
		cvsfilterlist.add("CVS/Root");
	}

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
	 * 扫描JAVA文件及JAR文件<br>
	 * 通过文件名查询CVS版本号<br>
	 * 输出CVS清单<br>
	 * */
	@Override
	public void change(){
		String _scanpath = "";
		boolean cvsfilterflag = false;
		while((_scanpath=scanPathQueue.poll())!=null){
			String projectname = new Path(_scanpath).getName();
			List<String> javalist = scan(_scanpath);
			BufferedWriter writer = null;	
			try{
				File writeFile = new File(this.writepath+"/"+projectname+".list");
				writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(writeFile), this.getWrite_code()));
				// alllist				
				for(int i=0;i<javalist.size();i++){
					String path = javalist.get(i).replace("\\", "/");
					for(int j=0;j<cvsfilterlist.size();j++){
						if(path.indexOf(cvsfilterlist.get(j))>=0){
							cvsfilterflag = true;
							break;
						}else{
							cvsfilterflag = false;
						}
					}
					if(cvsfilterflag) continue;
					System.out.println("[stream]"+path);
					String _tmp = queryCvsLastVersionByFile(path);
					writer.write(_tmp);
					writer.write("\r\n");
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
                	show += "2015-12-02 00:59 +0000 chenqx "+lastVersion+" "+name+" "+newpath+" == <remote>";
//                	System.out.println(show);
                	rb.append(show);
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
	class LogThread extends Thread {
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
		change();
	}
	
	public static void main(String[] args) {
		GetStreamLastVersionList cc = new GetStreamLastVersionList();
		cc.setWritepath("d:/Work/ETL/编译/List/stream");
		cc.setScan_rule(".*");
		cc.setRead_code("GBK");
		cc.setWrite_code("GBK");		
//		cc.queryCvsLastVersionByFile("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata/edc-bigdata-xml/edc-bigdata-XmlParser/src/main/java/com/newland/bi/xmlparser/XmlParserInfo.java");
		
		// 队列
		Queue<String> scanpathQueue = new ConcurrentLinkedQueue<String>();
		// streams
//		scanpathQueue.add("d:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata-stream/GN/Rain");
		scanpathQueue.add("D:/Work/CVS/BI/SSC/BIGDATA2.0/Develop/SourceCode/Code/edc-bigdata-stream/GN/nl-bi-dataCollect-GnXDR");
		
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
