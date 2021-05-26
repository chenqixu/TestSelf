package com.cqx.common.utils.file;

/**
 * RAFBeanEnum
 *
 * @author chenqixu
 */
public enum RAFBeanEnum {
    CONTENT("CONTENT"),
    END("END"),
    ;
    private String code;

    RAFBeanEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }}
