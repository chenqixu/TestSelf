package com.cqx.jmx;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.jmx.util.IJMXClient;
import com.cqx.jmx.util.JMXClientFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class HelloClient {
    private static MyLogger log = MyLoggerFactory.getLogger(HelloClient.class);

    public static void main(String[] args) {
//		new HelloClient().start("HelloWorld");
        new HelloClient().startClient("HelloWorld");
    }

    public void start(String beanname) {
        try {
            // 如果agent不做配置的话，默认jndi path为jmxrmi
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/" + beanname + "MBean");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection server = jmxc.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName(beanname + "MBean:name=" + beanname);

            // 访问paused属性
            boolean paused = (Boolean) server.getAttribute(mbeanName, "Paused");
            System.out.println("[first]" + paused);
            // 调用pause方法
            if (!paused) {
                server.invoke(mbeanName, "pause", new Object[]{true}, new String[]{"boolean"});
            } else {
                server.invoke(mbeanName, "pause", new Object[]{false}, new String[]{"boolean"});
            }
            paused = (Boolean) server.getAttribute(mbeanName, "Paused");
            System.out.println("[second]" + paused);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startClient(String beanname) {
        IJMXClient jmxClientUtil = JMXClientFactory.startJMXClient(beanname);
        boolean paused = jmxClientUtil.getAttributeByName("Paused");
        log.info(paused + "");
        jmxClientUtil.setAttributeByName("pause", "boolean", !paused);
        paused = jmxClientUtil.getAttributeByName("Paused");
        log.info(paused + "");
        String Greeting = jmxClientUtil.getAttributeByName("Greeting");
        log.info(Greeting + "");
        String result = jmxClientUtil.invoke("exec", new Object[]{"sh test.sh"}, new String[]{"java.lang.String"});
        log.info(result + "");
    }
}
