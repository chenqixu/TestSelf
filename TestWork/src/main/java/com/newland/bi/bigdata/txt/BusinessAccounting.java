package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.newland.bi.bigdata.changecode.ChangeCode;

public class BusinessAccounting extends ChangeCode implements Runnable {

	protected Queue<String> scanPathQueue = null;
	
	public Queue<String> getScanPathQueue() {
		return scanPathQueue;
	}

	public void setScanPathQueue(Queue<String> scanPathQueue) {
		this.scanPathQueue = scanPathQueue;
	}


	/**
	 * 扫描JAVA文件<br>
	 * 输出文件及文件行数<br>
	 * */
	@Override
	public void Change(){
		String _scanpath = "";
		while((_scanpath=scanPathQueue.poll())!=null){
			List<String> javalist = Scan(_scanpath);
			List<String> xmllist = Scan(_scanpath, ".*\\.xml");
			List<String> propertieslist = Scan(_scanpath, ".*\\.properties");
			try{
				// javalist
				for(int i=0;i<javalist.size();i++){
					String path = javalist.get(i).replace("\\", "/");
					String linecnt = String.valueOf(getLineCountByFile(path));
					System.out.println("[java]"+path+"[linecnt]"+linecnt);					
				}
				// xmllist
				for(int i=0;i<xmllist.size();i++){
					String path = xmllist.get(i).replace("\\", "/");
					String linecnt = String.valueOf(getLineCountByFile(path));
					System.out.println("[java]"+path+"[linecnt]"+linecnt);	
				}
				// propertieslist
				for(int i=0;i<propertieslist.size();i++){
					String path = propertieslist.get(i).replace("\\", "/");
					String linecnt = String.valueOf(getLineCountByFile(path));
					System.out.println("[java]"+path+"[linecnt]"+linecnt);	
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * 通过文件名统计文件行数
	 * */
	@SuppressWarnings("unused")
	public int getLineCountByFile(String filename){
		BufferedReader reader = null;
    	int linecnt = 0;
		try{
			File readFile = new File(filename);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFile), read_code));
			String _tmp = null;
			while((_tmp=reader.readLine())!=null){
				linecnt++;
			}
			reader.close();
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
		return linecnt;
	}
	
	@Override
	public void run() {
		Change();
	}

	public static void main(String[] args) {
		BusinessAccounting ba = new BusinessAccounting();
		ba.setScan_rule(".*\\.java");
		ba.setRead_code("GBK");

		// 队列
		Queue<String> scanpathQueue = new ConcurrentLinkedQueue<String>();
		scanpathQueue.add("d:/Work/CVS/BI/SSC/NETLOGCHECK/Develop/SourceCode/Code/edc-bigdata-queryNetLog-HN");
		scanpathQueue.add("d:/Work/CVS/BI/SSC/NETLOGCHECK/Develop/SourceCode/Code/nl-component-fileToHbaseHN");
		scanpathQueue.add("d:/Work/CVS/BI/SSC/NETLOGCHECK/Develop/SourceCode/Code/nl-component-pretreatmentHNData");
		
		ba.setScanPathQueue(scanpathQueue);
		Thread c1 = new Thread(ba);
		c1.start();
	}
}
