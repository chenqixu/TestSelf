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
