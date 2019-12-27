package com.newland.bi.bigdata.bean;


import com.newland.bi.bigdata.annotation.BeanDesc;

/**
 * FileToRedisBean
 *
 * @author chenqixu
 */
public class FileToRedisBean extends BaseBean {
    @BeanDesc(value = "扫描路径")
    private String scan_path;
    @BeanDesc(value = "扫描规则")
    private String scan_rule;

    public String getScan_path() {
        return scan_path;
    }

    public void setScan_path(String scan_path) {
        this.scan_path = scan_path;
    }

    public String getScan_rule() {
        return scan_rule;
    }

    public void setScan_rule(String scan_rule) {
        this.scan_rule = scan_rule;
    }
}
