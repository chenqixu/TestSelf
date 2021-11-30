package com.cqx.common.utils.http;

import com.cqx.common.utils.list.ListHelper;
import org.jsoup.nodes.Document;
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
        String imgSavePath = "E:/Photo/Comic/爬虫/eromanga-kamuro.com/1.jpg";
        String resource1 = HttpParserUtil.getLastResource(imgUrl, new int[]{1});
        String resource2 = HttpParserUtil.getLastResource(imgUrl, new int[]{2, 1});
        logger.info("resource1：{}，resource2：{}", resource1, resource2);
        // 保存图片
        long fileSize = httpParserUtil.download(imgUrl
                , timeout
                , imgSavePath);
        logger.info("img size：{}", HttpParserUtil.getFormatSize(fileSize, EnumSizeUnit.BYTE));
    }

    // アーカイブ - 归档，archives-2
    @Test
    public void parserAllMonth() throws IOException {
        //https://eromanga-kamuro.com/date/2021/11
        //https://eromanga-kamuro.com/date/2021/10
        //https://eromanga-kamuro.com/date/2021/09
        //https://eromanga-kamuro.com/date/2021/08
        //https://eromanga-kamuro.com/date/2021/07
        //https://eromanga-kamuro.com/date/2021/06
        //https://eromanga-kamuro.com/date/2021/05
        //https://eromanga-kamuro.com/date/2021/04
        //https://eromanga-kamuro.com/date/2021/03
        //https://eromanga-kamuro.com/date/2021/02
        //https://eromanga-kamuro.com/date/2021/01
        //https://eromanga-kamuro.com/date/2020/12
        //https://eromanga-kamuro.com/date/2020/11
        //https://eromanga-kamuro.com/date/2020/10
        //https://eromanga-kamuro.com/date/2020/09
        //https://eromanga-kamuro.com/date/2020/08
        //https://eromanga-kamuro.com/date/2020/07
        //https://eromanga-kamuro.com/date/2020/06
        //https://eromanga-kamuro.com/date/2020/05
        //https://eromanga-kamuro.com/date/2020/04
        //https://eromanga-kamuro.com/date/2020/03
        //https://eromanga-kamuro.com/date/2020/02
        //https://eromanga-kamuro.com/date/2020/01
        //https://eromanga-kamuro.com/date/2019/12
        //https://eromanga-kamuro.com/date/2019/11
        //https://eromanga-kamuro.com/date/2019/10
        //https://eromanga-kamuro.com/date/2019/09
        // 本地 file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/date_2021_09.htm
        // 远程 https://eromanga-kamuro.com/date/2021/07
        // 解析月份
        Elements elements = httpParserUtil.parserGetElements(
                "https://eromanga-kamuro.com/date/2021/07"
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
                                String href = child.attr("href");
                                String year = href.substring(href.length() - 7, href.length() - 3);
                                String mm = href.substring(href.length() - 2);
                                logger.info("href：{}，isEnd：{}，year：{}，mm：{}", href, isEnd, year, mm);
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
        // 可能有的没有地址 file:///E:/Photo/Comic/爬虫/eromanga-kamuro.com/114454.htm
        // 可能有的没有地址 https://eromanga-kamuro.com/%e5%92%b2%e8%89%af%e5%b0%86%e5%8f%b8/114454
        // 404 https://eromanga-kamuro.com/date/2232
        // ccs_query ".alignnone"
        httpParserUtil.parser("https://eromanga-kamuro.com/date/2232"
                , timeout
                , ".alignnone"
                , null
                , null
                , new AbstractHttpParserUtilDeal() {
                    @Override
                    public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) {
                        String strUrl = child.attr("src");
                        logger.info("[src]：{}，[src is null]：{}，[src length]：{}，isEnd：{}"
                                , strUrl, strUrl == null, strUrl != null ? strUrl.length() : 0, isEnd);
                        if (strUrl == null || strUrl.length() == 0) {
                            logger.info("【图片】地址不存在，无法下载，跳过");
                        }
                    }

                    @Override
                    public void noDataDeal() {
                        logger.info("没有资源");
                    }
                }
        );
    }

    @Test
    public void dealNullDocument() {
        Document document = new Document("Null");
        logger.info("{}", document.select(".a").size());
    }
}
