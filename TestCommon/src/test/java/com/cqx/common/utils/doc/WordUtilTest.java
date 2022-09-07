package com.cqx.common.utils.doc;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.pdf.PdfUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class WordUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(WordUtilTest.class);
    private WordUtil wordUtil;

    @Before
    public void setUp() throws Exception {
        wordUtil = new WordUtil();
    }

    @After
    public void tearDown() throws Exception {
        if (wordUtil != null) wordUtil.close();
    }

    @Test
    public void readDoc() throws IOException {
        String path = "d:\\Work\\实时\\ADB\\KafkaToAdb\\需求\\";
        String fileName = "238783-关于铁通监控调度大屏二期的需求实时数据采集-产品需求说明书.doc";
        wordUtil.readDoc(path + fileName);
    }

    @Test
    public void readImageDoc() throws IOException {
        String path = "d:\\Work\\CVS\\BI\\系统文档\\EDC业务\\应用层\\实时应用\\实时ETL\\B域\\09_维护手册\\";
        String fileName = "关于试点智能互动式语音应答服务的相关需求-维护手册.doc";
        wordUtil.readDoc(path + fileName);
    }

    @Test
    public void writeText() throws IOException, InvalidFormatException {
        String fileName = "d:\\tmp\\九色鹿\\test.docx";
        wordUtil.open();
        wordUtil.writeText("123");
        wordUtil.writeImage("d:\\tmp\\九色鹿\\out.jpg", 400, 600);
        wordUtil.newPage();
        wordUtil.save(fileName);
    }

    @Test
    public void pointTest() {
        logger.info("points 200 to EMU: {}", Units.toEMU(200));
    }

    @Test
    public void pdfToDocx() throws IOException, InvalidFormatException {
        PdfUtil pdfUtil = new PdfUtil();
        String scanPath = "d:\\tmp\\九色鹿\\教具\\";
        String picturePath = "d:\\tmp\\九色鹿\\图片\\";
        String docxName = "d:\\tmp\\九色鹿\\test.docx";
        // 扫描PDF
        for (String path : FileUtil.listFile(scanPath)) {
            String newPath = FileUtil.endWith(scanPath + path);
            logger.info("目录：{}，目录名称：{}", newPath, path);
            if (new File(newPath).isDirectory()) {
                for (String pdf : FileUtil.listFile(newPath, "pdf")) {
                    String pdfFile = newPath + pdf;
                    String imgFile = picturePath + path + "##" + pdf.replace(".pdf", ".jpg");
                    // 截取PDF第一页，保存成图片
                    pdfUtil.pdfFileToImage(new File(pdfFile), imgFile);
                    logger.info("PDF文件：{}，保存成图片：{}", pdfFile, imgFile);
                }
            }
        }
        // 扫描图片，保存成docx
        wordUtil.open();
        for (String path : FileUtil.listFile(picturePath)) {
            String[] pathArray = path.split("##", -1);
            if (pathArray.length == 2) {
                logger.info("目录名：{}，文件名：{}", pathArray[0], pathArray[1]);
                wordUtil.writeText(pathArray[0]);
                wordUtil.writeText(pathArray[1].replace(".jpg", ""));
                wordUtil.writeImage(picturePath + path, 400, 600);
                wordUtil.newPage();
            }
        }
        wordUtil.save(docxName);
    }
}