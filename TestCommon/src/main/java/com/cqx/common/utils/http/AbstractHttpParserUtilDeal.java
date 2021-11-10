package com.cqx.common.utils.http;

import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * AbstractHttpParserUtilDeal
 *
 * @author chenqixu
 */
public abstract class AbstractHttpParserUtilDeal implements HttpParserUtil.IHttpParserUtilDeal {

    public abstract void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException;

    public void noDataDeal() {
    }
}
