package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.UserBehavior;

/**
 * 开机时延解析
 *
 * @author chenqixu
 */
public class Event17Parse extends AbstractBodyParse {

    protected static int code = 17;

    @Override
    public BodyInfo parse(String leftoverstr) {
        UserBehavior userBehavior = new UserBehavior();
        userBehavior.setBootTimeDelay(leftoverstr);
        bodyInfo.setBody(userBehavior);
        return bodyInfo;
    }
}
