package com.cqx.common.utils.image;

import java.io.IOException;

/**
 * 图像识别处理接口
 *
 * @author chenqixu
 */
public interface IDealImage {
    void read(String srcImg) throws IOException;

    void save(String srcImg, EnumImage enumImage) throws IOException;
}
