package com.cqx.common.utils.pdf;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

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

    public BufferedImage pageConvertToImage(PDPage page, int dpi, ImageType imageType) throws IOException {
        try (PDDocument document = new PDDocument()) {
            document.addPage(page);
            PDFRenderer renderer = new PDFRenderer(document);
            document.close();
            return renderer.renderImageWithDPI(0, dpi, imageType);
        }
    }

    public void pageConvertToImage(String pdfSource, int pageIndex, int dpi, ImageType imageType, String targetPath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(pdfSource);
             PDDocument doc = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage bufferedImage = renderer.renderImageWithDPI(pageIndex, dpi, imageType);
            bufferedImage.flush();
            SaveImage(bufferedImage, targetPath);
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
                SaveImage(image, targetPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 把图像流保存成图像文件
     *
     * @param image      图像流
     * @param targetPath 输出的图像文件
     */
    public void SaveImage(BufferedImage image, String targetPath) {
        InputStream byteInputStream = null;
        File uploadFile = new File(targetPath);
        try (ByteArrayOutputStream bs = new ByteArrayOutputStream();
             ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
             FileOutputStream fops = new FileOutputStream(uploadFile)
        ) {
            ImageIO.write(image, "jpg", imOut);
            byteInputStream = new ByteArrayInputStream(bs.toByteArray());
            fops.write(readInputStream(byteInputStream));
            fops.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteInputStream != null) {
                try {
                    byteInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从PDF文件中截取指定页面
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
            e.printStackTrace();
        }
        return null;
    }
}
