package com.cqx.common.utils.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * PdfUtil
 *
 * @author chenqixu
 */
public class PdfUtil {
    private static final Logger logger = LoggerFactory.getLogger(PdfUtil.class);
    // 分辨率
    private static final int DPI = 288;

    public static void main(String[] args) throws IOException {
        PdfUtil pdfUtil = new PdfUtil();
        String source = "D:\\tmp\\九色鹿\\阿拉丁\\L3 Table Game教具 20220701.pdf";
        String dest = "D:\\tmp\\九色鹿\\out.pdf";
        String targetfile = "D:\\tmp\\九色鹿\\out.jpg";
        // 上传的是jpg格式的图片结尾，png太大了
        pdfUtil.pdfFileToImage(new File(source), targetfile);
//        pdfUtil.pageConvertToImage(source, 0, DPI, ImageType.BINARY, targetfile);
    }

    /**
     * 截取指定PDF页面，保存成图片
     *
     * @param pdfSource
     * @param pageIndex
     * @param dpi
     * @param imageType
     * @param targetPath
     * @throws IOException
     */
    public void pageConvertToImage(String pdfSource, int pageIndex, int dpi, ImageType imageType, String targetPath) throws IOException {
        // 读入文件流
        try (FileInputStream inputStream = new FileInputStream(pdfSource);
             // 加载PDF文档
             PDDocument doc = PDDocument.load(inputStream)) {
            // 加载PDF渲染器
            PDFRenderer renderer = new PDFRenderer(doc);
            // Returns the given page as an RGB image at the given DPI.
            // 返回指定页面的图像流，指定DPI，指定RGB
            BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, dpi, imageType);
            // 需要刷新图像流，否则会导致没有数据
            bufferedImage.flush();
            // 把图像流保存成文件
            saveImage(bufferedImage, targetPath);
        }
    }

    /**
     * 读取PDF文件的第一页，保存成图片
     *
     * @param pdfFile    PDF文件
     * @param targetPath 目标图片文件
     */
    public void pdfFileToImage(File pdfFile, String targetPath) {
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile);
             PDDocument doc = PDDocument.load(fileInputStream)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if (pageCount > 0) {
                // scale – the scaling factor, where 1 = 72 DPI
                BufferedImage image = renderer.renderImage(0, Float.valueOf(String.valueOf(DPI / 72)));
                image.flush();
                saveImage(image, targetPath);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 读取输出流，输出字节数组
     *
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public byte[] readInputStream(InputStream inStream) throws Exception {
        byte[] result;
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            result = outStream.toByteArray();
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
        return result;
    }

    /**
     * 把图像流保存成图像文件
     *
     * @param image      图像流
     * @param targetPath 输出的图像文件
     */
    public void saveImage(BufferedImage image, String targetPath) {
        File uploadFile = new File(targetPath);
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
             ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
             FileOutputStream fops = new FileOutputStream(uploadFile)
        ) {
            ImageIO.write(image, "jpg", imOut);
            fops.write(readInputStream(new ByteArrayInputStream(bs.toByteArray())));
            fops.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 从PDF文件中截取指定页面，另外保存成1个PDF文件
     *
     * @param pageNum
     * @param source
     * @param dest
     * @return
     */
    public String splitPdf(int pageNum, String source, String dest) {
        File indexFile = new File(source);
        File outFile = new File(dest);
        PDDocument document;
        try {
            document = PDDocument.load(indexFile);
            // document.getNumberOfPages();
            Splitter splitter = new Splitter();
            splitter.setStartPage(pageNum);
            splitter.setEndPage(pageNum);
            List pages = splitter.split(document);
            for (Object page : pages) {
                PDDocument pd = (PDDocument) page;
                if (outFile.exists()) {
                    boolean delete = outFile.delete();
                }
                pd.save(outFile);
                pd.close();
                if (outFile.exists()) {
                    return outFile.getPath();
                }
            }
            document.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 合并图片到PDF中
     *
     * @param imgs
     * @param pdfFile
     * @throws IOException
     */
    public void mergerImgToPDF(List<String> imgs, String pdfFile) throws IOException {
        int write_cnt = 0;
        // 创建空白文档
        try (PDDocument pdDocument = new PDDocument()) {
            for (String img : imgs) {
                // 先判断是否是文件
                File imgFile = new File(img);
                if (imgFile.isFile()) {
                    // 创建空白页面
                    PDPage page = new PDPage();
                    pdDocument.addPage(page);
                    // 通过图片路径和PDF文档对象创建PDF图片image对象
                    PDImageXObject image = PDImageXObject.createFromFile(img, pdDocument);
                    // 创建pageStream对象
                    try (PDPageContentStream pageStream = new PDPageContentStream(pdDocument, page
                            , PDPageContentStream.AppendMode.APPEND, false, false)) {
                        // pageStream对象绘制图片位置及大小
                        // 以PDF文件右下角为原点（x,y）是图片左下角左边
                        // width、height是图片的长和宽
                        pageStream.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
                        write_cnt++;
                        logger.info("写入图片{}，width: {}，height: {}", img, image.getWidth(), image.getHeight());
                    }
                } else {
                    logger.warn("{} 不是一个文件，跳过！", img);
                }
            }
            if (write_cnt > 0) {
                // 保存PDF到指定路劲
                pdDocument.save(pdfFile);
            }
        }
    }
}
