package com.cqx.jmx.util;

import com.cqx.jmx.JMXClientFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class JMXClientFactory {
    private static Logger logger = LoggerFactory.getLogger(JMXClientFactory.class);
    private static JMXClientFactory jmxcf = new JMXClientFactory();
    private static Map<JMXClientFactoryBean, JMXClientUtil> clientUtilMap = new HashMap<>();

    private JMXClientFactory() {
    }

    public static synchronized JMXClientUtil getJMXClientUtil(JMXClientFactoryBean jmxClientFactoryBean) {
        JMXClientUtil jmxClientUtil = clientUtilMap.get(jmxClientFactoryBean);
        if (jmxClientUtil == null) {
            jmxClientUtil = jmxcf.new JMXClientUtil();
            clientUtilMap.put(jmxClientFactoryBean, jmxClientUtil);
        }
        return jmxClientUtil;
    }

    /**
     * 默认使用1099端口
     */
    public static JMXClientUtil startJMXClient(String objectname) {
        String ip = "127.0.0.1";
        int rmiPort = 1099;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        JMXClientFactoryBean jmxClientFactoryBean = JMXClientFactoryBean.builder()
                .setObjectname(objectname).setIp(ip).setRmiPort(rmiPort);
        startJMXClient(jmxClientFactoryBean);
        return getJMXClientUtil(jmxClientFactoryBean);
    }

    public static JMXClientUtil startJMXClient(String objectname, int rmiPort) {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        JMXClientFactoryBean jmxClientFactoryBean = JMXClientFactoryBean.builder()
                .setObjectname(objectname).setIp(ip).setRmiPort(rmiPort);
        startJMXClient(jmxClientFactoryBean);
        return getJMXClientUtil(jmxClientFactoryBean);
    }

    public static JMXClientUtil startJMXClient(String objectname, String ip, int rmiPort) {
        JMXClientFactoryBean jmxClientFactoryBean = JMXClientFactoryBean.builder()
                .setObjectname(objectname).setIp(ip).setRmiPort(rmiPort);
        startJMXClient(jmxClientFactoryBean);
        return getJMXClientUtil(jmxClientFactoryBean);
    }

    public static void startJMXClient(JMXClientFactoryBean jmxClientFactoryBean) {
        getJMXClientUtil(jmxClientFactoryBean).startJMXClient(jmxClientFactoryBean);
    }

    private class JMXClientUtil implements IJMXClient {
        JMXServiceURL url;
        MBeanServerConnection server;
        ObjectName mbeanName;

        private JMXClientUtil() {
        }

        private void startJMXClient(String objectname, int rmiPort) {
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":"
                        + rmiPort + "/" + objectname + "MBean");
                JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                server = jmxc.getMBeanServerConnection();
                mbeanName = new ObjectName(objectname + "MBean:name=" + objectname);
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (MalformedObjectNameException e) {
                logger.error(e.getMessage(), e);
            }
        }

        private void startJMXClient(JMXClientFactoryBean jmxClientFactoryBean) {
            String objectname = jmxClientFactoryBean.getObjectname();
            String ip = jmxClientFactoryBean.getIp();
            int rmiPort = jmxClientFactoryBean.getRmiPort();
            try {
                url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":"
                        + rmiPort + "/" + objectname + "MBean");
                JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                server = jmxc.getMBeanServerConnection();
                mbeanName = new ObjectName(objectname + "MBean:name=" + objectname);
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (MalformedObjectNameException e) {
                logger.error(e.getMessage(), e);
            }
        }

        private void startJMXClient(String objectname, String ip, int rmiPort) {
            try {
                url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":"
                        + rmiPort + "/" + objectname + "MBean");
                JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
                server = jmxc.getMBeanServerConnection();
                mbeanName = new ObjectName(objectname + "MBean:name=" + objectname);
            } catch (MalformedURLException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (MalformedObjectNameException e) {
                logger.error(e.getMessage(), e);
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T getAttributeByName(String attribute) {
            T t = null;
            if (server != null && mbeanName != null) {
                try {
                    t = (T) server.getAttribute(mbeanName, attribute);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return t;
        }

        public <T> void setAttributeByName(String attribute, String type, T t) {
            if (server != null && mbeanName != null) {
                try {
                    server.invoke(mbeanName, attribute, new Object[]{t}, new String[]{type});
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        public <T> T invoke(String operationName, Object[] params, String[] signature) {
            T t = null;
            if (server != null && mbeanName != null) {
                try {
                    t = (T) server.invoke(mbeanName, operationName, params, signature);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            return t;
        }
    }
}
