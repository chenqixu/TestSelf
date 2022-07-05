package com.cqx.common.utils.net.asnone;

/**
 * 位6代表该数据单元是原子式还是结构类型
 *
 * @author chenqixu
 */
public enum EnumTagType {
    /**
     * 原子式，代表该域采用ASN最小编码单元编码
     */
    Primitive("0"),
    /**
     * 结构类型，代表该域由多个ASN最小编码单元组成
     */
    Construct("1"),
    ;

    private String typeValue;

    EnumTagType(String typeValue) {
        this.typeValue = typeValue;
    }

    public static EnumTagType valueOfByValue(String typeValue) {
        switch (typeValue) {
            case "0":
                return Primitive;
            case "1":
                return Construct;
            default:
                return null;
        }
    }

    public String getTypeValue() {
        return this.typeValue;
    }
}
