package com.cqx.common.utils.jdbc.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * c3p0工具<br>
 * c3p0默认会去读取class下的配置c3p0-config.xml
 *
 * @author chenqixu
 */
public class C3p0Util {
    private static final Logger logger = LoggerFactory.getLogger(C3p0Util.class);
    private static final String FILENAME = "jdbc.xml"; // "c3p0-config.xml";
    private static C3p0Util c3p0Util;
    private ComboPooledDataSource c3p0;

    private C3p0Util() {
        try {
            // Get file from resources folder
            ClassLoader classLoader = C3p0Util.class.getClassLoader();
            URL fileURL = classLoader.getResource(FILENAME);
            if (fileURL != null) {
                File file = new File(fileURL.getFile());

                //创建一个读取XML文件的对象
                SAXReader reader = new SAXReader();
                //创建一个文档对象
                Document document = reader.read(file);
                //获取文件的根节点
                Element element = document.getRootElement();

                //获取节点元素
                String driverClass = element.elementText("driverClass");
                String url = element.elementText("url");
                String userName = element.elementText("userName");
                String password = element.elementText("password");

                c3p0 = new ComboPooledDataSource();
                c3p0.setDriverClass(driverClass);
                c3p0.setJdbcUrl(url);
                c3p0.setUser(userName);
                c3p0.setPassword(password);
                logger.info("c3po，driverClass：{}，url：{}，userName：{}，password：{}", driverClass, url, userName, password);
                // 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3
                c3p0.setAcquireIncrement(3);
                // 初始化时获取三个连接，取值应在minPoolSize与maxPoolSize之间。Default: 3
                c3p0.setInitialPoolSize(3);
                // 每60秒检查所有连接池中的空闲连接。Default: 0
                c3p0.setIdleConnectionTestPeriod(30);
                // -连接池中保留的最小连接数。
                c3p0.setMinPoolSize(1);
                // 连接池中保留的最大连接数。Default: 15
                c3p0.setMaxPoolSize(15);
                // JDBC的标准参数，用以控制数据源内加载的PreparedStatements数量。但由于预缓存的statements
                // 属于单个connection而不是整个连接池。所以设置这个参数需要考虑到多方面的因素。
                // 如果maxStatements与maxStatementsPerConnection均为0，则缓存被关闭。Default: 0
                c3p0.setMaxStatements(0);
                // c3p0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能
                // 通过多线程实现多个操作同时被执行。Default: 3
                c3p0.setNumHelperThreads(3);
                // 最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0
                c3p0.setMaxIdleTime(60);
                c3p0.setTestConnectionOnCheckin(true);
                c3p0.setTestConnectionOnCheckout(false);
                c3p0.setPreferredTestQuery("select 1 from dual");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static C3p0Util getInstance() {
        if (c3p0Util == null) {
            c3p0Util = new C3p0Util();
            return c3p0Util;
        } else {
            return c3p0Util;
        }
    }

    public Connection getConnection() throws SQLException {
        logger.info("getConnection.c3p0：{}", c3p0);
        return this.c3p0.getConnection();
    }
}
