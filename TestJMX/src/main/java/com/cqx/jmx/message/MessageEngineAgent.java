package com.cqx.jmx.message;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class MessageEngineAgent {
	@SuppressWarnings("restriction")
	public void start() {
		try {
			StringBuilder param = new StringBuilder(); 
			param.append("com.sun.management.jmxremote.port=9999").append(","); 
			param.append("com.sun.management.jmxremote.authenticate=false").append(","); 
			param.append("com.sun.management.jmxremote.ssl=false").append(","); 
			sun.management.Agent.premain(param.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName mxbeanName = new ObjectName(
					"com.example:type=MessageEngine");
			MessageEngineMXBean mxbean = new MessageEngine();
			mbs.registerMBean(mxbean, mxbeanName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
