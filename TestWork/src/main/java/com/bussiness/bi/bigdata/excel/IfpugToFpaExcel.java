package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * IFPUG转FPA提示词
 *
 * @author chenqixu
 */
public class IfpugToFpaExcel {
    private static final Logger logger = LoggerFactory.getLogger(IfpugToFpaExcel.class);

    public static void main(String[] args) {
        new IfpugToFpaExcel().readExcel("d:\\Work\\CVS\\BI\\SSC\\项目管理\\工程项目(未立项)\\标签基础平台\\投资框架任务包\\子任务包-两级互动管理建设\\工作簿1.xlsx"
                , "d:\\Work\\CVS\\BI\\SSC\\项目管理\\工程项目(未立项)\\标签基础平台\\投资框架任务包\\子任务包-两级互动管理建设\\prom\\");
    }

    public void readExcel(String path, String savePath) {
        List<ExcelSheetList> list;
        ExcelUtils eu = new ExcelUtils();
        FileUtil fu = new FileUtil();
        int num = 1;
        try {
            list = eu.readExcel(path);
            if (list != null) {
                // 循环sheet
                for (ExcelSheetList excelSheetList : list) {
                    if (excelSheetList.getSheetName().equals("Sheet1")) {
                        AtomicBoolean isFirst = new AtomicBoolean(true);
                        for (List<String> contents : excelSheetList.getSheetList()) {
                            String FunctionPointName = contents.get(5);
                            String FunctionPointDesc = contents.get(6);
                            String AssetClass = contents.get(7);
                            String ElementCorrespondingValue = contents.get(8);
                            String NumberOfElements = contents.get(10);
                            // 跳过第一行
                            if (!isFirst.getAndSet(false)) {
                                if (FunctionPointName == null) {
                                    break;
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append("## 任务\n" + "请参考以下内容，将IFPUG格式的功能点描述，转换成FPA格式的功能点描述\n");
                                sb.append("## 输出格式\n" + "内容\n\n");
                                sb.append("## IFPUG格式-功能点名称\n");
                                sb.append(FunctionPointName);
                                sb.append("\n");
                                sb.append("## IFPUG格式-功能点描述\n");
                                sb.append(FunctionPointDesc);
                                sb.append("\n");
                                sb.append("## IFPUG格式-资产类别\n");
                                sb.append(AssetClass);
                                sb.append("\n");
                                sb.append("## IFPUG格式-要素对应值\n");
                                sb.append(ElementCorrespondingValue);
                                sb.append("\n");
                                sb.append("## IFPUG格式-要素数量\n");
                                sb.append(NumberOfElements);
                                sb.append("\n");
                                sb.append("## 举例\n" +
                                        "- IFPUG格式功能点描述\n" +
                                        "新增一个复合标签列表查询类页面，输入复合标签信息，呈现复合标签基础情况的查询结果，包括复合标签名称、主体、状态、创建人员、归属区域等情况。\n" +
                                        "- 转换后的FPA格式功能点描述\n" +
                                        "提供复合标签列表查询页面，支持输入复合标签相关信息作为查询条件，并以列表形式呈现包含复合标签名称、主体、状态、创建人员、归属区域等基础信息 。\n");
                                try {
                                    fu.createFile(savePath + num++ + "-" + FunctionPointName + ".txt", "UTF-8");
                                    fu.write(sb.toString());
                                } finally {
                                    fu.closeWrite();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
