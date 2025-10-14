package com.bussiness.bi.bigdata.label.rule;

import java.util.Map;

/**
 * 控制层级
 *
 * @author chenqixu
 */
public class SubResultBean {
    private SubTypeBean stb1;
    private SubTypeBean stb2;
    private SubResultBean s1;
    private SubResultBean s2;
    private String op;
    private boolean result;
    private String name;
    private boolean isLeaf;
    private int seq;

    public SubResultBean() {
    }

    public SubResultBean(SubTypeBean stb1, SubTypeBean stb2, String op, boolean result, int seq) {
        this.stb1 = stb1;
        this.stb2 = stb2;
        this.op = op;
        this.result = result;
        this.seq = seq;
        this.name = "操作层" + seq;
        this.isLeaf = true;
    }

    public SubResultBean(SubResultBean s1, SubResultBean s2, String op, boolean result, int seq) {
        this.s1 = s1;
        this.s2 = s2;
        this.op = op;
        this.result = result;
        this.seq = seq;
        this.name = "操作层" + seq;
        this.isLeaf = false;
    }

    public String getLeaf() {
        return String.format("%s[%s] %s %s = %s", stb1.getName(), stb1.getVal(), op, stb2.getVal(), result);
    }

    public String getLeaf(Map<String, String> tagMap) {
        return String.format("[规则]%s %s %s [用户标签值]%s [用户标签值代入规则计算结果]%s"
                , tagMap.get(stb1.getName()), RuleUtil.parseAction(op), stb2.getVal(), stb1.getVal(), result);
    }

    public String getLevelAndRs() {
        return String.format("[%s%s]%s", getName(), isLeaf() ? "叶子" : "非叶子", isResult());
    }

    public String getParent() {
        return String.format("(%s) %s (%s) = %s", s1.getLevelAndRs(), op, s2.getLevelAndRs(), isResult());
    }

    public String getLevel() {
        return String.format("[%s%s]", getName(), isLeaf() ? "叶子" : "非叶子");
    }

    public SubResultBean getS1() {
        return s1;
    }

    public void setS1(SubResultBean s1) {
        this.s1 = s1;
    }

    public SubResultBean getS2() {
        return s2;
    }

    public void setS2(SubResultBean s2) {
        this.s2 = s2;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public SubTypeBean getStb1() {
        return stb1;
    }

    public void setStb1(SubTypeBean stb1) {
        this.stb1 = stb1;
    }

    public SubTypeBean getStb2() {
        return stb2;
    }

    public void setStb2(SubTypeBean stb2) {
        this.stb2 = stb2;
    }
}
