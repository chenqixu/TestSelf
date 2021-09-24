package com.cqx.common.utils.http;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * http解析工具
 *
 * @author chenqixu
 */
public class HttpParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpParserUtil.class);
    private final String LOCAL_HEAD = "file:///";

    public void parser(String url, int timeout, String parentCcsQuery, List<String> childCcsQuerys
            , List<String> attributeKeys, IHttpParserUtilDeal iHttpParserUtilDeal) throws IOException {
        Document doc;
        if (url.startsWith(LOCAL_HEAD)) {
            File input = new File(url.replace(LOCAL_HEAD, ""));
            doc = Jsoup.parse(input, "UTF-8");
        } else {
            Connection conn = Jsoup.connect(url);
            doc = conn.timeout(timeout).get();
        }
        Elements parentElements = doc.select(parentCcsQuery);
        for (Element parent : parentElements) {
            if (childCcsQuerys != null && childCcsQuerys.size() > 0) {
                for (String childCcsQuery : childCcsQuerys) {
                    Elements childs = parent.select(childCcsQuery);
                    for (Element child : childs) {
                        if (iHttpParserUtilDeal != null) {
                            iHttpParserUtilDeal.deal(parent, child, childCcsQuery);
                        } else {
                            print(child, attributeKeys);
                        }
                    }
                }
            } else {
                print(parent, attributeKeys);
            }
        }
    }

    public void parser(String url, int timeout, String ccsQuery, List<String> attributeKeys) throws IOException {
        parser(url, timeout, ccsQuery, null, attributeKeys, null);
    }

    public void parser(String url, String ccsQuery, List<String> attributeKeys) throws IOException {
        parser(url, 5000, ccsQuery, attributeKeys);
    }

    private void print(Element element, List<String> attributeKeys) {
        logger.info("【text】{}", element.text());
        if (attributeKeys != null && attributeKeys.size() > 0) {
            for (String attributeKey : attributeKeys) {
                logger.info("【{} 】{}", attributeKey, element.attr(attributeKey));
            }
        }
    }

    public interface IHttpParserUtilDeal {
        void deal(Element parent, Element child, String childCcsQuery);
    }
}
