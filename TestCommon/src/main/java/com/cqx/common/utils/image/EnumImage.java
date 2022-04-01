package com.cqx.common.utils.image;

/**
 * 图像后缀集合
 *
 * @author chenqixu
 */
public enum EnumImage {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    WEBP("webp"),
    ;

    private String withPointName;
    private String name;

    EnumImage(String name) {
        this.name = name;
        this.withPointName = "." + name;
    }

    public String getName() {
        return this.name;
    }

    public String getWithPointName() {
        return this.withPointName;
    }
}
