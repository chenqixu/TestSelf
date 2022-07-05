package com.cqx.common.utils.net.asnone;

/**
 * 标识符
 *
 * @author chenqixu
 */
public class TagBean {
    /**
     * 位8和位7代表数据的类型标记
     */
    private EnumTagClass tagClass;
    /**
     * 位6代表该数据单元是原子式还是结构类型
     */
    private EnumTagType tagType;
    /**
     * 位5到位1代表具体分配的TAG值
     */
    private String tagNumber;
    private boolean tagValueToBig;// 默认false，表示标识符值在0-30之间
    private String subsequentTag;
    private int tagLength;

    @Override
    public String toString() {
        if (subsequentTag != null) {
            return String.format("subsequentTag: %s, tag number: %s"
                    , subsequentTag, tagNumber);
        } else {
            return String.format("class: %s, type: %s, tag number: %s, Universal: %s"
                    , tagClass, tagType, tagNumber, EnumUniversalTagNumber.valueOfByValue(
                            Integer.valueOf(tagNumber, 2)));
        }
    }

    public EnumTagClass getTagClass() {
        return tagClass;
    }

    public void setTagClass(EnumTagClass tagClass) {
        this.tagClass = tagClass;
    }

    public EnumTagType getTagType() {
        return tagType;
    }

    public void setTagType(EnumTagType tagType) {
        this.tagType = tagType;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
        if (tagNumber.equals("11111")) tagValueToBig = true;
    }

    public boolean isTagValueToBig() {
        return tagValueToBig;
    }

    public void setTagValueToBig(boolean tagValueToBig) {
        this.tagValueToBig = tagValueToBig;
    }

    public String getSubsequentTag() {
        return subsequentTag;
    }

    public void setSubsequentTag(String subsequentTag) {
        this.subsequentTag = subsequentTag;
        tagValueToBig = subsequentTag.equals("1");
    }

    public int getTagLength() {
        return tagLength;
    }

    public void setTagLength(int tagLength) {
        this.tagLength = tagLength;
    }
}
