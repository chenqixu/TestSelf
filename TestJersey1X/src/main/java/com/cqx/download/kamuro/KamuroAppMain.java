package com.cqx.download.kamuro;

import com.cqx.common.utils.http.AbstractHttpParserUtilDeal;
import com.cqx.common.utils.http.HttpParserUtil;
import com.cqx.common.utils.list.ListHelper;
import com.cqx.download.http.FileStreamDeal;
import com.cqx.download.yaoqi.FileUtil;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 启动入口
 *
 * @author chenqixu
 */
public class KamuroAppMain {
    private static final Logger logger = LoggerFactory.getLogger(KamuroAppMain.class);
    private final int timeout = 15000;// 超时，单位毫秒
    private String filePath;
    private Map<String, String> localFileMap;
    private boolean is_down = true;// 是否下载
    private boolean is_next = true;// 是否跳转到下一页
    private HttpParserUtil httpParserUtil;

    public KamuroAppMain(String filePath) {
        this.httpParserUtil = new HttpParserUtil();
        this.filePath = filePath;
    }

    public static void main(String[] args) throws IOException {
        String FILE_PATH = "E:\\Photo\\Comic\\爬虫\\eromanga-kamuro.com\\images\\202108\\";
        // 移动硬盘
        FILE_PATH = "G:\\Reader\\Comic\\爬虫\\eromanga-kamuro.com\\images\\202108\\";
        FileStreamDeal.setFile_path(FILE_PATH);
        KamuroAppMain appMain = new KamuroAppMain(FILE_PATH);
        // 先扫描一下本地
        appMain.scanLocal();
        // 解析
        appMain.parser("https://eromanga-kamuro.com/date/2021/08");
    }

    /**
     * 扫描本地路径，防止重复下载
     */
    public void scanLocal() {
        localFileMap = new HashMap<>();
        // 扫描本地路径，防止重复下载
        File file = new File(filePath);
        for (String str : Objects.requireNonNull(file.list())) {
            // 确认下是不是目录，是的话，加入map
            if (new File(filePath + str).isDirectory()) {
                localFileMap.put(str, "ok");
                logger.info("扫描到的本地目录：{}", str);
            }
        }
    }

    /**
     * 下一页处理
     *
     * @param url
     * @throws IOException
     */
    public void nextPage(final String url) throws IOException {
        httpParserUtil.parser(url
                , timeout
                , ".pagination-next"
                , ListHelper.getInstance(String.class).add("a").get()
                , null
                , new NextPageHttpParserUtilDeal(url)
        );
    }

    /**
     * 解析当前页，解析完成后跳转下一页
     *
     * @param url
     * @throws IOException
     */
    public void parser(String url) throws IOException {
        // 解析当页
        httpParserUtil.parser(url
                , timeout
                , ".ect-entry-card"
                , ListHelper.getInstance(String.class).add("a").get()
                , null
                , new ParserPageHttpParserUtilDeal()
        );
        // 下一页
        nextPage(url);
    }

    /**
     * 如果下载完成，需要生成ok.txt标志
     *
     * @param fileUtil
     * @param isEnd
     */
    private void saveEnd(FileUtil fileUtil, boolean isEnd) {
        if (isEnd) {
            fileUtil.saveEnd();
        }
    }

    /**
     * 下一页处理流程
     */
    public class NextPageHttpParserUtilDeal extends AbstractHttpParserUtilDeal {
        private String url;

        public NextPageHttpParserUtilDeal(String url) {
            this.url = url;
        }

        @Override
        public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException {
            String nextPageUrl = child.attr("href");
            if (nextPageUrl != null && nextPageUrl.trim().length() > 0) {
                if (is_next) {
                    logger.info("【开始处理下一页】{}", nextPageUrl);
                    parser(nextPageUrl);
                } else {
                    logger.info("【下一页】{}", nextPageUrl);
                }
            } else {
                logger.info("{}没有下一页。", url);
            }
        }
    }

    /**
     * 本页处理流程
     */
    public class ParserPageHttpParserUtilDeal extends AbstractHttpParserUtilDeal {

        @Override
        public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException {
            final FileUtil fileUtil = new FileUtil(filePath);
            // 获取本页的每一本书
            String title = child.text();// 标题
            String bookUrl = child.attr("href");// 书的地址
            String newBookName = HttpParserUtil.getLastResource(bookUrl, new int[]{2, 1});
            logger.info("【书的地址】{}，【新的书名】 {}", bookUrl, newBookName);
            // 设置书名
            fileUtil.setTitle(newBookName);

            // 判断是否有ok.txt标志
            if (localFileMap.get(newBookName) != null && fileUtil.isBookDown()) {
                logger.info("{}这本书已经下载了。", newBookName);
                return;
            }
            if (is_down) {
                // 创建书的目录，并写入标题到readme.txt文件
                fileUtil.mkdir();
                fileUtil.saveTitle(title);

                // 解析书中的图片并下载
                httpParserUtil.parser(bookUrl
                        , timeout
                        , ".alignnone"
                        , null
                        , null
                        , new ParserBookHttpParserUtilDeal(fileUtil)
                );
            }
        }
    }

    /**
     * 图书处理流程
     */
    public class ParserBookHttpParserUtilDeal extends AbstractHttpParserUtilDeal {
        private FileUtil fileUtil;

        public ParserBookHttpParserUtilDeal(FileUtil fileUtil) {
            this.fileUtil = fileUtil;
        }

        @Override
        public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException {
            // 获得图片地址
            String jpgUrl = child.attr("src");
            // 生成图片保存地址
            String jpgSaveName = fileUtil.getSaveImgName();
            logger.info("【图片地址】{}，【图片保存地址】{}", jpgUrl, jpgSaveName);
            // 判断是否下载
            if (fileUtil.isImgDown(jpgSaveName)) {
                logger.info("【图片】{}已经下载", jpgSaveName);
                // 如果都下载完成，但是缺了ok.txt标志，需要生成ok.txt标志
                saveEnd(fileUtil, isEnd);
                return;
            }
            // 下载图片（内部自动重连）
            httpParserUtil.download(jpgUrl, timeout, jpgSaveName);
            // 如果下载完成，需要生成ok.txt标志
            saveEnd(fileUtil, isEnd);
        }

        @Override
        public void noDataDeal() {
            // 没有资源，需要生成ok.txt标志
            saveEnd(fileUtil, true);
        }
    }
}
