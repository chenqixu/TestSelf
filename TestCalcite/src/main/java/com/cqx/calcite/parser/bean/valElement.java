package com.cqx.calcite.parser.bean;

import java.util.List;

/**
 * valElement
 *
 * @author chenqixu
 */
public class valElement implements I_clause<String> {
    private String val;
    private operationElement parent;

    public valElement(String s) {
        this.val = s;
    }

    @Override
    public String getVal() {
        return this.val;
    }

    @Override
    public void setVal(String s) {
        this.val = s;
    }

    @Override
    public operationElement getParent() {
        return parent;
    }

    @Override
    public void setParent(operationElement parent) {
        this.parent = parent;
    }

    @Override
    public List<I_clause> getChilds() {
        return null;
    }
}
