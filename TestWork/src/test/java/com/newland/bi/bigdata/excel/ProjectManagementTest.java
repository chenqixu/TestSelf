package com.newland.bi.bigdata.excel;

import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectManagementTest {

    private ProjectManagement projectManagement = new ProjectManagement();

    @Test
    public void run() {
//        projectManagement.run("d:\\Work\\WEB\\资源服务化\项目管理\\rsmgr20190830.xlsx");
//        projectManagement.run("d:\\Work\\WEB\\资源服务化\项目管理\\rsmgr20190906.xlsx");
//        projectManagement.run("d:\\Work\\WEB\\资源服务化\项目管理\\rsmgr20190912.xlsx");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileName = "d:\\Work\\WEB\\资源服务化\\项目管理\\rsmgr" + simpleDateFormat.format(new Date()) + ".xlsx";
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            projectManagement.run(fileName);
        }
    }
}