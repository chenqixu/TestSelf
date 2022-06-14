package com.cqx.common.utils.ocr.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * 图像文字识别
 *
 * @author chenqixu
 */
public class ImageOCRUtil {
    // 语言库位置
    private String lagnguagePath = System.getenv("TESSDATA_PREFIX");

    public void ocr(String img, String language) {
        ITesseract instance = new Tesseract();
        //设置训练库的位置
        instance.setDatapath(lagnguagePath);
        //chi_sim简体中文， eng英文
        instance.setLanguage(language);
        instance.setTessVariable("textord_tabfind_force_vertical_text", "0");
        instance.setTessVariable("textord_tabfind_vertical_text", "1");
        instance.setTessVariable("textord_tabfind_vertical_horizontal_mix", "1");
        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            result = instance.doOCR(new File(img));
            long endTime = System.currentTimeMillis();
            System.out.println("Time is：" + (endTime - startTime) + " 毫秒");
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        System.out.println("result: ");
        System.out.println(result);
    }
}
