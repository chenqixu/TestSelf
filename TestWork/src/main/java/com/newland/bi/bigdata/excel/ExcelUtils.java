package com.newland.bi.bigdata.excel;

import com.newland.bi.bigdata.bean.ExcelSheetList;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
                if (ExcelCommons.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(path);
                } else if (ExcelCommons.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(path);
                }
            } else {
                System.out.println(path + ExcelCommons.NOT_EXCEL_FILE);
            }
        }
        return null;
    }

    /**
     * Read the Excel 2010
     *
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    private List<ExcelSheetList> readXlsx(String path) throws IOException {
        InputStream is = null;
        List<ExcelSheetList> resultlist = new ArrayList<ExcelSheetList>();
        try {
            is = new FileInputStream(path);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            // 循环每一页，并处理当前页
            for (XSSFSheet xssfSheet : xssfWorkbook) {
                if (xssfSheet == null) {
                    continue;
                }
                ExcelSheetList esl = new ExcelSheetList();
                // 设置sheetName
                esl.setSheetName(xssfSheet.getSheetName());
                List<List<String>> sheetlist = new ArrayList<List<String>>();
                // 设置sheet内容
                esl.setSheetList(sheetlist);
                // 处理当前页，循环每一行
                for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                    if (xssfRow != null) {
                        int minColIx = xssfRow.getFirstCellNum();
                        int maxColIx = xssfRow.getLastCellNum();
                        List<String> rowlist = new ArrayList<String>();
                        // 遍历该行，获取每个cell元素
                        for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                            XSSFCell cell = xssfRow.getCell(colIx);
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
     * Read the Excel 2003-2007
     *
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    private List<ExcelSheetList> readXls(String path) throws IOException {
        InputStream is = null;
        List<ExcelSheetList> resultlist = new ArrayList<ExcelSheetList>();
        try {
            is = new FileInputStream(path);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
            // 循环每一页，并处理当前页
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                ExcelSheetList esl = new ExcelSheetList();
                // 设置sheetName
                esl.setSheetName(hssfSheet.getSheetName());
                List<List<String>> sheetlist = new ArrayList<List<String>>();
                // 设置sheet内容
                esl.setSheetList(sheetlist);
                // 处理当前页，循环每一行
                for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow != null) {
                        int minColIx = hssfRow.getFirstCellNum();
                        int maxColIx = hssfRow.getLastCellNum();
                        List<String> rowlist = new ArrayList<String>();
                        // 遍历该行，获取每个cell元素
                        for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                            HSSFCell cell = hssfRow.getCell(colIx);
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

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellStyle().getDataFormat() > 0) {
            return simpleDateFormat.format(xssfRow.getDateCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellStyle().getDataFormat() > 0) {
            return simpleDateFormat.format(hssfCell.getDateCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
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
            return path.substring(path.lastIndexOf(ExcelCommons.POINT) + 1, path.length());
        }
        return ExcelCommons.EMPTY;
    }

    /**
     * Write the Excel 2003-2007
     *
     * @param path
     * @param excelSheetLists
     * @return
     * @throws IOException
     */
    private int writeXls(String path, List<ExcelSheetList> excelSheetLists) throws IOException {
        FileOutputStream stream = null;
        int result = 0;
        try {
            //创建Excel文件薄
            HSSFWorkbook workbook = new HSSFWorkbook();
            //数据加载
            for (int i = 0; i < excelSheetLists.size(); i++) {
                String sheetName = excelSheetLists.get(i).getSheetName();
                List<List<String>> rowList = excelSheetLists.get(i).getSheetList();
                //创建工作表sheeet
                HSSFSheet sheet = workbook.createSheet(sheetName);
                for (int j = 0; j < rowList.size(); j++) {
                    //创建行
                    HSSFRow row = sheet.createRow(j);
                    List<String> cellList = rowList.get(j);
                    for (int z = 0; z < cellList.size(); z++) {
                        //创建列
                        HSSFCell cell = row.createCell(z);
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

    private int writeXlsx(String path, List<ExcelSheetList> excelSheetLists) throws IOException {
        FileOutputStream stream = null;
        int result = 0;
        try {
            //创建Excel文件薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            //数据加载
            for (int i = 0; i < excelSheetLists.size(); i++) {
                String sheetName = excelSheetLists.get(i).getSheetName();
                List<List<String>> rowList = excelSheetLists.get(i).getSheetList();
                //创建工作表sheeet
                XSSFSheet sheet = workbook.createSheet(sheetName);
                for (int j = 0; j < rowList.size(); j++) {
                    //创建行
                    XSSFRow nextrow = sheet.createRow(j);
                    List<String> cellList = rowList.get(j);
                    for (int z = 0; z < cellList.size(); z++) {
                        //创建列
                        XSSFCell cell = nextrow.createCell(z);
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

    public int writeExcel(String path, List<ExcelSheetList> excelSheetLists) throws IOException {
        if (path == null || ExcelCommons.EMPTY.equals(path)) {
            return -1;
        } else {
            String postfix = getPostfix(path);
            if (!ExcelCommons.EMPTY.equals(postfix)) {
                if (ExcelCommons.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return writeXls(path, excelSheetLists);
                } else if (ExcelCommons.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return writeXlsx(path, excelSheetLists);
                }
            } else {
                System.out.println(path + ExcelCommons.NOT_EXCEL_FILE);
            }
        }
        return -1;
    }

}
