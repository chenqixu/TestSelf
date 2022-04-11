package com.cqx.common.utils.compress.tar;

import org.junit.Test;

import java.io.IOException;

public class TarUtilTest {

    @Test
    public void unZFile() {
    }

    @Test
    public void unTarFile() {
        TarUtil.builder().unTarFile("d:\\tmp\\chm\\yz_newland_1636621408616_keytab.tar"
                , "d:\\tmp\\chm\\"
                , "user.keytab"
                , "yz_newland.keytab"
                , true
        );
    }

    @Test
    public void unTarGzFile() throws IOException {
        TarUtil.builder().unTarGzFile("d:\\Work\\ETL\\上网日志查询2022\\data\\VoLTE_cx_291713185973300267_3759863131050729424_8618888633869_0_20220315_145812_011052_45_3759860724912480409.cap.tar.gz");
    }
}