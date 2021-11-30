package com.cqx.common.utils.http;

import com.cqx.common.utils.system.SleepUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.NoRouteToHostException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * http解析工具
 *
 * @author chenqixu
 */
public class HttpParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpParserUtil.class);
    private final String LOCAL_HEAD = "file:///";
    private AtomicLong downloadSize = new AtomicLong(0);

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
     * 获取格式化后的大小
     *
     * @param size
     * @param enumSizeUnit
     * @return
     */
    public static String getFormatSize(long size, EnumSizeUnit enumSizeUnit) {
        return String.format("%s (%s)", size / enumSizeUnit.getDivisor(), enumSizeUnit.getName());
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
        if (url.startsWith(LOCAL_HEAD)) {// 本地文件
            File input = new File(url.replace(LOCAL_HEAD, ""));
            doc = Jsoup.parse(input, "UTF-8");
        } else {// 网络地址
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
     * @return 文件大小（单位byte）
     */
    public long download(String url, int timeout, String savePath) {
        int statusCode = 0;
        int retryCnt = 0;
        long fileSize = 0L;
        // 无限连接直到成功
        while (statusCode != 200) {
            if (retryCnt >= 1) {
                logger.warn("【重试次数】{}，【地址】{}", retryCnt, url);
            }
            try {
                Connection.Response response = execute(url, timeout);
                statusCode = response.statusCode();
                // 保存文件，抛出异常要重试
                fileSize = saveFile(response.bodyStream(), savePath);
                downloadSize.addAndGet(fileSize);
            } catch (Exception e) {
                retryCnt++;
                statusCode = 0;
                logger.error(String.format("下载文件异常，状态=%s，重试次数=%s，异常信息=%s"
                        , statusCode, retryCnt, e.getMessage()));
                // 网络物理异常，可以休眠的久一点
                if (UnknownHostException.class.isAssignableFrom(e.getClass())
                        || NoRouteToHostException.class.isAssignableFrom(e.getClass())) {
                    SleepUtil.sleepSecond(5);
                }
            }
        }
        return fileSize;
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
                Connection.Response response = execute(url, timeout);
                statusCode = response.statusCode();
                // 可能正常返回404，不是返回200，造一个空文档对象
                if (statusCode != 200) {
                    logger.warn("请求{}返回了非200状态：{}", url, statusCode);
                    return new Document("Null");
                } else {
                    document = response.parse();
                }
            } catch (UncheckedIOException | IOException e) {// 只有连接异常才会自动重试
                retryCnt++;
                statusCode = 0;
                logger.error(String.format("解析url获取对应的Document异常，状态=%s，重试次数=%s，异常信息=%s"
                        , statusCode, retryCnt, e.getMessage()));
                // 太多重定向，直接返回一个空文档对象
                if (e.getMessage().contains("Too many redirects occurred trying to load URL")) {
                    logger.warn("太多重定向，直接返回一个空文档对象！");
                    return new Document("Null");
                }
            }
        }
        return document;
    }

    /**
     * 解析url
     *
     * @param url
     * @param timeout
     * @return
     * @throws IOException
     */
    private Connection.Response execute(String url, int timeout) throws IOException {
        Connection conn = Jsoup.connect(url);
        addHeader(conn);
        return conn
                .method(Connection.Method.GET)// 使用GET请求
                .timeout(timeout)// 设置超时
                .ignoreContentType(true)// 忽略类型错误
                .ignoreHttpErrors(true)// 忽略Http错误
                .execute();
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
     * @return 文件大小（单位byte）
     * @throws IOException 写文件可能产生的IO异常
     */
    private long saveFile(BufferedInputStream bufferedInputStream, String savePath) throws IOException {
        int buffer_len = 2048;
        // 一次最多读取2k
        byte[] buffer = new byte[buffer_len];
        // 实际读取的长度
        int readLength;
        long realReadLength = 0L;
        try (
                // 根据文件保存地址，创建文件输出流
                FileOutputStream fileOutputStream = new FileOutputStream(new File(savePath));
                // 创建的一个写出的缓冲流
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            // 文件逐步写入本地
            while ((readLength = bufferedInputStream.read(buffer, 0, buffer_len)) != -1) {// 先读出来，保存在buffer数组中
                bufferedOutputStream.write(buffer, 0, readLength);// 再从buffer中取出来保存到本地
                realReadLength += readLength;
            }
        } finally {
            // 关闭缓冲流
            bufferedInputStream.close();
        }
        return realReadLength;
    }

    /**
     * 添加模拟浏览器的header
     *
     * @param conn
     */
    private void addHeader(Connection conn) {
        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.header("Accept-Language", "zh-cn,zh;q=0.5");
        conn.header("Accept-Encoding", "gzip, deflate");
    }

    /**
     * 获取下载总量（byte）
     *
     * @return
     */
    public long getDownloadSize() {
        return downloadSize.get();
    }

    /**
     * 重置下载总量（byte）
     */
    public void resetDownloadSize() {
        downloadSize.set(0L);
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
