package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.net.bean.NetBean;
import com.newland.bi.bigdata.net.bean.NetBody;
import com.newland.bi.bigdata.net.bean.NetCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * NetUtils
 *
 * @author chenqixu
 */
public class NetUtils {

    public static DecimalFormat df;
    public static final String FILE_CACHE = "d:\\tmp\\data\\";
    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    static {
        // 设置数字格式，保留一位有效小数
        df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    }

    public static NetBean buildString(String value) {
        return NetBean.newbuilder()
                .setHead(NetCode.SEND_STRING)
                .setNetBody(new NetBody(value));
    }

    public static NetBean buildClose() {
        return NetBean.newbuilder()
                .setHead(NetCode.END_TAG);
    }

    public static boolean isEnd(NetBean netBean) {
        return netBean.getHead() == NetCode.END_TAG;
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
