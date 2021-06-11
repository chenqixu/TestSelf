package com.cqx.common.utils.excel;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    // 注意有并发问题
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 字段个数是否参考标题
    private boolean isAlignByTitle = false;

    public ExcelUtils() {
    }

    public ExcelUtils(boolean isAlignByTitle) {
        this.isAlignByTitle = isAlignByTitle;
    }

    /**
     * read the Excel file
     *
     * @param path the path of the Excel file
     * @return
     * @throws IOException
     */
    public List<ExcelSheetList> readExcel(String path) throws IOException {
        if (path == null || ExcelCommons.EMPTY.equals(path)) {
            return null;
        } else {
            String postfix = getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                return read(path, postfix);
            } else {
                logger.info("{}", path + ExcelCommons.NOT_EXCEL_FILE);
            }
        }
        return null;
    }

    /**
     * 根据后缀创建Workbook
     *
     * @param postfix
     * @param is
     * @return
     * @throws IOException
     */
    private Workbook createWorkbook(String postfix, InputStream is) throws IOException {
        if (ExcelCommons.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
            if (is == null) return new HSSFWorkbook();
            else return new HSSFWorkbook(is);
        } else if (ExcelCommons.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)
                || ExcelCommons.OFFICE_EXCEL_XLSM.equals(postfix)) {
            if (is == null) return new XSSFWorkbook();
            else return new XSSFWorkbook(is);
        } else {
            return null;
        }
    }

    /**
     * 公式计算器
     *
     * @param workbook
     * @return
     */
    private FormulaEvaluator createFormulaEvaluator(Workbook workbook) {
        FormulaEvaluator evaluator = null;
        if (workbook instanceof HSSFWorkbook) {
            evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        } else if (workbook instanceof XSSFWorkbook) {
            evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }
        return evaluator;
    }

    /**
     * 名称管理器
     *
     * @param workbook
     * @return
     */
    private Map<String, Name> createName(Workbook workbook) {
        Map<String, Name> nameMap = new HashMap<>();
        for (Name name : workbook.getAllNames()) {
            logger.debug("name：{}，RefersToFormula：{}", name.getNameName(), name.getRefersToFormula());
            nameMap.put(name.getNameName(), name);
        }
        return nameMap;
    }

    /**
     * Read the Excel 2010/2003-2007
     *
     * @param path
     * @param postfix
     * @return
     * @throws IOException
     */
    private List<ExcelSheetList> read(String path, String postfix) throws IOException {
        InputStream is = null;
        List<ExcelSheetList> resultlist = new ArrayList<>();
        try {
            is = new FileInputStream(path);
            Workbook workbook = createWorkbook(postfix, is);
            // 创建计算器，用于计算公式
            FormulaEvaluator evaluator = createFormulaEvaluator(workbook);
            // 获取名称管理器
            Map<String, Name> nameMap = createName(workbook);
            // 判断工作簿是否创建成功
            if (workbook == null) throw new IOException(String.format("创建工作簿失败，path：%s，postfix：%s", path, postfix));
            // 循环每一页，并处理当前页
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }
                ExcelSheetList esl = new ExcelSheetList();
                // 设置sheetName
                esl.setSheetName(sheet.getSheetName());
                List<List<String>> sheetlist = new ArrayList<>();
                // 设置sheet内容
                esl.setSheetList(sheetlist);
                // 如果要按表头对齐长度，获取表头长度
                int titleRow = -1;
                if (isAlignByTitle) {
                    if (sheet.getLastRowNum() >= 0) {
                        Row firstRow = sheet.getRow(0);
                        if (firstRow != null) titleRow = firstRow.getLastCellNum();
                    }
                }
                // 处理当前页，循环每一行
                for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        int minColIx = row.getFirstCellNum();
                        int maxColIx = row.getLastCellNum();
                        // 如果要按表头对齐长度，最大单元格以表头为准
                        if (titleRow > -1) maxColIx = titleRow;
                        List<String> rowlist = new ArrayList<>();
                        // 遍历该行，获取每个cell元素
                        for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                            Cell cell = row.getCell(colIx);
                            if (cell == null) {
                                rowlist.add("");
                            } else {
                                rowlist.add(getValue(cell, evaluator));
                            }
                        }
                        sheetlist.add(rowlist);
                    }
                }
                resultlist.add(esl);
            }
        } finally {
            if (is != null) is.close();
        }
        return resultlist;
    }

    /**
     * 读取cell
     *
     * @param cell
     * @return
     */
    private String getValue(Cell cell, FormulaEvaluator evaluator) {
        switch (cell.getCellType()) {
            case NUMERIC:// Numeric Cell type (0)
                //时间判断
                if (cell.getCellStyle().getDataFormat() > 0) {
                    return simpleDateFormat.format(cell.getDateCellValue());
                } else {
                    //可能有小数点、也可能是科学计数法
                    Double d = cell.getNumericCellValue();
                    //判断绝对值和原值相减是否为0，为0则取longValue，不为0则保留小数
                    if (Math.round(d) - d == 0) {
                        return String.valueOf(d.longValue());
                    } else {
                        return String.valueOf(d);
                    }
                }
            case STRING:// String Cell type (1)
                return String.valueOf(cell.getStringCellValue());
            case FORMULA:// Formula Cell type (2) 公式
                if (evaluator != null) {
                    String evaluatorVal = null;
                    try {
                        logger.debug("CellFormula：{}", cell.getCellFormula());
                        evaluatorVal = evaluator.evaluate(cell).getStringValue();
                    } catch (Exception e) {
                        // todo 名称管理器
                        // org.apache.poi.ss.formula.FormulaParseException:
                        // Name 'GET.WORKBOOK' is completely unknown in the current workbook
                        // todo 不认识的公式
                        // Unexpected ptg class (org.apache.poi.ss.formula.ptg.ArrayPtg)
//                        logger.error(e.getMessage(), e);
                    }
                    return evaluatorVal;
                }
                break;
            case BLANK:// Blank Cell type (3)
                break;
            case BOOLEAN:// Boolean Cell type (4)
                return String.valueOf(cell.getBooleanCellValue());
            case ERROR:// Error Cell type (5)
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * get postfix of the path
     *
     * @param path
     * @return
     */
    private String getPostfix(String path) {
        if (path == null || ExcelCommons.EMPTY.equals(path.trim())) {
            return ExcelCommons.EMPTY;
        }
        if (path.contains(ExcelCommons.POINT)) {
            return path.substring(path.lastIndexOf(ExcelCommons.POINT) + 1);
        }
        return ExcelCommons.EMPTY;
    }

    /**
     * Write the Excel 2010/2003-2007
     *
     * @param path
     * @param postfix
     * @param excelSheetLists
     * @return
     * @throws IOException
     */
    private int write(String path, String postfix, List<ExcelSheetList> excelSheetLists) throws IOException {
        FileOutputStream stream = null;
        int result = 0;
        try {
            //创建Excel文件薄
            Workbook workbook = createWorkbook(postfix, null);
            // 判断工作簿是否创建成功
            if (workbook == null) throw new IOException(String.format("创建工作簿失败，path：%s，postfix：%s", path, postfix));
            //数据加载
            for (int i = 0; i < excelSheetLists.size(); i++) {
                String sheetName = excelSheetLists.get(i).getSheetName();
                List<List<String>> rowList = excelSheetLists.get(i).getSheetList();
                //创建工作表sheeet
                Sheet sheet = workbook.createSheet(sheetName);
                for (int j = 0; j < rowList.size(); j++) {
                    //创建行
                    Row nextrow = sheet.createRow(j);
                    List<String> cellList = rowList.get(j);
                    for (int z = 0; z < cellList.size(); z++) {
                        //创建列
                        Cell cell = nextrow.createCell(z);
                        cell.setCellValue(cellList.get(z));
                    }
                }
                result++;
            }
            //创建一个文件
            File file = new File(path);
            boolean create_file_status = file.createNewFile();
            if (create_file_status) {
                stream = FileUtils.openOutputStream(file);
                workbook.write(stream);
            }
        } finally {
            if (stream != null) stream.close();
        }
        return result;
    }

    /**
     * 写Excel
     *
     * @param path
     * @param excelSheetLists
     * @return
     * @throws IOException
     */
    public int writeExcel(String path, List<ExcelSheetList> excelSheetLists) throws IOException {
        if (path == null || ExcelCommons.EMPTY.equals(path)) {
            return -1;
        } else {
            String postfix = getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                return write(path, postfix, excelSheetLists);
            } else {
                logger.info("{}", path + ExcelCommons.NOT_EXCEL_FILE);
            }
        }
        return -1;
    }

}
