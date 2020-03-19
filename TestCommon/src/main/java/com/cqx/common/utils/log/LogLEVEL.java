package com.cqx.common.utils.log;

/**
 * LogLEVEL
 *
 * @author chenqixu
 */
public enum LogLEVEL {
    ERROR(0, "ERROR"),
    WARN(1, "WARN"),
    INFO(2, "INFO"),
    DEBUG(3, "DEBUG");

    private int level;
    private String desc;

    LogLEVEL(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }}
