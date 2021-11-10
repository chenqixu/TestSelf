package com.cqx.common.utils.http;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;

/**
 * http解析工具
 *
 * @author chenqixu
 */
public class HttpParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpParserUtil.class);
    private final String LOCAL_HEAD = "file:///";

    /**
     * 截取url
     *
     * @param url    网址
     * @param indexs 截取的索引
     * @return 返回截取内容
     */
    public static String getLastResource(String url, int[] indexs) {
        if (url != null && url.length() > 0) {
            String decodeUrl;// 解码
            StringBuilder sb = new StringBuilder();
            try {
                decodeUrl = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new NullPointerException("传入的url解析的时候发生了异常！" + e.getMessage());
            }
            String[] urls = decodeUrl.split("/");
            for (int index : indexs) {
                if (urls.length >= index) {
                    sb.append(urls[urls.length - index]);
                } else {
                    throw new NullPointerException("要获取的资源[" + index + "]不存在！资源长度[" + urls.length + "]");
                }
            }
            return sb.toString();
        } else {
            throw new NullPointerException("传入的url为空！");
        }
    }

    /**
     * 通过css获取Elements
     *
     * @param url     网址
     * @param timeout ccs条件
     * @return Elements
     * @throws IOException
     */
    public Elements parserGetElements(String url, int timeout, String parentCcsQuery) throws IOException {
        Document doc;
        if (url.startsWith(LOCAL_HEAD)) {
            File input = new File(url.replace(LOCAL_HEAD, ""));
            doc = Jsoup.parse(input, "UTF-8");
        } else {
            doc = connUrlGetDoc(url, timeout);
        }
        return doc.select(parentCcsQuery);
    }

    /**
     * 元素解析
     *
     * @param parentElements
     * @param childCcsQuerys
     * @param attributeKeys
     * @param iHttpParserUtilDeal
     * @throws IOException
     */
    public void parserElements(Elements parentElements, List<String> childCcsQuerys
            , List<String> attributeKeys, IHttpParserUtilDeal iHttpParserUtilDeal) throws IOException {
        for (int i = 0; i < parentElements.size(); i++) {
            Element parent = parentElements.get(i);
            if (childCcsQuerys != null && childCcsQuerys.size() > 0) {
                for (String childCcsQuery : childCcsQuerys) {
                    Elements childs = parent.select(childCcsQuery);
                    for (int j = 0; j < childs.size(); j++) {
                        Element child = childs.get(j);
                        if (iHttpParserUtilDeal != null) {
                            iHttpParserUtilDeal.deal(parent, child, childCcsQuery, (j + 1) == childs.size());
                        } else {
                            print(child, attributeKeys);
                        }
                    }
                }
            } else {
                if (iHttpParserUtilDeal != null) {
                    iHttpParserUtilDeal.deal(parent, parent, null, (i + 1) == parentElements.size());
                } else {
                    print(parent, attributeKeys);
                }
            }
        }
    }

    /**
     * 解析url内容
     *
     * @param url                 网址
     * @param timeout             超时
     * @param parentCcsQuery      父ccs条件
     * @param childCcsQuerys      子ccs条件
     * @param attributeKeys       属性
     * @param iHttpParserUtilDeal 自定义处理
     * @throws IOException 解析Document异常
     */
    public void parser(String url, int timeout, String parentCcsQuery, List<String> childCcsQuerys
            , List<String> attributeKeys, IHttpParserUtilDeal iHttpParserUtilDeal) throws IOException {
        if (parentCcsQuery == null || parentCcsQuery.length() == 0) {
            throw new NullPointerException("查询条件不能为空！");
        }
        // 通过css获取Elements
        Elements parentElements = parserGetElements(url, timeout, parentCcsQuery);
        // 元素解析
        parserElements(parentElements, childCcsQuerys, attributeKeys, iHttpParserUtilDeal);
        // 如果没有匹配到任何资源
        if (parentElements.size() == 0) {
            if (iHttpParserUtilDeal != null) {
                iHttpParserUtilDeal.noDataDeal();
            } else {
                logger.info("{}没有匹配到任何资源", url);
            }
        }
    }

    public void parser(String url, int timeout, String ccsQuery, List<String> attributeKeys) throws IOException {
        parser(url, timeout, ccsQuery, null, attributeKeys, null);
    }

    public void parser(String url, String ccsQuery, List<String> attributeKeys) throws IOException {
        parser(url, 5000, ccsQuery, attributeKeys);
    }

    /**
     * 下载文件
     *
     * @param url      网址
     * @param timeout  超时
     * @param savePath 保存路径全名，含文件名
     */
    public void download(String url, int timeout, String savePath) {
        int statusCode = 0;
        int retryCnt = 0;
        // 无限连接直到成功
        while (statusCode != 200) {
            if (retryCnt >= 1) {
                logger.warn("【重试次数】{}，【地址】{}", retryCnt, url);
            }
            try {
                Connection conn = Jsoup.connect(url);
                Connection.Response response = conn
                        .method(Connection.Method.GET)
                        .timeout(timeout)
                        .ignoreContentType(true)
                        .execute();
                statusCode = response.statusCode();
                // 保存文件，抛出异常要重试
                saveFile(response.bodyStream(), savePath);
            } catch (Exception e) {
                retryCnt++;
                statusCode = 0;
                logger.error(String.format("下载文件异常，状态=%s，重试次数=%s，异常信息=%s"
                        , statusCode, retryCnt, e.getMessage()));
                if (retryCnt > 30) {
                    throw new NullPointerException("下载文件异常次数超过30次！严重告警！地址：" + url);
                }
            }
        }
    }

    /**
     * 解析url获取对应的Document，无限自动重试
     *
     * @param url     网址
     * @param timeout 超时
     * @return 返回Document对象
     */
    private Document connUrlGetDoc(String url, int timeout) {
        int statusCode = 0;
        Document document = null;
        int retryCnt = 0;
        // 进行自动重试
        while (statusCode != 200) {
            if (retryCnt >= 1) {
                logger.warn("【重试次数】{}，【地址】{}", retryCnt, url);
            }
            try {
                Connection conn = Jsoup.connect(url);
                Connection.Response response = conn
                        .timeout(timeout)
                        .execute();
                statusCode = response.statusCode();
                document = response.parse();
            } catch (UncheckedIOException | IOException e) {// 只有连接异常才会自动重试
                retryCnt++;
                statusCode = 0;
                logger.error(String.format("解析url获取对应的Document异常，状态=%s，重试次数=%s，异常信息=%s"
                        , statusCode, retryCnt, e.getMessage()));
            }
        }
        return document;
    }

    /**
     * 打印内容和对应的属性
     *
     * @param element       元素
     * @param attributeKeys 属性
     */
    private void print(Element element, List<String> attributeKeys) {
        logger.info("【text】{}", element.text());
        if (attributeKeys != null && attributeKeys.size() > 0) {
            for (String attributeKey : attributeKeys) {
                logger.info("【{} 】{}", attributeKey, element.attr(attributeKey));
            }
        }
    }

    /**
     * 保存文件到本地
     *
     * @param bufferedInputStream 输入流
     * @param savePath            保存路径全名，含文件名
     * @throws IOException 写文件可能产生的IO异常
     */
    private void saveFile(BufferedInputStream bufferedInputStream, String savePath) throws IOException {
        int buffer_len = 2048;
        // 一次最多读取2k
        byte[] buffer = new byte[buffer_len];
        // 实际读取的长度
        int readLenghth;
        try (
                // 根据文件保存地址，创建文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(new File(savePath));
                // 创建的一个写出的缓冲流
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            // 文件逐步写入本地
            while ((readLenghth = bufferedInputStream.read(buffer, 0, buffer_len)) != -1) {// 先读出来，保存在buffer数组中
                bufferedOutputStream.write(buffer, 0, readLenghth);// 再从buffer中取出来保存到本地
            }
        } finally {
            // 关闭缓冲流
            bufferedInputStream.close();
        }
    }

    public interface IHttpParserUtilDeal {
        /**
         * 自定义处理
         *
         * @param parent        父级
         * @param child         自己
         * @param childCcsQuery 查询ccs条件
         * @param isEnd         是否结束
         * @throws IOException 未知的IO异常
         */
        void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException;

        /**
         * 没有匹配到任何资源
         */
        void noDataDeal();
    }
}
