package com.cqx.common.utils.system;

import org.junit.Test;

import java.io.File;

public class JarUtilTest {

    @Test
    public void loadJar() throws Exception {
        JarUtil.loadJar(new File("D:\\Document\\Workspaces\\Git\\TestSelf\\TestCommon\\target\\TestCommon-1.0.0-sources.jar"));
    }
}