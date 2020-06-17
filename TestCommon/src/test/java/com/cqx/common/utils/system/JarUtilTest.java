package com.cqx.common.utils.system;

import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JarUtilTest {

    @Test
    public void loadJar() throws Exception {
        Map<String, List<String>> codeMap = JarUtil.loadJar(new File("D:\\Document\\Workspaces\\Git\\TestSelf\\TestCommon\\target\\TestCommon-1.0.0-sources.jar"));
        System.out.println(codeMap.keySet());
        for (String code : codeMap.get("com/cqx/common/utils/file/FileUtil.java")) {
            System.out.println(code);
        }
    }
}