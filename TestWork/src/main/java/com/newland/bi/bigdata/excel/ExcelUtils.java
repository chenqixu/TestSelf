package com.newland.bi.bigdata.excel;

import com.newland.bi.bigdata.bean.ExcelSheetList;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean isAlignByTitle = false;//字段个数是否参考标题

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
                System.out.println(path + ExcelCommons.NOT_EXCEL_FILE);
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
        } else if (ExcelCommons.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
            if (is == null) return new XSSFWorkbook();
            else return new XSSFWorkbook(is);
        } else {
            return null;
        }
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
                                rowlist.add(getValue(cell));
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
    private String getValue(Cell cell) {
        if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
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
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
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
                System.out.println(path + ExcelCommons.NOT_EXCEL_FILE);
            }
        }
        return -1;
    }

}
