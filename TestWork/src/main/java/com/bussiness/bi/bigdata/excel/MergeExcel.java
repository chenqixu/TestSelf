package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 合并多余的单元格
 *
 * @author chenqixu
 */
public class MergeExcel {
    private static final Logger logger = LoggerFactory.getLogger(MergeExcel.class);
    private ExcelUtils eu = new ExcelUtils();

    public List<ExcelSheetList> read(String path, String readSheetName, String writeSheetName) throws IOException {
        List<ExcelSheetList> excelSheetLists = new ArrayList<>();
        ExcelSheetList excelSheetList = new ExcelSheetList();
        excelSheetList.setSheetName(writeSheetName);
        excelSheetLists.add(excelSheetList);
        Map<String, List<List<String>>> map = eu.readExcelToMap(path);
        if (map != null) {
            StringBuilder sb = new StringBuilder();
            String oldName = null;
            String d1 = null;
            String d2 = null;
            String d3 = null;
            String d4 = null;
            for (List<String> sheet3 : map.get(readSheetName)) {
                if (sheet3.get(0) == null) {
                    sb.append(sheet3.get(5));
                } else if (oldName != null && sheet3.get(0) != null && !sheet3.get(0).equals(oldName)) {
                    // 切换了才输出
                    logger.info("[0]{}，[1]{}，[2]{}，[3]{}，[4]{}，[5]{}", oldName, d1, d2, d3, d4, sb.toString());
                    excelSheetList.newLine()
                            .addColumn(oldName)
                            .addColumn(d1)
                            .addColumn(d2)
                            .addColumn(d3)
                            .addColumn(d4)
                            .addColumn(sb.toString());
                    sb = new StringBuilder();
                    oldName = sheet3.get(0);
                    d1 = sheet3.get(1);
                    d2 = sheet3.get(2);
                    d3 = sheet3.get(3);
                    d4 = sheet3.get(4);
                }

                if (sheet3.get(0) != null) {
                    oldName = sheet3.get(0);
                    sb.append(sheet3.get(5));
                    d1 = sheet3.get(1);
                    d2 = sheet3.get(2);
                    d3 = sheet3.get(3);
                    d4 = sheet3.get(4);
                }
            }
            // 输出最后一个
            logger.info("[0]{}，[1]{}，[2]{}，[3]{}，[4]{}，[5]{}", oldName, d1, d2, d3, d4, sb.toString());
            excelSheetList.newLine()
                    .addColumn(oldName)
                    .addColumn(d1)
                    .addColumn(d2)
                    .addColumn(d3)
                    .addColumn(d4)
                    .addColumn(sb.toString());
        }
        return excelSheetLists;
    }

    public void merge(String path, List<ExcelSheetList> writeSheet) throws IOException {
        FileUtil.del(path);
        eu.writeExcel(path, writeSheet);
    }
}
