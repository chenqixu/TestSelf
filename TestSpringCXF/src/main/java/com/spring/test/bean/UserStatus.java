package com.spring.test.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UserStatus implements RowMapper<UserStatus> {
	private String msisdn;
	private String lacci;
	private String uptime;
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getLacci() {
		return lacci;
	}
	public void setLacci(String lacci) {
		this.lacci = lacci;
	}
	public String getUptime() {
		return uptime;
	}
	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
	@Override
	public UserStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserStatus us = new UserStatus();
		us.setLacci(rs.getString("lacci"));
		us.setMsisdn(rs.getString("msisdn"));
		us.setUptime(rs.getString("uptime"));
		return us;
	}
}
