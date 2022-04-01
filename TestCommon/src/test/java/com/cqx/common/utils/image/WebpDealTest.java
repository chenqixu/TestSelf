package com.cqx.common.utils.image;

import org.junit.Test;

import java.io.IOException;

public class WebpDealTest {

    @Test
    public void save() throws IOException {
        IDealImage iDealImage = new WebpDeal();
        iDealImage.save("e:\\Self\\宁化小学\\信息技术课\\键盘1.webp", EnumImage.JPG);
        iDealImage.save("e:\\Self\\宁化小学\\信息技术课\\键盘2.webp", EnumImage.JPG);
        iDealImage.save("e:\\Self\\宁化小学\\信息技术课\\键盘3.webp", EnumImage.JPG);
    }
}