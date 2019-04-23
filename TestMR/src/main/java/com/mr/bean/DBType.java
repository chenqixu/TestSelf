package com.mr.bean;

/**
 * DBType
 *
 * @author chenqixu
 */
public enum DBType {
    ORACLE("oracle.jdbc.driver.OracleDriver"),
    MYSQL("com.mysql.jdbc.Driver"),
    ;

    private final String driverClass;

    DBType(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return this.driverClass;
    }
}
