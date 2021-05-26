package com.cqx.common.model.filter;

/**
 * DataFilterActionEnum
 *
 * @author chenqixu
 */
public enum DataFilterActionEnum {
    FILE_ACTION("FILE_ACTION"),
    MEMORY_ACTION("MEMORY_ACTION"),
    ;

    private String action;

    DataFilterActionEnum(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}

