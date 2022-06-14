package com.bussiness.bi.mobilebox.parse;

import com.bussiness.bi.mobilebox.bean.BodyInfo;
import com.bussiness.bi.mobilebox.bean.HeaderInfo;
import com.bussiness.bi.mobilebox.bean.MobileBoxInfo;
import com.cqx.common.utils.system.ClassUtil;
import com.bussiness.bi.mobilebox.exception.MobileBoxException;
import com.bussiness.bi.mobilebox.utils.BodyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 解析工厂
 *
 * @author chenqixu
 */
public class ParseFactory {

    private static Logger logger = LoggerFactory.getLogger(ParseFactory.class);
    private static Map<Integer, AbstractBodyParse> bodyParseMap = new HashMap<>();
    private static ParseFactory parseFactory = new ParseFactory();
    private static ClassUtil<BodyImpl, AbstractBodyParse> classUtil = new ClassUtil();

    static {
        Set<Class<?>> classSet = classUtil.getClassSet("com.newland.bi.mobilebox.impl",
                BodyImpl.class);
        for (Class<?> cls : classSet) {
            try {
                AbstractBodyParse obj = (AbstractBodyParse) cls.newInstance();
                logger.info("code：{}，obj：{}", obj.getCode(), obj);
                bodyParseMap.put(obj.getCode(), obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

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
        AbstractBodyParse abstractBodyParse = bodyParseMap.get(headerInfo.getCode());
        if (abstractBodyParse != null) {
            BodyInfo bodyInfo = abstractBodyParse.parse(headerParse.getLeftoverstr());
            mobileBoxInfo.setHeaderInfo(headerInfo);
            mobileBoxInfo.setBodyInfo(bodyInfo);
        }
        return mobileBoxInfo;
    }
}
