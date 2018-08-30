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
	private static JMXFactory jmxf = new JMXFactory();
	
	private JMXFactory(){}
	
	public static JMXUtil getJMXUtil(){
		return jmxf.new JMXUtil();
	}
	
	public static void startJMX(String objectname, Object obj){
		getJMXUtil().startJMX(objectname, obj);
	}
	
	private class JMXUtil {
		protected MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		protected ObjectName object;		
			
		public void startJMX(String objectname, Object obj) {
			try {
				object = new ObjectName(objectname + "MBean:name=" + objectname);
				mbs.registerMBean(obj, object);
				
				int rmiPort = 1099;
				LocateRegistry.createRegistry(rmiPort);
				String ip = InetAddress.getLocalHost().getHostAddress();
				JMXServiceURL url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://"+ ip + ":" + rmiPort + "/" + objectname + "MBean");
				JMXConnectorServer jmxConnector = JMXConnectorServerFactory
						.newJMXConnectorServer(url, null, mbs);
				jmxConnector.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
