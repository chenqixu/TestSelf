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
        read_path = "D:\\tmp\\excel\\tmp_excel_20250327_1.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            if (sheet.getSheetName().equals("S1")) {
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
                // 打印v3中有多少v4
//                for (PCBean _pcBeanLv0 : root.getChildList()) {
//                    System.out.println("Lv1=" + _pcBeanLv0);
//                    for (PCBean _pcBeanLv1 : _pcBeanLv0.getChildList()) {
//                        System.out.println("Lv2=" + _pcBeanLv1);
//                        for (PCBean _pcBeanLv2 : _pcBeanLv1.getChildList()) {
//                            StringBuilder sb = new StringBuilder();
//                            for (PCBean _pcBeanLv3 : _pcBeanLv2.getChildList()) {
//                                sb.append(_pcBeanLv3).append("、");
//                            }
//                            System.out.println("Lv3=" + _pcBeanLv2 + "：包含" + sb);
//                        }
//                    }
//                }
                // 打印v2中有多少v3
                for (PCBean _pcBeanLv0 : root.getChildList()) {
                    System.out.println("Lv1=" + _pcBeanLv0);
                    for (PCBean _pcBeanLv1 : _pcBeanLv0.getChildList()) {
                        System.out.println("Lv2=" + _pcBeanLv1);
                        StringBuilder sb = new StringBuilder();
                        for (PCBean _pcBeanLv2 : _pcBeanLv1.getChildList()) {
                            sb.append(_pcBeanLv2).append("、");
                        }
                        System.out.println("Lv2=" + _pcBeanLv1 + "：包含" + sb);
                    }
                }
            }
        }
    }

    /**
     * 信息补全
     *
     * @throws Exception
     */
    @Test
    public void buquan() throws Exception {
        String name = "标签一期-软件上线合格证书 - 副本.xlsx";
        String read_path = "d:\\Work\\实时\\标签大宽表\\标签平台\\交维\\主要功能验证报告、软件上线合格证书\\" + name;
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            if (sheet.getSheetName().equals("Sheet1")) {
                String his = null;
                String current = null;
                for (int i = 2; i < sheet.getSheetList().size(); i++) {
                    current = sheet.getSheetList().get(i).get(5);
                    if (his == null) {
                        his = current;
                    }
                    if (current == null) {
                        current = his;
                    } else {
                        his = current;
                    }
//                    System.out.println(String.format("[5]%s[6]%s", sheet.getSheetList().get(i).get(5), sheet.getSheetList().get(i).get(6)));
//                    System.out.println(String.format("[5]%s[6]%s", current, sheet.getSheetList().get(i).get(6)));
                    System.out.println(current);
                }
            }
        }
    }

    /**
     * 提示词生成
     *
     * @throws Exception
     */
    @Test
    public void buildPrompt() throws Exception {
        String tmp = "请输出一份功能验证报告，需要输出如下4点：预置条件、测试步骤、界面预期输出、数据预期输出。\n" +
                "注意1：有界面就不需要数据预期输出，此时数据预期输出填写为\"本功能仅涉及前端，不涉及数据输出\"，没界面就需要数据预期输出，此时界面预期输出填写为\"本功能仅涉及后端，不涉及界面输出\"。\n" +
                "注意2：有几个功能就写几个功能，不要造功能。\n" +
                "注意3：本功能仅涉及%s。\n" +
                "输出格式如下：\n" +
                "#xxxxx\n" +
                "##预置条件\n" +
                "xxxxx\n" +
                "##测试步骤\n" +
                "功能1：xxxxx\n" +
                "功能2：xxxxx\n" +
                "功能……：xxxxx\n" +
                "##界面预期输出\n" +
                "功能1：xxxxx\n" +
                "功能2：xxxxx\n" +
                "功能……：xxxxx\n" +
                "##数据预期输出\n" +
                "功能1：xxxxx\n" +
                "功能2：xxxxx\n" +
                "功能……：xxxxx\n" +
                "参考如下：\n" +
                "#菜单管理\n" +
                "##预置条件\n" +
                "用户拥有“菜单管理”菜单权限\n" +
                "##测试步骤\n" +
                "菜单查询：\n" +
                "1、点击『菜单管理』\n" +
                "2、页面左侧，填写[名称]关键字\n" +
                "3、点击【查询】\n" +
                "菜单增加：\n" +
                "1、点击『菜单管理』\n" +
                "2、页面左侧，对已有目录图标根菜单右击，【新建】\n" +
                "3、配置菜单基本信息\n" +
                "菜单编辑：\n" +
                "1、点击『菜单管理』\n" +
                "2、菜单栏选择点击新增的菜单，在右侧详细信息下，点击【编辑】\n" +
                "3、配置该菜单基本信息，点击【保存】\n" +
                "菜单删除：\n" +
                "1、点击『菜单管理』\n" +
                "2、对新增的子菜单目录右击，【删除】，【确定】\n" +
                "##界面预期输出\n" +
                "菜单查询：可以正常查询到菜单。\n" +
                "菜单增加：可以正常增加菜单。\n" +
                "菜单编辑：可以正常编辑菜单。\n" +
                "菜单删除：可以正常删除菜单。\n" +
                "##数据预期输出\n" +
                "本功能仅涉及前端，不涉及数据输出。\n";

        String name = "标签一期-软件上线合格证书 - 1.xlsx";
//        name = "工作簿1.xlsx";
        String read_path = "d:\\Work\\实时\\标签大宽表\\标签平台\\交维\\主要功能验证报告、软件上线合格证书\\" + name;
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);

        String write_name = "工作簿2.xlsx";
        String write_path = "d:\\Work\\实时\\标签大宽表\\标签平台\\交维\\主要功能验证报告、软件上线合格证书\\" + write_name;
        List<ExcelSheetList> writeExcelSheetLists = new ArrayList<>();
        ExcelSheetList writeExcelSheetList = new ExcelSheetList();
        writeExcelSheetList.setSheetName("Sheet");
        writeExcelSheetLists.add(writeExcelSheetList);

        for (ExcelSheetList sheet : excelSheetLists) {
            if (sheet.getSheetName().equals("Sheet1")) {
                String his = null;
                String current = null;
                String his_type = null;
                String current_type = null;
                StringBuilder sb = new StringBuilder();
                List<TypeBean> typelist = new ArrayList<>();
                int j = 0;
                for (int i = 2; i < sheet.getSheetList().size(); i++) {
                    current = sheet.getSheetList().get(i).get(5);
                    String gongneng = sheet.getSheetList().get(i).get(6);
                    String gongnengdesc = sheet.getSheetList().get(i).get(7);

                    current_type = sheet.getSheetList().get(i).get(8);
                    if (his_type == null) {
                        his_type = current_type;
                    }
                    if (current_type == null) {
                        current_type = his_type;
                    } else {
                        his_type = current_type;
                        typelist.add(new TypeBean(i, his_type, current));
                    }

                    if (his == null) {
                        his = current;
                    }
                    if (current == null) {
                        current = his;
                        if (gongnengdesc.endsWith("。") || gongnengdesc.endsWith(".")) {
                        } else {
                            gongnengdesc = gongnengdesc + "。";
                        }
                        sb.append(gongneng).append("：\"").append(gongnengdesc).append("\"\n");
                    } else {
                        j++;
                        his = current;
                        if (sb.length() > 0) {
//                            System.out.println(sb + String.format(tmp, typelist.get(j - 2).getType()));
                            writeExcelSheetList
                                    .newLine()
                                    .addColumn(typelist.get(j - 2).getName())
                                    .addColumn(sb + String.format(tmp, typelist.get(j - 2).getType()));
                        }
                        sb = new StringBuilder();
                        sb.append(current).append("，实现了以下功能，\n");
                        if (gongnengdesc.endsWith("。") || gongnengdesc.endsWith(".")) {
                        } else {
                            gongnengdesc = gongnengdesc + "。";
                        }
                        sb.append(gongneng).append("：\"").append(gongnengdesc).append("\"\n");
                    }
                }
                if (sb.length() > 0) {
//                    System.out.println(sb + String.format(tmp, typelist.get(typelist.size() - 1).getType()));
                    writeExcelSheetList
                            .newLine()
                            .addColumn(typelist.get(typelist.size() - 1).getName())
                            .addColumn(sb + String.format(tmp, typelist.get(typelist.size() - 1).getType()));
                }
                excelUtils.writeExcel(write_path, writeExcelSheetLists);
            }
        }
    }
}