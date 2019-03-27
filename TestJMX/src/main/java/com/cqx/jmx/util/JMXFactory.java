package com.cqx.jmx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;

public class JMXFactory {
    private static Logger logger = LoggerFactory.getLogger(JMXFactory.class);
    private static JMXFactory jmxf = new JMXFactory();

    private JMXFactory() {
    }

    public static JMXUtil getJMXUtil() {
        return jmxf.new JMXUtil();
    }

    /**
     * 默认使用1099端口
     */
    public static void startJMX(String objectname, Object obj) {
        getJMXUtil().startJMX(objectname, obj, 1099);
    }

    public static void startJMX(String objectname, Object obj, int rmiPort) {
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
                        "service:jmx:rmi:///jndi/rmi://" + ip + ":" + rmiPort + "/" + objectname + "MBean");
                JMXConnectorServer jmxConnector = JMXConnectorServerFactory
                        .newJMXConnectorServer(url, null, mbs);
                jmxConnector.start();
                logger.info("[" + objectname + "] jmx is start.");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
