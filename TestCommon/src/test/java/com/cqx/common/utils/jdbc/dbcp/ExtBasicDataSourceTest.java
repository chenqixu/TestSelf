package com.cqx.common.utils.jdbc.dbcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ExtBasicDataSourceTest {
    private static final Logger logger = LoggerFactory.getLogger(ExtBasicDataSourceTest.class);
    private ExtBasicDataSource extBasicDataSource;

    @Before
    public void setUp() throws Exception {
        extBasicDataSource = new ExtBasicDataSource();
        extBasicDataSource.init();
    }

    @After
    public void tearDown() throws Exception {
        if (extBasicDataSource != null) extBasicDataSource.close();
    }

    @Test
    public void getExtConnection() throws SQLException {
        extBasicDataSource.getExtConnection();
    }
}