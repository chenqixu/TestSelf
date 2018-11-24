package com.cqx.jmx;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.cqx.jmx.util.JMXClientFactory;
import com.cqx.jmx.util.LogInfoFactory;

public class HelloClient {
	private LogInfoFactory log = LogInfoFactory.getInstance();
	
	public HelloClient() {
		log.setNeedTime(false);
		log.setLevel(1);
	}
	
	public void start(String beanname) {
		try {
			// 如果agent不做配置的话，默认jndi path为jmxrmi
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/"+beanname+"MBean");
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection server = jmxc.getMBeanServerConnection();
			ObjectName mbeanName = new ObjectName(beanname + "MBean:name=" + beanname);
			
			// 访问paused属性
			boolean paused = (Boolean)server.getAttribute(mbeanName, "Paused");
			System.out.println("[first]"+paused);
			// 调用pause方法
			if (!paused) {
				server.invoke(mbeanName, "pause", new Object[]{true}, new String[]{"boolean"});
			}else{
				server.invoke(mbeanName, "pause", new Object[]{false}, new String[]{"boolean"});
			}
			paused = (Boolean)server.getAttribute(mbeanName, "Paused");
			System.out.println("[second]"+paused);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void startClient(String beanname){
		JMXClientFactory.startJMXClient(beanname);
		boolean paused = JMXClientFactory.getAttributeByName("Paused");
		log.info(paused+"");
		JMXClientFactory.setAttributeByName("pause", "boolean", !paused);
		paused = JMXClientFactory.getAttributeByName("Paused");
		log.info(paused+"");
		String Greeting = JMXClientFactory.getAttributeByName("Greeting");
		log.info(Greeting+"");
	}
	
	public static void main(String[] args) {
//		new HelloClient().start("HelloWorld");
		new HelloClient().startClient("HelloWorld");
	}
}
