package com.cqx.common.utils.jdbc;

import com.cqx.common.test.TestBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * DM8Test
 *
 * @author chenqixu
 */
public class DM8Test extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(DM8Test.class);

    /**
     * 验证SPI接口
     */
    @Test
    public void loaderDriversTest() {
        // 代码参考 java.sql.DriverManager类loadInitialDrivers()
        ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
        Iterator<Driver> driversIterator = loadedDrivers.iterator();
        try {
            while (driversIterator.hasNext()) {
                Driver driver = driversIterator.next();
                System.out.println(driver);
            }
        } catch (Throwable t) {
            // Do nothing
        }
    }

    /**
     * 达梦驱动验证
     *
     * @throws IOException
     * @throws SQLException
     */
    @Test
    public void dm8() throws IOException, SQLException {
        Map params = getParam("jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
        DBBean dbBean = paramsParserUtil.getBeanMap().get("dm8_204sysdba_Bean");
        Properties props = new Properties();
        props.put("user", dbBean.getUser_name());
        props.put("password", dbBean.getPass_word());
        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            logger.info("Element: {}", e.nextElement());
        }
        try (Connection _conn = DriverManager.getConnection(dbBean.getTns(), props)) {
            logger.info("conn: {}", _conn);
        }
    }
}
