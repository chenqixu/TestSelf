package com.cqx.work.monitor;

public enum UserRuleTypeEnum {
    add,
    delete,
    ;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
