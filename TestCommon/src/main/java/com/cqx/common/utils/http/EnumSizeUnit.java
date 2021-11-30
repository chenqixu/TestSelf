package com.cqx.common.utils.http;

/**
 * 大小单位
 *
 * @author chenqixu
 */
public enum EnumSizeUnit {
    BYTE(1L, "byte"),
    KB(1024L, "kb"),
    MB(1024L * 1024L, "mb"),
    GB(1024L * 1024L * 1024L, "gb"),
    TB(1024L * 1024L * 1024L * 1024L, "tb"),
    PB(1024L * 1024L * 1024L * 1024L * 1024L, "pb"),
    ;

    private long divisor;
    private String name;

    EnumSizeUnit(long divisor, String name) {
        this.divisor = divisor;
        this.name = name;
    }

    public long getDivisor() {
        return divisor;
    }

    public String getName() {
        return name;
    }
}
