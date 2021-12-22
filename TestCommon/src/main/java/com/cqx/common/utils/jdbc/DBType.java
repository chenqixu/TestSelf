package com.cqx.common.utils.jdbc;

/**
 * DBType
 *
 * @author chenqixu
 */
public enum DBType {
    MYSQL("com.mysql.jdbc.Driver", "select 1"),
    MYSQL8("com.mysql.cj.jdbc.Driver", "select 1"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "select 1 from dual"),
    POSTGRESQL("org.postgresql.Driver", "select 1"),
    DERBY_LOCAL("org.apache.derby.jdbc.EmbeddedDriver", "values 1"),
    DERBY_NET("org.apache.derby.jdbc.ClientDriver", "values 1"),
    HIVE1("org.apache.hadoop.hive.jdbc.HiveDriver", "select 1"),
    HIVE3("org.apache.hive.jdbc.HiveDriver", "select 1"),
    IMPALA("com.cloudera.impala.jdbc41.Driver", "select 1 from dual"),
    GSDB("fjlz.gsdb.jdbc.GsdbDriver", "select 1 from dual"),
    OB_ORACLE("com.alipay.oceanbase.jdbc.Driver", "select 1 from dual"),
    OB_MYSQL(MYSQL),
    OTHER("", ""),
    ;

    private String driver;
    private String validation_query;

    DBType(String driver) {
        this.driver = driver;
    }

    DBType(DBType dbType) {
        this(dbType.getDriver(), dbType.getValidation_query());
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
