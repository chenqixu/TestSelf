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
     * 写入图像，200x200 points
     *
     * @param imageFile
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImage(String imageFile) throws IOException, InvalidFormatException {
        writeImage(imageFile, 200, 200);
    }

    /**
     * 写入图像
     *
     * @param imageFile
     * @param width
     * @param height
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void writeImage(String imageFile, double width, double height) throws IOException, InvalidFormatException {
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
            System.err.println("Unsupported picture: " + imageFile +
                    ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
            return;
        }
        try (FileInputStream is = new FileInputStream(imageFile)) {
            runX.addPicture(is, format, imageFile, Units.toEMU(width), Units.toEMU(height));
        }
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
//            return null;
        } else {
            String postfix = ExcelUtils.getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                read(path, postfix);
            } else {
                logger.info("{}", path + ExcelCommons.NOT_DOC_FILE);
            }
        }
//        return null;
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
