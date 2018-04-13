package com.cqx.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

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
	// // �������ʹ��,�ָ�
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
	public static void main(String[] args) throws MalformedObjectNameException,
			NotCompliantMBeanException, InstanceAlreadyExistsException,
			MBeanRegistrationException, IOException {
		// �������ַ�ʽ������JConsole��ʹ��
		// MBeanServer server = MBeanServerFactory.createMBeanServer();
		// ���Ƚ���һ��MBeanServer,MBeanServer�����������ǵ�MBean,ͨ����ͨ��MBeanServer����ȡ����MBean����Ϣ�����
		// ����MBean�ķ�����Ȼ���������ǵ���Դ��һ������
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		String domainName = "MyMBean";

		// ΪMBean�������new Hello()������ObjectNameʵ��
		ObjectName helloName = new ObjectName(domainName + ":name=HelloWorld");
		// ��new Hello()�������ע�ᵽMBeanServer��ȥ
		mbs.registerMBean(new HelloWorld(), helloName);

		// Distributed Layer,
		// �ṩ��һ��HtmlAdaptor��֧��Http����Э�飬������һ�������HTML���棬�����Hello�����������ΪԶ�˹���Ľ���
		// ��ʵ��HtmlAdaptor��һ���򵥵�HttpServer������Http����ת��ΪJMX Agent������
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
}
