package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtilsTest {

    private ExcelUtils excelUtils;

    @Before
    public void setUp() {
        excelUtils = new ExcelUtils(true);
    }

    @Test
    public void readExcel() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\read.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            System.out.println(sheet.getSheetName());
            if (sheet.getSheetName().equals("Sheet1")) {
                for (List<String> content : sheet.getSheetList()) {
                    System.out.println(content);
                }
            }
        }
    }

    @Test
    public void readExcelXLS() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\设计.xls";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            System.out.println(sheet.getSheetName());
            if (sheet.getSheetName().equals("Sheet1")) {
                for (List<String> content : sheet.getSheetList()) {
                    System.out.println(content);
                }
            }
        }
    }

    @Test
    public void writeExcel() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\设计.xlsx";
        String write_path = "d:\\tmp\\data\\zip\\copy.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        excelUtils.writeExcel(write_path, excelSheetLists);
    }

    @Test
    public void cloneExcel() throws Exception {
        List<String> names = new ArrayList<>();
        names.add("5G套餐清单模型");
        names.add("家庭共享中屏清单模型");
        names.add("5G手机销售清单模型");
        names.add("组网办理清单模型");
        names.add("安防办理清单模型");
        names.add("多形态终端中屏清单模型");
        names.add("宽带竣工清单模型");
        names.add("服务提升(出行)清单模型");
        names.add("魔百盒(受理)清单模型");
        names.add("魔百盒(竣工)清单模型");
        names.add("大屏增值包清单模型");
        names.add("和彩云清单模型");
        names.add("权益清单模型");
        names.add("权益超市-惠生活清单模型");
        names.add("咪咕视频清单模型");
        names.add("视频彩铃清单模型");
        names.add("和玛挪车清单模型");
        names.add("语音遥控器清单模型");
        names.add("健康产品清单模型");
        names.add("和家固话清单模型");
        names.add("全家享清单模型");
        names.add("硬件销售指标清单模型");
        names.add("融合清单模型");
        excelUtils.cloneSheet("d:\\Work\\CVS\\BI\\SSC\\项目管理\\工程项目(未立项)\\实时应用迁移中台\\03_详细设计\\B域-详细设计\\中台实时能力提升建设工程-B域-公共模型层规划(初版).xlsx"
                , 1, names);
    }

    @Test
    public void cloneExcelXls() throws Exception {
        List<String> names = new ArrayList<>();
        names.add("5G");
        names.add("4G");
        names.add("23G");
        excelUtils.cloneSheet("d:\\tmp\\data\\excel\\test1.xls"
                , 0, names);
    }
}