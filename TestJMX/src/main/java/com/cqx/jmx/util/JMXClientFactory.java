package com.cqx.jmx.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXClientFactory {
	private static JMXClientFactory jmxcf = new JMXClientFactory();
	private static JMXClientUtil jmxcu = jmxcf.new JMXClientUtil();
	
	private JMXClientFactory(){}
	
	public static JMXClientUtil getJMXClientUtil(){
		if(jmxcu==null)jmxcu = jmxcf.new JMXClientUtil();
		return jmxcu;
	}

	/**
	 * 默认使用1099端口
	 * */
	public static void startJMXClient(String objectname){
		getJMXClientUtil().startJMXClient(objectname, 1099);
	}
	
	public static void startJMXClient(String objectname, int rmiPort){
		getJMXClientUtil().startJMXClient(objectname, rmiPort);
	}
	
	public static <T> T getAttributeByName(String attribute){
		return getJMXClientUtil().getAttributeByName(attribute);
	}
	
	public static <T> void setAttributeByName(String attribute, String type, T t){
		getJMXClientUtil().setAttributeByName(attribute, type, t);
	}
	
	private class JMXClientUtil {
		JMXServiceURL url;
		MBeanServerConnection server;
		ObjectName mbeanName;
		
		public void startJMXClient(String objectname, int rmiPort){
			try {
				String ip = InetAddress.getLocalHost().getHostAddress();
				url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ip+":"
						+rmiPort+"/"+objectname+"MBean");
				JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
				server = jmxc.getMBeanServerConnection();
				mbeanName = new ObjectName(objectname + "MBean:name=" + objectname);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getAttributeByName(String attribute){
			T t = null;
			if(server!=null && mbeanName!=null){
				try {
					t = (T) server.getAttribute(mbeanName, attribute);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return t;
		}
		
		public <T> void setAttributeByName(String attribute, String type, T t){
			if(server!=null && mbeanName!=null){
				try {
					server.invoke(mbeanName, attribute, new Object[]{t}, new String[]{type});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
