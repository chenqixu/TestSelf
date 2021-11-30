package com.cqx.common.utils.compress.tar;

import org.junit.Test;

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
}