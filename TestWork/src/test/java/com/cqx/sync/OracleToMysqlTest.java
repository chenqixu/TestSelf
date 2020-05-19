package com.cqx.sync;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class OracleToMysqlTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(OracleToMysqlTest.class);
    private OracleToMysql oracleToMysql;

    @Before
    public void setUp() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("edc_addressquery");
        dbBean.setPass_word("edc_addressquery");
        dbBean.setPool(false);
        oracleToMysql = new OracleToMysql(dbBean);
    }

    @After
    public void tearDown() throws Exception {
        if (oracleToMysql != null) oracleToMysql.release();
    }

    @Test
    public void getAllTable() throws SQLException {
        oracleToMysql.getAllTable();
    }

    @Test
    public void descTable() throws SQLException {
        oracleToMysql.descTable("SM2_PASSWD_CODE");
    }

    @Test
    public void getTableFields() throws SQLException {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.205:1521:cbass");
        dbBean.setUser_name("xdcdr");
        dbBean.setPass_word("xdcdr");
        dbBean.setPool(false);
        oracleToMysql = new OracleToMysql(dbBean);
        String fields = oracleToMysql.getTableFields("CQX_CCS_USER");
        logger.info("CQX_CCS_USER：{}", fields.toLowerCase());
        fields = oracleToMysql.getTableFields("CQX_CCS_USER");
        logger.info("CQX_CSS_BROADBAND_RESERVATION：{}", fields.toLowerCase());
    }

    @Test
    public void getAllTableAndFields() throws SQLException {
        oracleToMysql.getAllTableAndFields();
    }

}