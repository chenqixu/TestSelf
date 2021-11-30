package com.cqx.common.utils.http;

import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * AbstractHttpParserUtilDeal
 *
 * @author chenqixu
 */
public abstract class AbstractHttpParserUtilDeal implements HttpParserUtil.IHttpParserUtilDeal {
    private ICallBack iCallBack;

    public AbstractHttpParserUtilDeal() {
    }

    public AbstractHttpParserUtilDeal(ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }

    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException {
        if (iCallBack != null)
            this.iCallBack.callBack();
    }

    public void noDataDeal() {
        if (iCallBack != null)
            this.iCallBack.callBack();
    }

    public void setiCallBack(ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }
}
