package com.cqx.common.utils.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * DBBean
 *
 * @author chenqixu
 */
public class DBBean {
    private String name;
    private String user_name;
    private String pass_word;
    private String tns;
    private DBType dbType;
    private boolean isPool = true;//默认走连接池

    public static DBBean newbuilder() {
        return new DBBean();
    }

    public static List<DBBean> parser(Object param) {
        List<Map<String, ?>> parser = (ArrayList<Map<String, ?>>) param;
        List<DBBean> result = new ArrayList<>();
        for (Map<String, ?> map : parser) {
            result.add(DBBean.newbuilder().parserMap(map));
        }
        return result;
    }

    public DBBean parserMap(Map<String, ?> param) {
        // 解析参数
        setName((String) param.get("name"));
        setUser_name((String) param.get("user_name"));
        setPass_word((String) param.get("pass_word"));
        // tns：如果是List需要随机取一个，如果是String就直接Set
        Object tns = param.get("tns");
        if (tns.getClass().equals(java.util.ArrayList.class)) {
            List<String> tnsList = (List<String>) param.get("tns");
            Random random = new Random();
            setTns(tnsList.get(random.nextInt(tnsList.size())));
        } else if (tns.getClass().equals(java.lang.String.class)) {
            setTns((String) param.get("tns"));
        }
        // dbType：可能为空，需要解析driver和validation_query
        String _dbType_Str = (String) param.get("dbType");
        if (_dbType_Str == null) {
            DBType _dbType = DBType.valueOf("OTHER");
            String _driver = (String) param.get("driver");
            String _validation_query = (String) param.get("validation_query");
            _dbType.setDriver(_driver);
            _dbType.setValidation_query(_validation_query);
            setDbType(_dbType);
        } else {
            setDbType(DBType.valueOf((String) param.get("dbType")));
        }
        return this;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
