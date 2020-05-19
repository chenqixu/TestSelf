package com.cqx.sync;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParserSQLFileTest {

    private ParserSQLFile parserSQLFile;

    @Before
    public void setUp() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.205:1521:cbass");
        dbBean.setUser_name("xdcdr");
        dbBean.setPass_word("xdcdr");
        dbBean.setPool(false);

        String file_name = "d:\\Work\\ETL\\OGG\\data\\sss1.sql";

        parserSQLFile = new ParserSQLFile(dbBean, file_name);
    }

    @After
    public void tearDown() throws Exception {
        if (parserSQLFile != null) parserSQLFile.release();
    }

    @Test
    public void readAndExec() throws Exception {
        parserSQLFile.readAndExec();
    }
}