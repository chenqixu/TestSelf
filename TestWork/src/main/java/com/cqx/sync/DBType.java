package com.cqx.sync;

/**
 * DBType
 *
 * @author chenqixu
 */
public enum DBType {
    MYSQL("com.mysql.jdbc.Driver"),
    ORACLE("oracle.jdbc.driver.OracleDriver"),
    ;

    private String driver;

    DBType(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return driver;
    }
}
