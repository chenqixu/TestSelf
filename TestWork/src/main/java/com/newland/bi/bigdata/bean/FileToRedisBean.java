package com.newland.bi.bigdata.bean;


import com.cqx.common.annotation.BeanDesc;
import com.cqx.common.bean.javabean.BaseBean;

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
    @BeanDesc(value = "扫描序列")
    private int scan_seq;
    @BeanDesc(value = "起始")
    private long start_cnt;
    @BeanDesc(value = "结束")
    private Long end_cnt;
    @BeanDesc(value = "计数器")
    private Integer cnt;

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

    public int getScan_seq() {
        return scan_seq;
    }

    public void setScan_seq(int scan_seq) {
        this.scan_seq = scan_seq;
    }

    public long getStart_cnt() {
        return start_cnt;
    }

    public void setStart_cnt(long start_cnt) {
        this.start_cnt = start_cnt;
    }

    public Long getEnd_cnt() {
        return end_cnt;
    }

    public void setEnd_cnt(Long end_cnt) {
        this.end_cnt = end_cnt;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }
}
