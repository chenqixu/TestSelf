package com.cqx.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import com.cqx.jmx.util.JMXFactory;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class HelloAgent
// implements NotificationListener
{
	// private MBeanServer mbs;
	// @Override
	// public void handleNotification(Notification notification, Object
	// handback) {
	// this.mbs = MBeanServerFactory.createMBeanServer("HelloAgent");
	// HelloWorld hw = new HelloWorld();
	// ObjectName helloWorldName = null;
	// try{
	// helloWorldName = new ObjectName("HelloAgent:name=helloWorld");
	// mbs.registerMBean(hw, helloWorldName);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// startHtmlAdaptorServer();
	// }
	// public void startHtmlAdaptorServer(){
	// HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
	// ObjectName adapterName = null;
	// try {
	// // 多个属性使用,分隔
	// adapterName = new ObjectName("HelloAgent:name=htmladapter,port=9092");
	// htmlAdaptorServer.setPort(9092);
	// mbs.registerMBean(htmlAdaptorServer, adapterName);
	// htmlAdaptorServer.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// public static void main(String args[]){
	// System.out.println(" hello agent is running");
	// HelloAgent agent = new HelloAgent();
	// }
	
	public void agentStart() throws Exception {
		// 下面这种方式不能再JConsole中使用
		// MBeanServer server = MBeanServerFactory.createMBeanServer();
		// 首先建立一个MBeanServer,MBeanServer用来管理我们的MBean,通常是通过MBeanServer来获取我们MBean的信息，间接
		// 调用MBean的方法，然后生产我们的资源的一个对象。
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		String domainName = "MyMBean";

		// 为MBean（下面的new Hello()）创建ObjectName实例
		ObjectName helloName = new ObjectName(domainName + ":name=HelloWorld");
		// 将new Hello()这个对象注册到MBeanServer上去
		mbs.registerMBean(new HelloWorld(), helloName);

		// Distributed Layer,
		// 提供了一个HtmlAdaptor。支持Http访问协议，并且有一个不错的HTML界面，这里的Hello就是用这个作为远端管理的界面
		// 事实上HtmlAdaptor是一个简单的HttpServer，它将Http请求转换为JMX Agent的请求
		ObjectName adapterName = new ObjectName(domainName
				+ ":name=htmladapter,port=8082");
		HtmlAdaptorServer adapter = new HtmlAdaptorServer();
		adapter.start();
		mbs.registerMBean(adapter, adapterName);

		int rmiPort = 1099;
		Registry registry = LocateRegistry.createRegistry(rmiPort);

		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://localhost:" + rmiPort + "/"
						+ domainName);
		JMXConnectorServer jmxConnector = JMXConnectorServerFactory
				.newJMXConnectorServer(url, null, mbs);
		jmxConnector.start();
	}
	
	public static void start() {
		JMXFactory.startJMX("HelloWorld", new HelloWorld());
	}
	
	public static void main(String[] args) throws MalformedObjectNameException,
			NotCompliantMBeanException, InstanceAlreadyExistsException,
			MBeanRegistrationException, IOException {
		start();
	}
}
