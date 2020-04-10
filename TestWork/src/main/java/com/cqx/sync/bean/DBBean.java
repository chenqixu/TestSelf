package com.cqx.sync.bean;

import com.cqx.sync.DBType;

/**
 * DBBean
 *
 * @author chenqixu
 */
public class DBBean {
    private String user_name;
    private String pass_word;
    private String tns;
    private DBType dbType;
    private boolean isPool = true;//默认走连接池

    @Override
    public String toString() {
        return "DBType：" + dbType + "，tns：" + tns + "，user_name：" + user_name + "，pass_word：" + pass_word + "，isPool：" + isPool;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPass_word() {
        return pass_word;
    }

    public void setPass_word(String pass_word) {
        this.pass_word = pass_word;
    }

    public String getTns() {
        return tns;
    }

    public void setTns(String tns) {
        this.tns = tns;
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
    }

    public boolean isPool() {
        return isPool;
    }

    public void setPool(boolean pool) {
        isPool = pool;
    }
}
