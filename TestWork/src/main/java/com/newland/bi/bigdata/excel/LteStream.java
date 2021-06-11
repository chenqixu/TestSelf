package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;

import java.util.List;

/**
 * LteStream
 *
 * @author chenqixu
 */
public class LteStream {

    private ExcelUtils eu = new ExcelUtils();

    public void run(String path) {
        List<ExcelSheetList> list;
        try {
            list = eu.readExcel(path);
            for (ExcelSheetList excelSheetList : list) {
                for (List<String> stringList : excelSheetList.getSheetList()) {
                    System.out.println("    - name: " + stringList.get(0));
                    System.out.println("      keyWord: " + stringList.get(1));
                    System.out.println("      isDpi: false");
                    System.out.println("      value: test1");
                    System.out.println("      dpirequestField:");
                    System.out.println("      hwField:");
                    System.out.println("      sinkField: test1");
                    System.out.println("      rtmField:");
                    System.out.println("      rtmkey:");
                    System.out.println("      topic:");
                    System.out.println("      bolt: MoveFileBolt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cvsinfo(String path) {
        List<ExcelSheetList> list;
        try {
            list = eu.readExcel(path);
            for (ExcelSheetList excelSheetList : list) {
                if (excelSheetList.getSheetName().equals("batch")) {
                    System.out.println(excelSheetList.getSheetName());
                    for (List<String> stringList : excelSheetList.getSheetList()) {
                        System.out.println("名称：" + stringList.get(0));
                        System.out.println("cvs路径：" + stringList.get(1) + "\\" + stringList.get(0));
                        System.out.println("生成结果：" + stringList.get(0) + "-1.0.jar");
                    }
                } else {
                    System.out.println(excelSheetList.getSheetName());
                    for (List<String> stringList : excelSheetList.getSheetList()) {
                        System.out.println("名称：" + stringList.get(0));
                        System.out.println("cvs路径：" + stringList.get(1) + "\\" + stringList.get(0));
                        System.out.println("生成结果：" + stringList.get(0) + "-1.0.jar");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
