package com.cqx.common.utils.doc;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.pdf.PdfUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        wordUtil.writeImageByPoint("d:\\tmp\\九色鹿\\out.jpg", 400, 600);
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
                wordUtil.writeImageByPoint(picturePath + path, 400, 600);
                wordUtil.newPage();
            }
        }
        wordUtil.save(docxName);
    }

    @Test
    public void imageToDoc() throws IOException, InvalidFormatException {
        String picturePath = "e:\\Self\\课本\\小学\\英语四年级下(闽教)\\";
        String docxName = "e:\\Self\\课本\\小学\\英语四年级下.docx";
        try {
            // 打开文件
            wordUtil.open();
            // 扫描图片
            for (String path : FileUtil.listFileEndWith(picturePath, ".jpg")) {
                // 原始像素
//                wordUtil.writeImageByOriginalPixel(picturePath + path);
                // 根据point
//                wordUtil.writeImageByPoint(picturePath + path, 415, 596);
                // 宽度固定，根据厘米
                wordUtil.writeImageByFixCentimeterWidth(picturePath + path, 14.65d);
                // 根据厘米，写入4
                // A4大小: 宽 14.65, 高 21.05
//                wordUtil.writeImageByCentimeter(picturePath + path, 14.65d, 21.05d);
            }
        } finally {
            // 保存
            wordUtil.save(docxName);
        }
    }

    @Test
    public void imageToPDF() throws IOException {
        String picturePath = "e:\\Self\\课本\\小学\\英语四年级下(闽教)\\";
        String pdfName = "e:\\Self\\课本\\小学\\英语四年级下.pdf";
        List<String> imgs = new ArrayList<>();
        PdfUtil pdfUtil = new PdfUtil();
        // 扫描PDF
        for (String path : FileUtil.listFileEndWith(picturePath, ".jpg")) {
            String newPath = FileUtil.endWith(picturePath) + path;
            logger.info("文件全路径：{}", newPath);
            imgs.add(newPath);
        }
        pdfUtil.mergerImgToPDF(imgs, pdfName);
    }

    @Test
    public void wordCreate() throws IOException, XmlException {
        WordDocumentBean wordDocumentBean = null;
        String modelName = "d:\\tmp\\data\\word\\model.docx";
        String docxName = "d:\\tmp\\data\\word\\1.docx";
        try {
            // 打开文件
            wordUtil.openSingle();

            // 模板读取
            wordDocumentBean = wordUtil.readDoc(modelName);
            CTStyles ctStyles = wordDocumentBean.getXwDocument().getStyle();
            wordUtil.getDocxDocument().createStyles().setStyles(ctStyles);

            // 创建段落
            for (int i = 1; i <= 4; i++) {
                XWPFParagraph title = wordUtil.getDocxDocument().createParagraph();
                // 设置段落的样式
                title.setStyle("" + i);
                XWPFRun run = title.createRun();
                run.setText("Level " + i + " Heading");
            }

            for (XWPFParagraph paragraph : wordUtil.getDocxDocument().getParagraphs()) {
                String style = paragraph.getStyle();
                CTString pStyle = paragraph.getCTP().getPPr().getPStyle();
                System.out.println(String.format("style=%s, pstyle=%s", style, pStyle));
            }
        } finally {
            // 保存
            wordUtil.save(docxName);
            if (wordDocumentBean != null) wordDocumentBean.close();
        }
    }

    @Test
    public void wordRead() throws IOException {
        String docxName = "d:\\tmp\\data\\word\\1.docx";
        // 打开文件
        try (WordDocumentBean wordDocumentBean = wordUtil.readDoc(docxName)) {
            for (XWPFParagraph paragraph : wordDocumentBean.getXwDocument().getParagraphs()) {
                String style = paragraph.getStyle();
                CTString pStyle = paragraph.getCTP().getPPr().getPStyle();
                System.out.println(String.format("style=%s, pstyle=%s", style, pStyle));
            }
        }
    }
}