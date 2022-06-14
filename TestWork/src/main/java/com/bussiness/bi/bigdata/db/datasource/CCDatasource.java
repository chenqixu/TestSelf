package com.bussiness.bi.bigdata.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * <pre>
 * 
 * </pre>
 * */
public class CCDatasource implements DataSource {
	
	private String driverclassname;
	private String username;
	private String password;
	private String url;
	private int maxactive;
	private int minidle;
	private int maxidle;
	private boolean closed;	
	
	public String getDriverclassname() {
		return driverclassname;
	}

	public void setDriverClassName(String driverclassname) {
		this.driverclassname = driverclassname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getMaxactive() {
		return maxactive;
	}

	public void setMaxActive(int maxactive) {
		this.maxactive = maxactive;
	}

	public int getMinidle() {
		return minidle;
	}

	public void setMinIdle(int minidle) {
		this.minidle = minidle;
	}

	public int getMaxidle() {
		return maxidle;
	}

	public void setMaxIdle(int maxidle) {
		this.maxidle = maxidle;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public CCDatasource(){
		
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
