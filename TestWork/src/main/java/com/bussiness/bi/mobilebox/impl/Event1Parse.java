package com.bussiness.bi.mobilebox.impl;

import com.bussiness.bi.mobilebox.bean.BodyInfo;
import com.bussiness.bi.mobilebox.bean.DeviceInfo;
import com.bussiness.bi.mobilebox.parse.AbstractBodyParse;
import com.bussiness.bi.mobilebox.utils.BodyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户ID解析
 *
 * @author chenqixu
 */
@BodyImpl
public class Event1Parse extends AbstractBodyParse {

    private static Logger logger = LoggerFactory.getLogger(Event1Parse.class);

    @Override
    public int getCode() {
        return 1;
    }

    @Override
    public BodyInfo parse(String leftoverstr) {
        logger.info("leftoverstr：{}", leftoverstr);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setUserID(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }

}
