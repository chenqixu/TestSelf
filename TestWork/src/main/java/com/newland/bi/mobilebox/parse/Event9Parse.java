package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.EpgTag;

/**
 * 界面布局xml解析
 *
 * @author chenqixu
 */
public class Event9Parse extends AbstractBodyParse {

    protected static int code = 9;

    @Override
    public BodyInfo parse(String leftoverstr) {
        EpgTag epgTag = new EpgTag();
        // TODO: post解析xml
        bodyInfo.setBody(epgTag);
        return bodyInfo;
    }
}
