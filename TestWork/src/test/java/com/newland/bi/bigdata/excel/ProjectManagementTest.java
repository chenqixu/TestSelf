package com.newland.bi.bigdata.excel;

import org.junit.Test;

public class ProjectManagementTest {

    private ProjectManagement projectManagement = new ProjectManagement();

    @Test
    public void run() {
        projectManagement.run("d:\\Work\\WEB\\资源服务化\\rsmgr20190830.xlsx");
    }
}