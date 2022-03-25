package com.cqx.common.utils.jdbc.c3p0;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class C3p0UtilTest {

    @Test
    public void getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = C3p0Util.getInstance().getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from tabs");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
//            boolean ret = st.execute("update b set bid=7 where bname='xxx'");
//            System.out.println(String.format("conn: %s, ret: %s", conn, ret));
        } finally {
            if (conn != null) conn.close();
        }
    }
}