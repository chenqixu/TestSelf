package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.net.bean.NetBean;
import com.newland.bi.bigdata.net.bean.NetBody;
import com.newland.bi.bigdata.net.bean.NetCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * NetUtils
 *
 * @author chenqixu
 */
public class NetUtils {

    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public static NetBean buildString(String value) {
        return NetBean.newbuilder()
                .setHead(NetCode.SEND_STRING)
                .setNetBody(new NetBody(value));
    }

    public static NetBean buildClose() {
        return NetBean.newbuilder()
                .setHead(NetCode.END_TAG);
    }

    public static Object getValue(NetBean netBean) {
        return netBean.getNetBody().getValue();
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
