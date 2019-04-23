package com.mr.bean;

/**
 * DBBean
 *
 * @author chenqixu
 */
public class DBBean {
    private DBType dbType;
    private String dbUrl;
    private String userName;
    private String passwd;

    public static DBBean newbuilder() {
        return new DBBean();
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public DBBean setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public DBBean setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPasswd() {
        return passwd;
    }

    public DBBean setPasswd(String passwd) {
        this.passwd = passwd;
        return this;
    }

    public DBType getDbType() {
        return dbType;
    }

    public DBBean setDbType(DBType dbType) {
        this.dbType = dbType;
        return this;
    }
}
