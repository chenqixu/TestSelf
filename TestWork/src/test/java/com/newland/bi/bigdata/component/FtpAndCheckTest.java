package com.newland.bi.bigdata.component;

import com.cqx.common.utils.ftp.FtpParamCfg;
import org.junit.Before;
import org.junit.Test;

public class FtpAndCheckTest {
    private FtpAndCheck ftpAndCheck;

    @Before
    public void setUp() {
        FtpParamCfg ftpParamCfg = new FtpParamCfg("10.1.8.204", 22, "edc_base", "fLyxp1s*");
        ftpAndCheck = new FtpAndCheck(ftpParamCfg);
    }

    @Test
    public void listFile() {
        try {
            ftpAndCheck.splitPath("/bi/aigm/xdcopy/A02755,/bi/aigm/dmbasscopy/A02755,/bi/aigm/cbassdata/A02755,/bi/aigm/ali/A02755",
                    "A02755%00DD$6.AVL",
                    "d:\\tmp\\data\\gejie\\");
            ftpAndCheck.splitPath("/bi/aigl/xdcopy/H04308,/bi/aigl/dmbasscopy/H04308,/bi/aigl/cbassdata/H04308",
                    "Interface_Channelplayback_%00HH",
                    "d:\\tmp\\data\\gejie\\");
            ftpAndCheck.splitPath("/bi/aigs/xdcopy/A04406",
                    "D_W_PHOME-R2.002_%00DD_1.1_01.dat",
                    "d:\\tmp\\data\\gejie\\");
        } finally {
            ftpAndCheck.close();
        }
    }

    @Test
    public void patttern() {
        ftpAndCheck.matches("A0275520200531000000.AVL", "A02755%00DD$6.AVL");
        ftpAndCheck.matches("i_11100_20191221_VGOP-R1.6-28303_00_001.dat", "i_11100_%00DD_VGOP-R1.6-28303_??_???.dat");
    }
}