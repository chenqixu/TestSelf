package com.cqx.process;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogThread extends Thread {
	private static MyLogger log = MyLoggerFactory.getLogger(LogThread.class);
	private InputStream is;

	private String type;

	public LogThread(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
    	InputStreamReader isr = null;
    	BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, "GB2312");
            br = new BufferedReader(isr, 1024);
            String line;
            while ((line = br.readLine()) != null) {
                if (type.equals("err")) {
                	log.info("##错误日志##"+line);
                } else {
                	log.info("##内容##"+line);
                }
            }
        } catch (IOException ioe) {
        	log.error("创建/读取 IO异常", ioe);
        } finally {
        	if(isr != null){
        		try {
					isr.close();
				} catch (IOException e) {
					log.error("InputStreamReader流关闭IO异常", e);
				}
        	}
        	if(br != null){
        		try {
        			br.close();
				} catch (IOException e) {
					log.error("BufferedReader流关闭IO异常", e);
				}
        	}
        }
    }
}
