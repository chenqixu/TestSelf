package com.cqx.common.utils.excel;

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
}