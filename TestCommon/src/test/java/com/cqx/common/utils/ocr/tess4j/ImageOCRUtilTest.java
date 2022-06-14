package com.cqx.common.utils.ocr.tess4j;

import org.junit.Before;
import org.junit.Test;

public class ImageOCRUtilTest {
    private ImageOCRUtil imageOCRUtil;

    @Before
    public void setUp() {
        imageOCRUtil = new ImageOCRUtil();
    }

    @Test
    public void ocr() {
        imageOCRUtil.ocr("I:\\Document\\Workspaces\\Git\\TestSelf\\TestCommon\\src\\test\\resources\\img\\4.jpg"
                , "jpn");
    }
}