package com.cqx.common.utils.doc;

/**
 * CellBean
 *
 * @author chenqixu
 */
public class CellBean {
    private String text;
    private String color;

    public CellBean(String text, String color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }
}
