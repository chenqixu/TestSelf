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
        // 需要输入2个参数，1：父目录，2：子目录
        if (args.length == 2) {
            // 父目录
            String f_path = "d:\\Work\\CVS\\BI\\SSC\\项目管理\\工程项目(未立项)\\标签基础平台\\投资框架任务包\\";
            // 子目录
            String c_path = "子任务包-两级互动管理建设";
            c_path = "子任务包-政企存量二期集团考核能力建设";
            c_path = "子任务包-全网标签库与四度三维体系建设";
            // 从参数中获取
            f_path = args[0];
            c_path = args[1];
            new IfpugToFpaExcel().readExcel(f_path + c_path + "\\工作簿1.xlsx"
                    , f_path + c_path + "\\prom\\");
        } else {
            System.err.println("需要输入2个参数，1：父目录，2：子目录！");
        }
    }

    public void readExcel(String path, String savePath) {
        // 校验path是否存在
        if (!FileUtil.isFile(path)) {
            System.err.println(String.format("找不到输入文件：%s", path));
            System.exit(-1);
        }
        // 校验savePath是否存在
        if (!FileUtil.isDirectory(savePath)) {
            System.err.println(String.format("找不到输出文件路径：%s", savePath));
            System.exit(-1);
        }
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
                                sb.append("## 输出\n" + "直接输出内容，就是FPA格式的功能点描述，不要输出其他\n\n");
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
//                                sb.append("## 举例\n" +
//                                        "- IFPUG格式功能点描述\n" +
//                                        "新增一个复合标签列表查询类页面，输入复合标签信息，呈现复合标签基础情况的查询结果，包括复合标签名称、主体、状态、创建人员、归属区域等情况。\n" +
//                                        "- 转换后的FPA格式功能点描述\n" +
//                                        "提供复合标签列表查询页面，支持输入复合标签相关信息作为查询条件，并以列表形式呈现包含复合标签名称、主体、状态、创建人员、归属区域等基础信息 。\n");
                                sb.append("## 举例\n" +
                                        "- IFPUG格式功能点描述\n" +
                                        "新增一个“评估模型信息列表展示”查询类页面，输入模型信息，展示标签评估模型基础信息情况的查询结果，包括模型名称、模型类型、模型状态、创建\n" +
                                        "人等情况。\n" +
                                        "- 转换后的FPA格式功能点描述\n" +
                                        "支持展示所有标签监测模型的相关信息，包括展示了模型的基本属性（如名称、类型、对象等），还提供了创建人和创建时间等详细信息，帮助用户快速\n" +
                                        "获取所需的信息并进行有效的管理。\n" +
                                        "## 注意要点\n" +
                                        "不用体现“输入”，“过程处理”，只要描述好这个功能点实现的功能。");
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
