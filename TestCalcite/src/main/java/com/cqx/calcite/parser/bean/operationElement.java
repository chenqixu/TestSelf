package com.cqx.calcite.parser.bean;

import com.cqx.calcite.parser.operation.I_operation;

import java.util.ArrayList;
import java.util.List;

/**
 * operationElement
 *
 * @author chenqixu
 */
public class operationElement implements I_clause<I_operation> {
    private I_operation val;
    private operationElement parent;
    private List<I_clause> childs = new ArrayList<>();

    public operationElement(I_operation operation) {
        this.val = operation;
    }

    @Override
    public String toString() {
        return "【operationElement】this：" + val.getKey() +
                "，parent：" + (parent != null ? parent.getVal().getKey() : "null");
    }

    @Override
    public I_operation getVal() {
        return this.val;
    }

    @Override
    public void setVal(I_operation operation) {
        this.val = operation;
    }

    public void addChild(I_clause clause) {
        childs.add(clause);
    }

    @Override
    public operationElement getParent() {
        return parent;
    }

    @Override
    public void setParent(operationElement parent) {
        this.parent = parent;
        this.parent.addChild(this);
    }

    @Override
    public List<I_clause> getChilds() {
        return childs;
    }
}
