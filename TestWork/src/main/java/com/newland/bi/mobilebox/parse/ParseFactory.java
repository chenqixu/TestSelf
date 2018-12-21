package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.HeaderInfo;
import com.newland.bi.mobilebox.bean.MobileBoxInfo;
import com.newland.bi.mobilebox.exception.MobileBoxException;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析工厂
 *
 * @author chenqixu
 */
public class ParseFactory {

    private static Map<Integer, AbstractBodyParse> bodyParseMap = new HashMap<>();

    static {
        bodyParseMap.put(Event1Parse.code, new Event1Parse());
        bodyParseMap.put(Event3Parse.code, new Event3Parse());
        bodyParseMap.put(Event4Parse.code, new Event4Parse());
    }

    private static ParseFactory parseFactory = new ParseFactory();

    private ParseFactory() {
    }

    public static ParseFactory getInstance() {
        synchronized (parseFactory) {
            if (parseFactory == null) {
                synchronized (parseFactory) {
                    parseFactory = new ParseFactory();
                }
            }
        }
        return parseFactory;
    }

    public MobileBoxInfo parseLogValue(String logValue) throws MobileBoxException {
        MobileBoxInfo mobileBoxInfo = new MobileBoxInfo();
        HeaderParse headerParse = new HeaderParse(logValue);
        HeaderInfo headerInfo = headerParse.getParseObj();
        BodyInfo bodyInfo = bodyParseMap.get(headerInfo.getCode()).parse(headerParse.getLeftoverstr());
        mobileBoxInfo.setHeaderInfo(headerInfo);
        mobileBoxInfo.setBodyInfo(bodyInfo);
        return mobileBoxInfo;
    }
}
