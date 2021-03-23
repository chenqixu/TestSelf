package com.cqx.calcite.parser.bean;

/**
 * clause
 *
 * @author chenqixu
 */
public class Clause {
    private I_clause self;
    private I_clause parent;

    public Clause(I_clause self) {
        this.self = self;
    }

    public I_clause getSelf() {
        return self;
    }

    public void setSelf(I_clause self) {
        this.self = self;
    }

    public I_clause getParent() {
        return parent;
    }

    public void setParent(I_clause parent) {
        this.parent = parent;
    }
}
