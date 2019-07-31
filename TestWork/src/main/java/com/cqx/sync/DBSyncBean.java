package com.cqx.sync;

/**
 * DBSyncBean
 *
 * @author chenqixu
 */
public class DBSyncBean {
    private String src_tab_name;
    private String dst_tab_name;
    private String src_field;
    private String dst_field;
    private DBSyncType dbSyncType;
    private String condition;

    public String getSrc_tab_name() {
        return src_tab_name;
    }

    public void setSrc_tab_name(String src_tab_name) {
        this.src_tab_name = src_tab_name;
    }

    public String getDst_tab_name() {
        return dst_tab_name;
    }

    public void setDst_tab_name(String dst_tab_name) {
        this.dst_tab_name = dst_tab_name;
    }

    public String getSrc_field() {
        return src_field;
    }

    public void setSrc_field(String src_field) {
        this.src_field = src_field;
    }

    public String getDst_field() {
        return dst_field;
    }

    public void setDst_field(String dst_field) {
        this.dst_field = dst_field;
    }

    public DBSyncType getDbSyncType() {
        return dbSyncType;
    }

    public void setDbSyncType(DBSyncType dbSyncType) {
        this.dbSyncType = dbSyncType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
