package com.newland.bi.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	/**
     * read the Excel file
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
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public List<ExcelSheetList> readXlsx(String path) throws IOException {
//        System.out.println(ExcelCommons.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<ExcelSheetList> resultlist = new ArrayList<ExcelSheetList>();
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
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                	int minColIx = xssfRow.getFirstCellNum();
                	int maxColIx = xssfRow.getLastCellNum();
                	List<String> rowlist = new ArrayList<String>();
                	// 遍历该行，获取每个cell元素
                	for (int colIx = minColIx; colIx<maxColIx; colIx++){
                		XSSFCell cell = xssfRow.getCell(colIx);
                		if (cell == null) {
                			continue;
                		}
                		rowlist.add(getValue(cell));
                	}
                	sheetlist.add(rowlist);
                }
            }
            resultlist.add(esl);
        }
        return resultlist;
    }

    /**
     * Read the Excel 2003-2007
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public List<ExcelSheetList> readXls(String path) throws IOException {
//        System.out.println(ExcelCommons.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<ExcelSheetList> resultlist = new ArrayList<ExcelSheetList>();
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
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                	int minColIx = hssfRow.getFirstCellNum();
                	int maxColIx = hssfRow.getLastCellNum();
                	List<String> rowlist = new ArrayList<String>();
                	// 遍历该行，获取每个cell元素
                	for (int colIx = minColIx; colIx<maxColIx; colIx++){
                		HSSFCell cell = hssfRow.getCell(colIx);
                		if (cell == null) {
                			continue;
                		}
                		rowlist.add(getValue(cell));
                	}
                	sheetlist.add(rowlist);
                }
            }
            resultlist.add(esl);
        }
        return resultlist;
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(ExcelCommons.df.format(xssfRow.getNumericCellValue()));
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {        	
            return String.valueOf(ExcelCommons.df.format(hssfCell.getNumericCellValue()));
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
    
    /**
     * get postfix of the path
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
}
