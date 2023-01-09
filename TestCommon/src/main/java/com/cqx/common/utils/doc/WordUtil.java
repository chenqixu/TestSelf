package com.cqx.common.utils.doc;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.excel.ExcelCommons;
import com.cqx.common.utils.excel.ExcelUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * word doc工具
 * <p>
 * HWPF支持Microsoft Word 97(-2007)
 * <p>
 * XWPF支持new Word 2007 .docx
 *
 * @author chenqixu
 */
public class WordUtil implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WordUtil.class);
    private XWPFDocument docxDocument;
    private XWPFParagraph paragraphX;
    private XWPFRun runX;

    public WordUtil() {
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    public void open() throws IOException {
        docxDocument = new XWPFDocument();
        paragraphX = docxDocument.createParagraph();
        runX = paragraphX.createRun();
    }

    /**
     * 写入文本并换行
     *
     * @param text
     */
    public void writeText(String text) {
        runX.setText(text);
        runX.addCarriageReturn();// 回车
    }

    /**
     * 厘米转成EMU
     *
     * @param centimeter
     * @return
     */
    public int centimeterToEMU(double centimeter) {
        return (int) Math.rint(centimeter * Units.EMU_PER_CENTIMETER);
    }

    /**
     * 写入图片，单位是像素，原图大小
     *
     * @param imageFile
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImageByOriginalPixel(String imageFile) throws IOException, InvalidFormatException {
        try (FileInputStream is = new FileInputStream(imageFile)) {
            int len = is.available();
            if (len > 0) {
                byte[] bytes = new byte[len];
                int ret = is.read(bytes);
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                    BufferedImage img = javax.imageio.ImageIO.read(bis);
                    // 获取的单位是像素
                    int width = img.getWidth();
                    int height = img.getHeight();
                    logger.info("读取图片: {}, 大小: {}, 宽: {} 像素, 高: {} 像素", imageFile, ret, width, height);
                    // 重置，因为下面还要用
                    bis.reset();
                    writeImage(imageFile, bis, Units.pixelToEMU(width), Units.pixelToEMU(height));
                }
            } else {
                logger.warn("图片 {} 大小为0！", imageFile);
            }
        }
    }

    /**
     * 写入图片，单位是厘米，宽固定，高度等比例缩放<br>
     * 360000 EMUs per centimeter
     *
     * @param imageFile
     * @param fixCentimeterWidth
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImageByFixCentimeterWidth(String imageFile, double fixCentimeterWidth) throws IOException, InvalidFormatException {
        try (FileInputStream is = new FileInputStream(imageFile)) {
            int len = is.available();
            if (len > 0) {
                byte[] bytes = new byte[len];
                int ret = is.read(bytes);
                try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                    BufferedImage img = javax.imageio.ImageIO.read(bis);
                    // 获取的单位是像素
                    int _width = img.getWidth();
                    int _height = img.getHeight();
                    double centimeterHeight = fixCentimeterWidth * _height / _width;
                    logger.info("读取图片: {}, 大小: {}, 宽: {} 像素, 高: {} 像素, 等比例宽: {} 厘米, 等比例高: {} 厘米"
                            , imageFile, ret, _width, _height, fixCentimeterWidth, centimeterHeight);
                    bis.reset();
                    writeImage(imageFile, bis, centimeterToEMU(fixCentimeterWidth), centimeterToEMU(centimeterHeight));
                }
            }
        }
    }

    /**
     * 写入图片，单位是point<br>
     * 12700 EMUs per point
     *
     * @param imageFile
     * @param pointWidth
     * @param pointHeight
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImageByPoint(String imageFile, double pointWidth, double pointHeight) throws IOException, InvalidFormatException {
        try (FileInputStream is = new FileInputStream(imageFile)) {
            writeImage(imageFile, is, Units.toEMU(pointWidth), Units.toEMU(pointHeight));
        }
    }

    /**
     * 写入图片，单位是厘米<br>
     * 360000 EMUs per centimeter
     *
     * @param imageFile
     * @param centimeterWidth
     * @param centimeterHeight
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImageByCentimeter(String imageFile, double centimeterWidth, double centimeterHeight) throws IOException, InvalidFormatException {
        try (FileInputStream is = new FileInputStream(imageFile)) {
            writeImage(imageFile, is, centimeterToEMU(centimeterWidth), centimeterToEMU(centimeterHeight));
        }
    }

    /**
     * 写入图片，单位是原始的emu，像素、厘米、英寸、points都有一套转换关系，参考Units
     *
     * @param imageFile
     * @param is
     * @param emuWidth
     * @param emuHeight
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImage(String imageFile, InputStream is, int emuWidth, int emuHeight) throws IOException, InvalidFormatException {
        int format;
        if (imageFile.endsWith(".emf")) {
            format = Document.PICTURE_TYPE_EMF;
        } else if (imageFile.endsWith(".wmf")) {
            format = Document.PICTURE_TYPE_WMF;
        } else if (imageFile.endsWith(".pict")) {
            format = Document.PICTURE_TYPE_PICT;
        } else if (imageFile.endsWith(".jpeg") || imageFile.endsWith(".jpg")) {
            format = Document.PICTURE_TYPE_JPEG;
        } else if (imageFile.endsWith(".png")) {
            format = Document.PICTURE_TYPE_PNG;
        } else if (imageFile.endsWith(".dib")) {
            format = Document.PICTURE_TYPE_DIB;
        } else if (imageFile.endsWith(".gif")) {
            format = Document.PICTURE_TYPE_GIF;
        } else if (imageFile.endsWith(".tiff")) {
            format = Document.PICTURE_TYPE_TIFF;
        } else if (imageFile.endsWith(".eps")) {
            format = Document.PICTURE_TYPE_EPS;
        } else if (imageFile.endsWith(".bmp")) {
            format = Document.PICTURE_TYPE_BMP;
        } else if (imageFile.endsWith(".wpg")) {
            format = Document.PICTURE_TYPE_WPG;
        } else {
            logger.error("Unsupported picture: " + imageFile +
                    ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
            return;
        }
        runX.addPicture(is, format, imageFile, emuWidth, emuHeight);
    }

    /**
     * 写入分页符
     */
    public void newPage() {
        runX.addBreak(BreakType.PAGE);
    }

    /**
     * 保存到文件
     *
     * @param fileName
     * @throws IOException
     */
    public void save(String fileName) throws IOException {
        docxDocument.write(new FileOutputStream(fileName));
    }

    /**
     * 资源释放
     */
    @Override
    public void close() {
        if (docxDocument != null)
            try {
                docxDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void readDoc(String path) throws IOException {
        if (path == null || ExcelCommons.EMPTY.equals(path)) {
        } else {
            String postfix = ExcelUtils.getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                read(path, postfix);
            } else {
                logger.info("{}", path + ExcelCommons.NOT_DOC_FILE);
            }
        }
    }

    private void read(String path, String postfix) throws IOException {
        InputStream is = null;
        HWPFDocument document = null;
//        XWPFDocument xwpfDocument =null;
        try {
            is = new FileInputStream(path);
            document = new HWPFDocument(is);
//            printInfo(document.getRange());
//            printInfo(document.getBookmarks());
//            readTable(document.getRange());
//            readList(document.getRange());
            readPicture(document.getPicturesTable());
        } finally {
            if (is != null) is.close();
            if (document != null) document.close();
        }
    }

    /**
     * 读取所有图片
     *
     * @param picturesTable
     */
    private void readPicture(PicturesTable picturesTable) {
        for (Picture picture : picturesTable.getAllPictures()) {
            int picSize = picture.getSize();
            if (picSize > 0) {
                logger.info("图片大小：{}，描述：{}", Utils.changeUnit(picSize), picture.getDescription());
            }
        }
    }

    /**
     * 输出书签信息
     *
     * @param bookmarks
     */
    private void printInfo(Bookmarks bookmarks) {
        int count = bookmarks.getBookmarksCount();
        logger.info("书签数量：{}", count);
        Bookmark bookmark;
        for (int i = 0; i < count; i++) {
            bookmark = bookmarks.getBookmark(i);
            logger.info("书签{}的名称是：{}", (i + 1), bookmark.getName());
            logger.info("开始位置：{}", bookmark.getStart());
            logger.info("结束位置：{}", bookmark.getEnd());
        }
    }

    /**
     * 读表格
     * 每一个回车符代表一个段落，所以对于表格而言，每一个单元格至少包含一个段落，每行结束都是一个段落。
     *
     * @param range
     */
    private void readTable(Range range) {
        //遍历range范围内的table
        TableIterator tableIter = new TableIterator(range);
        Table table;
        TableRow row;
        TableCell cell;
        while (tableIter.hasNext()) {
            table = tableIter.next();
            int rowNum = table.numRows();
            for (int j = 0; j < rowNum; j++) {
                row = table.getRow(j);
                int cellNum = row.numCells();
                for (int k = 0; k < cellNum; k++) {
                    cell = row.getCell(k);
                    //输出单元格的文本
                    logger.info("{}", cell.text().trim());
                }
            }
        }
    }

    /**
     * 读列表
     *
     * @param range
     */
    private void readList(Range range) {
        int num = range.numParagraphs();
        Paragraph para;
        for (int i = 0; i < num; i++) {
            // 获取段落
            para = range.getParagraph(i);
            if (para.isInList()) {
                logger.info("list: {}", para.text());
            }
        }
    }

    /**
     * 输出Range
     *
     * @param range
     */
    private void printInfo(Range range) {
        //获取段落数
        int paraNum = range.numParagraphs();
        logger.info("{}", paraNum);
        for (int i = 0; i < paraNum; i++) {
            logger.info("段落{}：{}", (i + 1), range.getParagraph(i).text());
        }
        int secNum = range.numSections();
        logger.info("{}", secNum);
        Section section;
        for (int i = 0; i < secNum; i++) {
            section = range.getSection(i);
            logger.info("{}", section.getMarginLeft());
            logger.info("{}", section.getMarginRight());
            logger.info("{}", section.getMarginTop());
            logger.info("{}", section.getMarginBottom());
            logger.info("{}", section.getPageHeight());
            logger.info("{}", section.text());
        }
    }
}
