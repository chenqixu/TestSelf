package com.newland.bi.bigdata.net.impl;

import com.newland.bi.bigdata.net.NetUtils;
import com.newland.bi.bigdata.net.bean.NetBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * IClientDealNetBean
 *
 * @author chenqixu
 */
public class IClientDealNetBean extends IClientDeal {

    private static Logger logger = LoggerFactory.getLogger(IClientDealNetBean.class);
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    @Override
    public void newReader(InputStream is) throws IOException {
        ois = new ObjectInputStream(is);
    }

    @Override
    public void newWriter(OutputStream os) throws IOException {
        oos = new ObjectOutputStream(os);
    }

    @Override
    protected NetBean read() throws Exception {
        return (NetBean) ois.readObject();
    }

    @Override
    protected void write(Object value) throws Exception {
        oos.writeObject(value);
    }

    @Override
    protected void check(Object value) {
        throwNullException(ois, "Reader is null ! please newReader first !");
        throwNullException(oos, "Writer is null ! please newWriter first !");
        if (!(value instanceof NetBean)) {
            throw new UnsupportedOperationException("不支持的类型！");
        }
    }

    @Override
    public void close() {
        NetUtils.closeStream(ois);
        NetUtils.closeStream(oos);
        try {
            //发送结束标志
            oos.writeObject(NetUtils.buildClose());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
