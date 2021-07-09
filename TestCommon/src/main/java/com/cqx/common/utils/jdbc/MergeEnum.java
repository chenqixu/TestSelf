package com.cqx.common.utils.jdbc;

/**
 * MergeEnum
 *
 * @author chenqixu
 */
public enum MergeEnum {
    MERGE_INTO_ONLY("MERGE_INTO_ONLY"),
    MERGE_INTO_UPDATE("MERGE_INTO_UPDATE"),
    ;

    private String type;

    private MergeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
