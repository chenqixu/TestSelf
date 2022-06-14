package com.bussiness.bi.bigdata.net.impl;

import com.bussiness.bi.bigdata.net.NetUtils;
import com.bussiness.bi.bigdata.net.bean.NetBean;
import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * AcceptDealNetBean
 *
 * @author chenqixu
 */
public class AcceptDealNetBean extends IAcceptDeal {

    private static Logger logger = LoggerFactory.getLogger(AcceptDealNetBean.class);
    private IClientDeal<NetBean> iClientDeal;

    public AcceptDealNetBean(Socket client) {
        super(client);
    }

    @Override
    public void init() throws Exception {
        iClientDeal = new IClientDealNetBean();
        iClientDeal.initServer(client);
    }

    @Override
    public void run() {
        NetBean content;
        try {
            while (status) {
                while ((content = iClientDeal.read()) != null) {
                    logger.info("client：{}，read content：{}", client, content);
                    //拆包，判断是结束包还是内容包
                    if (NetUtils.isEnd(content)) {
                        logger.info("get end package，break.");
                        close();
                        break;
                    }
                    //封包，发送
                    iClientDeal.write(NetUtils.buildString(NetUtils.getValue(content) + "|replay"));
                }
                SleepUtils.sleepMilliSecond(50);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            iClientDeal.closeServer();
            closeSocket();
        }
    }
}
