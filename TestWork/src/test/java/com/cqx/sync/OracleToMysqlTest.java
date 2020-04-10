package com.cqx.sync;

import com.cqx.sync.bean.DBBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class OracleToMysqlTest {

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
        oracleToMysql.release();
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
    public void getAllTableAndFields() throws SQLException {
        oracleToMysql.getAllTableAndFields();
    }

}