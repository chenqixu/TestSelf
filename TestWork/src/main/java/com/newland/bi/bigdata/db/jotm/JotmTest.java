package com.newland.bi.bigdata.db.jotm;

import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;

import javax.naming.Context;
import javax.sql.XAConnection;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JotmTest {
	public static String getStatusName(int status) {
		String statusName = null;
		try {
			Field[] flds = Status.class.getDeclaredFields();
			for (int i = 0; i < flds.length; i++) {
				if (flds[i].getInt(null) == status) {
					statusName = flds[i].getName();
					break;
				}
			}
		} catch (Exception e) {
			statusName = "invalid status value!";
		}
		return statusName;
	}

	public static Connection getConnection(TransactionManager tm, String url, String user,
			String password) throws SQLException {
		StandardXADataSource standardXADataSource = new StandardXADataSource();
		standardXADataSource.setDriverName("oracle.jdbc.driver.OracleDriver");
		standardXADataSource.setUrl(url);
		standardXADataSource.setTransactionManager(tm);
		XAConnection xaconn = standardXADataSource.getXAConnection(user, password);
		return xaconn.getConnection();
	}

	public static void main(String[] a) throws Exception {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory");
		System.setProperty(Context.PROVIDER_URL, "rmi://10.1.4.185:1099");
		String dbURL1= "jdbc:oracle:thin:@10.1.8.79:1521/edc_etl_pri";
		String dbURL2= "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";	

		Jotm jotm = new Jotm(false, false);
		UserTransaction utx = jotm.getUserTransaction();

		System.out.println("not begin:"+getStatusName(utx.getStatus()));
		utx.begin();
		System.out.println("begin:"+getStatusName(utx.getStatus()));
		try {
			Connection conn = getConnection(jotm.getTransactionManager(),
					dbURL1, "edc_etl_col", "edc_etl_col");
			conn.setAutoCommit(false);
			Statement statement = conn.createStatement();
			statement.execute("insert into tmp_cqx(msisdn) values('315')");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Connection conn2 = getConnection(jotm.getTransactionManager(),
					dbURL2, "bishow", "bishow");
			conn2.setAutoCommit(false);
			Statement statement = conn2.createStatement();
			statement.execute("insert into tmp_cqx(msisdn) values('315')");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		utx.commit();
		utx.rollback();
		System.out.println("commit/rollbak:"+getStatusName(utx.getStatus()));

		jotm.stop();
	}
}
