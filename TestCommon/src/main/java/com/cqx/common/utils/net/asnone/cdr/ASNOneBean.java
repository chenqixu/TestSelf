package com.cqx.common.utils.net.asnone.cdr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ASNOneBean
 *
 * @author chenqixu
 */
public class ASNOneBean {
    private int tagNo;
    private String name;
    private ASNOneRule asnOneRule;
    private byte[] value;
    private String calcValue;
    private Map<Integer, ASNOneBean> child = new HashMap<>();
    private boolean root = false;
    private boolean leaf = false;

    public ASNOneBean() {
    }

    public ASNOneBean(boolean root) {
        this.root = root;
    }

    public ASNOneBean(ASNOneBean copy) {
        setTagNo(copy.getTagNo());
        setName(copy.getName());
        setAsnOneRule(copy.getAsnOneRule());
        setLeaf(copy.isLeaf());
        for (Map.Entry<Integer, ASNOneBean> entry : copy.childEntrySet()) {
            addChild(new ASNOneBean(entry.getValue()));
        }
    }

    public String printTree() {
        StringBuilder sb = new StringBuilder();
        _printTree(this, sb, 0);
        return sb.toString();
    }

    private void _printTree(ASNOneBean child, StringBuilder sb, int num) {
        if (child.isLeaf()) {
            if (child.getCalcValue() != null) {
                sb.append("\n");
                for (int i = 0; i < num; i++) {
                    sb.append("    ");
                }
                sb.append(String.format("%s [%s] %s", child.getName(), child.getTagNo(), child.getCalcValue()));
            }
        } else {
            sb.append("\n");
            for (int i = 0; i < num; i++) {
                sb.append("    ");
            }
            sb.append(String.format("%s [%s]", child.getName(), child.getTagNo()));
            num++;
            for (Map.Entry<Integer, ASNOneBean> entry : child.childEntrySet()) {
                _printTree(entry.getValue(), sb, num);
            }
        }
    }

    public Set<Map.Entry<Integer, ASNOneBean>> childEntrySet() {
        return child.entrySet();
    }

    public boolean hasChild() {
        return child.size() > 0;
    }

    public void addChild(ASNOneBean asnOneBean) {
        child.put(asnOneBean.getTagNo(), asnOneBean);
    }

    public ASNOneBean getChild(Integer tagNo) {
        return child.get(tagNo);
    }

    public int getTagNo() {
        return tagNo;
    }

    public void setTagNo(int tagNo) {
        this.tagNo = tagNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ASNOneRule getAsnOneRule() {
        return asnOneRule;
    }

    public void setAsnOneRule(ASNOneRule asnOneRule) {
        this.asnOneRule = asnOneRule;
    }

    public Map<Integer, ASNOneBean> getChild() {
        return child;
    }

    public void setChild(Map<Integer, ASNOneBean> child) {
        this.child = child;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getCalcValue() {
        return calcValue;
    }

    public void calcValue() throws Exception {
        if (asnOneRule != null && value != null) {
            calcValue = asnOneRule.parse(value);
        }
    }
}
