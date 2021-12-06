package com.cqx.common.utils.doc;

import com.cqx.common.utils.excel.ExcelCommons;
import com.cqx.common.utils.excel.ExcelUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * word doc工具
 * <p>
 * HWPF支持Microsoft Word 97(-2007)
 * <p>
 * XWPF支持new Word 2007 .docx
 *
 * @author chenqixu
 */
public class DocUtil {
    private static final Logger logger = LoggerFactory.getLogger(DocUtil.class);

    public DocUtil() {
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
            readList(document.getRange());
        } finally {
            if (is != null) is.close();
            if (document != null) document.close();
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
        //遍历range范围内的table。
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
