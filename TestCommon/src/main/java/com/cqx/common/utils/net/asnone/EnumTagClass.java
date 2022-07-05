package com.cqx.common.utils.net.asnone;

/**
 * 位8和位7代表数据的类型标记
 *
 * @author chenqixu
 */
public enum EnumTagClass {
    /**
     * 为基本类型进行分配TAG值用的
     */
    Universal("00"),
    /**
     * 很少用到
     */
    Application("01"),
    /**
     * 定义结构类型时使用
     */
    ContexSpecific("10"),
    /**
     * 私有的，可以根据具体协商而定
     */
    Private("11"),
    ;

    private String classValue;

    EnumTagClass(String classValue) {
        this.classValue = classValue;
    }

    public static EnumTagClass valueOfByValue(String classValue) {
        switch (classValue) {
            case "00":
                return Universal;
            case "01":
                return Application;
            case "10":
                return ContexSpecific;
            case "11":
                return Private;
            default:
                return null;
        }
    }

    public String getClassValue() {
        return this.classValue;
    }
}
