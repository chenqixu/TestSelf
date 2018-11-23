package com.cqx.jmx.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

public class JMXFactory {
	private static LogInfoFactory log = LogInfoFactory.getInstance();
	private static JMXFactory jmxf = new JMXFactory();
	
	private JMXFactory(){
		log.setNeedTime(false);
		log.setLevel(1);
	}
	
	public static JMXUtil getJMXUtil(){
		return jmxf.new JMXUtil();
	}
	
	/**
	 * 默认使用1099端口
	 * */
	public static void startJMX(String objectname, Object obj){
		getJMXUtil().startJMX(objectname, obj, 1099);
	}
	
	public static void startJMX(String objectname, Object obj, int rmiPort){
		getJMXUtil().startJMX(objectname, obj, rmiPort);
	}
	
	private class JMXUtil {
		protected MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		protected ObjectName object;		
			
		public void startJMX(String objectname, Object obj, int rmiPort) {
			try {
				object = new ObjectName(objectname + "MBean:name=" + objectname);
				mbs.registerMBean(obj, object);
				
				LocateRegistry.createRegistry(rmiPort);
				String ip = InetAddress.getLocalHost().getHostAddress();
				JMXServiceURL url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://"+ ip + ":" + rmiPort + "/" + objectname + "MBean");
				JMXConnectorServer jmxConnector = JMXConnectorServerFactory
						.newJMXConnectorServer(url, null, mbs);
				jmxConnector.start();
				log.info("["+objectname+"] jmx is start.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
