package com.cqx.common.utils.jdbc;

/**
 * DBType
 *
 * @author chenqixu
 */
public enum DBType {
    MYSQL("com.mysql.jdbc.Driver", "select 1"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "select 1 from dual"),
    POSTGRESQL("org.postgresql.Driver", "select 1"),
    DERBY("org.apache.derby.jdbc.EmbeddedDriver", "values 1"),
    HIVE1("org.apache.hadoop.hive.jdbc.HiveDriver", "select 1"),
    HIVE3("org.apache.hive.jdbc.HiveDriver", "select 1"),
    OTHER("", ""),
    ;

    private String driver;
    private String validation_query;

    DBType(String driver) {
        this.driver = driver;
    }

    DBType(String driver, String validation_query) {
        this.driver = driver;
        this.validation_query = validation_query;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getValidation_query() {
        return validation_query;
    }

    public void setValidation_query(String validation_query) {
        this.validation_query = validation_query;
    }
}
