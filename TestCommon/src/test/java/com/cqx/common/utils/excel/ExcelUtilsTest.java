package com.cqx.common.utils.excel;

import com.cqx.common.bean.system.PCBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExcelUtilsTest {

    private ExcelUtils excelUtils;

    @Before
    public void setUp() {
        excelUtils = new ExcelUtils(true);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * 重复内容验证
     *
     * @throws IOException
     */
    @Test
    public void checkRepeat() throws IOException {
        String read_path = "d:\\Work\\CVS\\BI\\SSC\\开发1组\\流程管理\\inter_plat_info_模板.xls";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        String name = "[%s]_[%s]_[%s]";
        List<String> contents = new ArrayList<>();
        for (ExcelSheetList sheet : excelSheetLists) {
            if (sheet.getSheetName().equals("Sheet1")) {
                for (List<String> content : sheet.getSheetList()) {
                    contents.add(String.format(name, content.get(3), content.get(4), content.get(6)));
                }
            }
        }
        // 分组，计数
        Map<String, Long> result = contents.stream().collect(
                Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // 等于having count(1)>1
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry);
            }
        }
    }

    @Test
    public void readLabelRJZCJXList() throws IOException {
        String read_path = "D:\\Work\\CVS\\BI\\SSC\\项目管理\\工程项目(未立项)\\标签基础平台\\验收\\cosmic\\福建公司2024年企业级数据中台标签能力提升开发-软件资产解析清单.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            if (sheet.getSheetName().equals("软件资产清单")) {
                PCBean root = new PCBean("root");

                PCBean pcBeanLv0 = new PCBean();
                PCBean pcBeanLv1 = new PCBean();
                PCBean pcBeanLv2 = new PCBean();
                PCBean pcBeanLv3 = new PCBean();
                String currentLv0 = null;
                String hisLv0 = null;
                String currentLv1 = null;
                String hisLv1 = null;
                String currentLv2 = null;
                String hisLv2 = null;
                String currentLv3 = null;
                String hisLv3 = null;
                for (List<String> content : sheet.getSheetList()) {
                    // 从0，1，2，3都判断一遍
                    // 当前不等于历史就做切换
                    currentLv0 = content.get(0);
                    currentLv1 = content.get(1);
                    currentLv2 = content.get(2);
                    currentLv3 = content.get(3);

                    if (hisLv0 == null || (currentLv0 != null && !hisLv0.contains(currentLv0))) {// 切换
                        hisLv0 = currentLv0;
                        pcBeanLv0 = root.addChild(currentLv0);
                    }

                    if (hisLv1 == null || (currentLv1 != null && !hisLv1.contains(currentLv1))) {// 切换
                        hisLv1 = currentLv1;
                        pcBeanLv1 = pcBeanLv0.addChild(currentLv1);
                    }

                    if (hisLv2 == null || (currentLv2 != null && !hisLv2.contains(currentLv2))) {// 切换
                        hisLv2 = currentLv2;
                        pcBeanLv2 = pcBeanLv1.addChild(currentLv2);
                    }

                    if (hisLv3 == null || (currentLv3 != null && !hisLv3.contains(currentLv3))) {// 切换
                        hisLv3 = currentLv3;
                        pcBeanLv3 = pcBeanLv2.addChild(currentLv3);
                    }
                }
                for (PCBean _pcBeanLv0 : root.getChildList()) {
                    System.out.println("Lv1=" + _pcBeanLv0);
                    for (PCBean _pcBeanLv1 : _pcBeanLv0.getChildList()) {
                        System.out.println("Lv2=" + _pcBeanLv1);
                        for (PCBean _pcBeanLv2 : _pcBeanLv1.getChildList()) {
                            StringBuilder sb = new StringBuilder();
                            for (PCBean _pcBeanLv3 : _pcBeanLv2.getChildList()) {
                                sb.append(_pcBeanLv3).append("、");
                            }
                            System.out.println("Lv3=" + _pcBeanLv2 + "：包含" + sb);
                        }
                    }
                }
            }
        }
    }
}