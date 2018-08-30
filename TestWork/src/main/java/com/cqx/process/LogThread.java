package com.cqx.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogThread extends Thread {
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
            isr = new InputStreamReader(is, "GB2312");
            br = new BufferedReader(isr, 1024);
            String line;
            while ((line = br.readLine()) != null) {
                if (type.equals("err")) {
                	LogInfoFactory.info("##错误日志##"+line);
                } else {
                	LogInfoFactory.info("##内容##"+line);
                }
            }
        } catch (IOException ioe) {
            LogInfoFactory.err("创建/读取 IO异常", ioe);
        } finally {
        	if(isr != null){
        		try {
					isr.close();
				} catch (IOException e) {
					LogInfoFactory.err("InputStreamReader流关闭IO异常", e);
				}
        	}
        	if(br != null){
        		try {
        			br.close();
				} catch (IOException e) {
					LogInfoFactory.err("BufferedReader流关闭IO异常", e);
				}
        	}
        }
    }
}
