package com.cqx.common.utils.net.asnone;

/**
 * LengthOctetsBean
 *
 * @author chenqixu
 */
public class LengthOctetsBean {
    private String flag;
    private int length = 0;
    private int lengthOctetsLength;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void addLength(int length) {
        this.length += length;
    }

    public int getLengthOctetsLength() {
        return lengthOctetsLength;
    }

    public void setLengthOctetsLength(int lengthOctetsLength) {
        this.lengthOctetsLength = lengthOctetsLength;
    }
}
