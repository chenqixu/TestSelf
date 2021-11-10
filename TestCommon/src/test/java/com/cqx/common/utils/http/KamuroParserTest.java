package com.cqx.common.utils.http;

import com.cqx.common.utils.list.ListHelper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * kamuo
 *
 * @author chenqixu
 */
public class KamuroParserTest {
    private static final Logger logger = LoggerFactory.getLogger(KamuroParserTest.class);
    private int timeout = 15000;
    private HttpParserUtil httpParserUtil = new HttpParserUtil();

    @Test
    public void saveImg() throws IOException {
        String imgUrl = "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png";
        String resource1 = HttpParserUtil.getLastResource(imgUrl, new int[]{1});
        String resource2 = HttpParserUtil.getLastResource(imgUrl, new int[]{2, 1});
        logger.info("resource1：{}，resource2：{}", resource1, resource2);
        // 保存图片
        httpParserUtil.download(imgUrl
                , timeout
                , "E:/Photo/Comic/爬虫/eromanga-kamuro.com/1.jpg");
    }

    // アーカイブ - 归档，archives-2
    @Test
    public void parserAllMonth() throws IOException {
        // 解析月份
        Elements elements = httpParserUtil.parserGetElements(
                "file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm"
                , timeout
                , ".widget");
        for (Element element : elements) {
            if (element.id().equals("archives-2")) {
                httpParserUtil.parserElements(
                        new Elements(element)
                        , ListHelper.getInstance(String.class).add("ul li a").get()
                        , null
                        , new AbstractHttpParserUtilDeal() {
                            @Override
                            public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                                logger.info("href：{}，isEnd：{}", child.attr("href"), isEnd);
                            }
                        }
                );
            }
        }
    }

    // お勧めリンクサイト - 网站缩略图，custom_html-2
    @Test
    public void parserWebsiteThumbnail() throws IOException {
        // 按网站缩略图
        Elements elements = httpParserUtil.parserGetElements(
                "file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm"
                , timeout
                , ".widget");
        for (Element element : elements) {
            if (element.id().equals("custom_html-2")) {
                httpParserUtil.parserElements(
                        new Elements(element)
                        , ListHelper.getInstance(String.class).add("ul li").get()
                        , null
                        , new AbstractHttpParserUtilDeal() {
                            @Override
                            public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                                logger.info("text：{}，href：{}，isEnd：{}"
                                        , child.text()
                                        , child.select("a").attr("href")
                                        , isEnd);
                            }
                        }
                );
            }
        }
    }

    // カテゴリー - 分类，categories-2
    @Test
    public void parserType() throws IOException {
        // 按分类解析
        Elements elements = httpParserUtil.parserGetElements(
                "file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm"
                , timeout
                , ".widget");
        for (Element element : elements) {
            if (element.id().equals("categories-2")) {
                httpParserUtil.parserElements(
                        new Elements(element)
                        , ListHelper.getInstance(String.class).add("ul li").get()
                        , null
                        , new AbstractHttpParserUtilDeal() {
                            @Override
                            public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                                logger.info("text：{}，post-count：{}，href：{}，isEnd：{}"
                                        , child.text()
                                        , child.select("span").text()
                                        , child.select("a").attr("href")
                                        , isEnd);
                            }
                        }
                );
            }
        }
    }

    @Test
    public void nextPage() throws IOException {
        // 下一页
        httpParserUtil.parser("file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm"
                , timeout
                , ".pagination-next"
                , ListHelper.getInstance(String.class).add("a").get()
                , null
                , new AbstractHttpParserUtilDeal() {
                    @Override
                    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                        logger.info("[href]：{}，isEnd：{}", child.attr("href"), isEnd);
                    }
                }
        );
    }

    @Test
    public void parserPageAndGetBook() throws IOException {
        // 解析当页，获取所有的书
        httpParserUtil.parser("file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm"
                , timeout
                , ".ect-entry-card"
                , ListHelper.getInstance(String.class).add("a").get()
                , null
                , new AbstractHttpParserUtilDeal() {
                    @Override
                    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                        logger.info("[href]：{}，isEnd：{}", child.attr("href"), isEnd);
                    }
                }
        );
    }

    @Test
    public void parserBookAndGetImg() throws IOException {
        // 解析某本书，获取所有图片
        // 没有资源 https://eromanga-kamuro.com/%e6%9c%aa%e5%88%86%e9%a1%9e/128205
        // 本地测试 file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/128372.htm
        // ccs_query ".alignnone"
        httpParserUtil.parser("https://eromanga-kamuro.com/%e6%9c%aa%e5%88%86%e9%a1%9e/128205"
                , timeout
                , ".alignnone"
                , null
                , null
                , new AbstractHttpParserUtilDeal() {
                    @Override
                    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                        logger.info("[src]：{}，isEnd：{}", child.attr("src"), isEnd);
                    }

                    @Override
                    public void noDataDeal() {
                        logger.info("没有资源");
                    }
                }
        );
    }
}
