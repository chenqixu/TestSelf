package com.cqx.common.utils.doc;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.excel.ExcelCommons;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.system.ByteUtil;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private CellBeanHelp cellBeanHelp;

    public WordUtil() {
    }

    private void init() {
        // 帮助类
        cellBeanHelp = new CellBeanHelp();

        XWPFStyles xwpfStyles = getDocxDocument().createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setAscii("微软雅黑");
        fonts.setEastAsia("微软雅黑");
        fonts.setHAnsi("微软雅黑");
        xwpfStyles.setDefaultFonts(fonts);
        createHeadingStyle(xwpfStyles, "标题 1", 1, 32, "000000", "微软雅黑");
        createHeadingStyle(xwpfStyles, "标题 2", 2, 28, "000000", "微软雅黑");
        createHeadingStyle(xwpfStyles, "标题 3", 3, 24, "000000", "微软雅黑");
        createHeadingStyle(xwpfStyles, "正文", 0, 20, "000000", "微软雅黑");
    }

    /**
     * 获取序号
     *
     * @param document
     * @return
     */
    public BigInteger getNumId(XWPFDocument document) {
        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));

        /*first level*/
        CTLvl cTLvl0 = cTAbstractNum.addNewLvl(); //create the first numbering level
        cTLvl0.setIlvl(BigInteger.ZERO); //mark it as the top outline level
        cTLvl0.addNewNumFmt().setVal(STNumberFormat.DECIMAL); //set the number format
        cTLvl0.addNewLvlText().setVal("%1"); //set the adornment; %1 is the first-level number or letter as set by number format
        cTLvl0.addNewStart().setVal(BigInteger.ONE); //set the starting number (here, index from 1)
        //        cTLvl0.addNewSuff().setVal(STLevelSuffix.SPACE);        //set space between number and text

        /*second level*/
        CTLvl cTLvl1 = cTAbstractNum.addNewLvl(); //create another numbering level
        cTLvl1.setIlvl(BigInteger.ONE); //specify that it's the first indent
        CTInd ctInd = cTLvl1.addNewPPr().addNewInd(); //add an indent
        //        ctInd.setLeft(inchesToTwips(.5));                       //set a half-inch indent
        cTLvl1.addNewNumFmt().setVal(STNumberFormat.DECIMAL); //the rest is fairly similar
        cTLvl1.addNewLvlText().setVal("%1.%2"); //setup to get 1.1, 1.2, ect.
        cTLvl1.addNewStart().setVal(BigInteger.ONE);
        //        cTLvl1.addNewSuff().setVal(STLevelSuffix.SPACE);

        /*thrid level*/
        CTLvl cTLvl2 = cTAbstractNum.addNewLvl(); //create another numbering level
        cTLvl2.setIlvl(BigInteger.valueOf(2)); //specify that it's the first indent
        CTInd ctInd2 = cTLvl2.addNewPPr().addNewInd(); //add an indent
        //        ctInd.setLeft(inchesToTwips(.5));                       //set a half-inch indent
        cTLvl2.addNewNumFmt().setVal(STNumberFormat.DECIMAL); //the rest is fairly similar
        cTLvl2.addNewLvlText().setVal("%1.%2.%3"); //setup to get 1.1.1, 1.2.1, ect.
        cTLvl2.addNewStart().setVal(BigInteger.valueOf(2));

        /*associate the numbering scheme with the document's numbering*/
        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
        XWPFNumbering numbering = document.createNumbering();
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        return numbering.addNum(abstractNumID);
    }

    /**
     * 创建标题
     *
     * @param style
     * @param text
     * @param fontSize
     * @param level
     */
    public void createTitle(String style, String text, int fontSize, int level) {
        // 创建段落并设置标题样式
        XWPFParagraph paragraph = getDocxDocument().createParagraph();
        BigInteger numId = getNumId(getDocxDocument());
        paragraph.setNumID(numId);// 设置序列
        CTDecimalNumber ctDecimalNumber = paragraph.getCTP().getPPr().getNumPr().addNewIlvl();
        ctDecimalNumber.setVal(BigInteger.valueOf(level));// 设置序列级别
        paragraph.setStyle(style);// 设置标题样式
        XWPFRun runX = paragraph.createRun();
        runX.setText(text);// 标题内容
        runX.setFontSize(fontSize);// 设置字体大小
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
        init();
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    public void openSingle() throws IOException {
        docxDocument = new XWPFDocument();
        init();
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

    public WordDocumentBean readDoc(String path) throws IOException {
        if (path == null || ExcelCommons.EMPTY.equals(path)) {
        } else {
            String postfix = ExcelUtils.getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                return read(path, postfix);
            } else {
                logger.info("{}", path + ExcelCommons.NOT_DOC_FILE);
            }
        }
        return null;
    }

    private WordDocumentBean read(String path, String postfix) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            if (WordCommons.OFFICE_WORD_2003_POSTFIX.equals(postfix)) {
                return new WordDocumentBean(new HWPFDocument(is));
            } else if (WordCommons.OFFICE_WORD_2010_POSTFIX.equals(postfix)) {
                return new WordDocumentBean(new XWPFDocument(is));
            } else {
                return null;
            }
//            document = new HWPFDocument(is);
//            printInfo(document.getRange());
//            printInfo(document.getBookmarks());
//            readTable(document.getRange());
//            readList(document.getRange());
//            readPicture(document.getPicturesTable());
        } finally {
            if (is != null) is.close();
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

    public void createTable() {
        // 创建3个标题
        createTitle("标题 1", "数据库逻辑模型设计", 22, 0);
        createTitle("标题 2", "数据实体描述", 18, 1);
        createTitle("标题 3", "ST_FRAME", 15, 2);

        // 创建一个新的表格
        XWPFTable table = getDocxDocument().createTable(2, 2);
        // 设置表格的列宽
        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setW(BigInteger.valueOf(9000));

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("表名", "PT_INFO");
        headers.put("实体存放", "磐维");
        headers.put("用户模式", "Subject");
        headers.put("表类型", "实体表");
        headers.put("分区字段", "");
        headers.put("分区类型", "");
        headers.put("分区开始时间", "");
        headers.put("索引", "无");
        headers.put("主键", "无");
        headers.put("外键", "无");
        headers.put("表是否压缩", "是");
        headers.put("是否大表", "无");
        headers.put("其他说明", "无");
        int rowIndex = 0;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            // 添加表头和数据列
            buildRow(table, rowIndex, cellBeanHelp.newCellList()
                    .addCell(new CellBean(entry.getKey(), "CCFFCC"))
                    .addCell(new CellBean(entry.getValue(), "CCFFCC"))
                    .getCellList());
            // 合并2,3,4列
            mergeCellsHorizontally(table, rowIndex, 1, 4);
            rowIndex++;
        }

        // 添加表头和数据列
        buildRow(table, rowIndex, cellBeanHelp.newCellList()
                .addCell(new CellBean("字段名称", "CCFFCC"))
                .addCell(new CellBean("数据类型", "CCFFCC"))
                .addCell(new CellBean("是否允许为空", "CCFFCC"))
                .addCell(new CellBean("字段描述", "CCFFCC"))
                .addCell(new CellBean("备注说明", "CCFFCC"))
                .getCellList());
        rowIndex++;

        // 添加数据行
        buildRow(table, rowIndex, cellBeanHelp.newCellList()
                .addCell(new CellBean("Srv_id", null))//字段名称
                .addCell(new CellBean("varchar2(50)", null))//数据类型
                .addCell(new CellBean("", null))//是否允许为空
                .addCell(new CellBean("服务编码", null))//字段描述
                .addCell(new CellBean("", null))//备注说明
                .getCellList());
    }

    public XWPFTable createTable(int rows, int cols) {
        return createTable(rows, cols, null);
    }

    public XWPFTable createTable(int rows, int cols, BigInteger weight) {
        // 创建一个新的表格
        XWPFTable table = getDocxDocument().createTable(rows, cols);
        if (weight != null) {
            // 设置表格的列宽
            CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
            tblWidth.setW(weight);
        }
        return table;
    }

    public XWPFTableRow buildRow(XWPFTable table, int rowIndex) {
        if (table.getRows().size() > rowIndex) {
            return table.getRow(rowIndex);
        } else {
            return table.insertNewTableRow(rowIndex);
        }
    }

    public void buildRow(XWPFTable table, int rowIndex, List<CellBean> cells) {
        XWPFTableRow xwpfTableRow;
        if (table.getRows().size() > rowIndex) {
            xwpfTableRow = table.getRow(rowIndex);
        } else {
            xwpfTableRow = table.insertNewTableRow(rowIndex);
        }
        for (int i = 0; i < cells.size(); i++) {
            CellBean cellBean = cells.get(i);
            buildCell(xwpfTableRow, i, cellBean.getText(), cellBean.getColor());
        }
    }

    public void buildCell(XWPFTableRow dataRow, int index, String text, String color) {
        XWPFTableCell cell;
        if (dataRow.getTableCells().size() > index) {
            cell = dataRow.getCell(index);
        } else {
            cell = dataRow.addNewTableCell();
        }
        cell.setText(text);
        if (color != null) cell.setColor(color);
    }

    /**
     * @param styles       样式
     * @param strStyleId   标题id
     * @param headingLevel 标题级别
     * @param pointSize    字体大小（/2）
     * @param hexColor     字体颜色
     * @param typefaceName 字体名称（默认微软雅黑）
     */
    public void createHeadingStyle(XWPFStyles styles, String strStyleId,
                                   int headingLevel, int pointSize, String hexColor, String typefaceName) {
        //创建样式
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        //设置id
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        // 数字越低在格式栏中越突出
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        // 样式将显示在“格式”栏中
        ctStyle.setQFormat(onoffnull);

        // 样式定义给定级别的标题
        if (headingLevel != 0) {
            CTPPr ppr = CTPPr.Factory.newInstance();
            ppr.setOutlineLvl(indentNumber);
            ctStyle.setPPr(ppr);
        }
        XWPFStyle style = new XWPFStyle(ctStyle);

        CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
        size.setVal(new BigInteger(String.valueOf(pointSize)));
        CTHpsMeasure size2 = CTHpsMeasure.Factory.newInstance();
        size2.setVal(new BigInteger(String.valueOf(pointSize)));

        CTFonts fonts = CTFonts.Factory.newInstance();
        if (typefaceName == null || typefaceName.equals("")) typefaceName = "微软雅黑";
        fonts.setAscii(typefaceName);  // 字体

        CTRPr rpr = CTRPr.Factory.newInstance();
        rpr.setRFonts(fonts);
        rpr.setSz(size);
        rpr.setSzCs(size2);  // 字体大小

        CTColor color = CTColor.Factory.newInstance();
        color.setVal(ByteUtil.hexStringToBytes(hexColor));
        rpr.setColor(color); // 字体颜色
        style.getCTStyle().setRPr(rpr);
        // is a null op if already defined

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }

    /**
     * 合并指定行的指定列范围的单元格
     *
     * @param table
     * @param rowIndex
     * @param fromCol
     * @param toCol
     */
    public void mergeCellsHorizontally(XWPFTable table, int rowIndex, int fromCol, int toCol) {
        XWPFTableRow row = table.getRow(rowIndex);
        CTTcPr ctTcPr = row.getCell(fromCol).getCTTc().addNewTcPr();
        ctTcPr.addNewGridSpan().setVal(BigInteger.valueOf(toCol));
    }

    public XWPFDocument getDocxDocument() {
        return docxDocument;
    }

    public CellBeanHelp getCellBeanHelp() {
        return cellBeanHelp;
    }
}
