package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CosmicExcel
 *
 * @author chenqixu
 */
public class CosmicExcel {
    private static final Logger logger = LoggerFactory.getLogger(CosmicExcel.class);

    public void readCosmicExcel(String path) {
        List<ExcelSheetList> list;
        ExcelUtils eu = new ExcelUtils();
        try {
            list = eu.readExcel(path);
            if (list != null) {
                // 循环sheet
                for (ExcelSheetList excelSheetList : list) {
                    if (excelSheetList.getSheetName().equals("COSMIC评估模型")) {
                        String header = null;
                        String desc = null;
                        StringBuilder content = new StringBuilder();
                        AtomicBoolean isFirst = new AtomicBoolean(true);
                        for (List<String> contents : excelSheetList.getSheetList()) {
                            String s0 = contents.get(2);
                            String s1 = contents.get(4);
                            String s2 = contents.get(5);
                            if (!isFirst.getAndSet(false)) {
                                if (header == null) {
                                    header = s0;
                                    content.delete(0, content.length());
                                    content.append(s1).append("。\r\n具体内容：").append(s2).append("；");
                                } else {
                                    if (s1 == null) {
                                        content.append(s2).append("；");
                                    } else {
                                        System.out.println(String.format("%s\r\n【功能描述】\r\n%s", header, content));
                                        header = s0;
                                        content.delete(0, content.length());
                                        content.append(s1).append("。\r\n具体内容：").append(s2).append("；");
                                    }
                                }
                            }
                        }
                        System.out.println(String.format("%s\r\n【功能描述】\r\n%s", header, content));
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
