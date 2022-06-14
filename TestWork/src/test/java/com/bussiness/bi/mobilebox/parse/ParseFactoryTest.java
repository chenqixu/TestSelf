package com.bussiness.bi.mobilebox.parse;

import com.bussiness.bi.mobilebox.bean.BodyInfo;
import com.bussiness.bi.mobilebox.bean.HeaderInfo;
import com.bussiness.bi.mobilebox.bean.MobileBoxInfo;
import com.bussiness.bi.mobilebox.exception.MobileBoxException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseFactoryTest {

    private static Logger logger = LoggerFactory.getLogger(ParseFactoryTest.class);
    private String logValue = "";

    @Before
    public void upBefore() {
        //用户ID
        logValue = "004403000003101016127868F700D909#KY#1#KY#18250326632";
        //APK版本号
        logValue = "004403FF0015893000008421F14E9D94#KY#3#KY#V6.6.0.3.0591.18.01.19";
        //终端盒子型号
        logValue = "004401FF00203330000100226D420398#KY#4#KY#R3300-L";
        logValue = "0044030000011010180458B42D514146#KY#1#KY#09-18 14:03:54.414###帝王攻略";
        logValue = "004401FF0001181003A460313B3E10F6#KY#1#KY#09-18 14:03:54.414###viptest5###V2.1.2###ZXV10###71440000004734400000749781D0C51A###1###3.9###tv.icntv.ott###ZXB860AV1.1(IBHX-ANDROIDSTBGA-069)###666#ip#wired#wireless";
    }

    @Test
    public void parseLogValueTest() throws MobileBoxException {
        logger.info("logValue：{}", logValue);
        MobileBoxInfo mobileBoxInfo = ParseFactory.getInstance().parseLogValue(logValue);
        HeaderInfo headerInfo = mobileBoxInfo.getHeaderInfo();
        BodyInfo bodyInfo = mobileBoxInfo.getBodyInfo();
        logger.info("headerInfo：{}", headerInfo);
        logger.info("bodyInfo：{}", bodyInfo);
    }
}