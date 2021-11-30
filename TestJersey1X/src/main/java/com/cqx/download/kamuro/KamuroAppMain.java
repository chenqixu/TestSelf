package com.cqx.download.kamuro;

import com.cqx.common.utils.http.AbstractHttpParserUtilDeal;
import com.cqx.common.utils.http.EnumSizeUnit;
import com.cqx.common.utils.http.HttpParserUtil;
import com.cqx.common.utils.http.ICallBack;
import com.cqx.common.utils.list.IKVList;
import com.cqx.common.utils.list.KVList;
import com.cqx.common.utils.list.ListHelper;
import com.cqx.common.utils.thread.CallableTool;
import com.cqx.common.utils.thread.ICallableTool;
import com.cqx.download.yaoqi.FileUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

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
    private FileUtil oldNextPagefileUtil;
    private CallableTool<Long> callableTool;
    private AtomicLong monthLong = new AtomicLong(0L);

    public KamuroAppMain() {
        this.httpParserUtil = new HttpParserUtil();
        // 并发下载图书
        this.callableTool = new CallableTool<>(4);
    }

    public KamuroAppMain(String filePath) {
        this();
        init(filePath);
    }

    /**
     * 解析时间
     *
     * @param time 解析时间（YYYYMM），比如：202011
     * @return 返回2020/11，格式：YYYY/MM
     */
    public static String parserTime(String time) {
        String url_time = time.substring(0, 4) + "/" + time.substring(4, 6);
        logger.info("time={}，url_time={}", time, url_time);
        return url_time;
    }

    public static void main(String[] args) throws IOException {
        String FILE_PATH = "E:\\Photo\\Comic\\爬虫\\eromanga-kamuro.com\\images\\%s\\";
        // 移动硬盘
        FILE_PATH = "X:\\Reader\\web\\res\\comic\\kamuro\\%s\\";
        // URL
        String URL = "https://eromanga-kamuro.com/date/2021/11";

        KamuroAppMain appMain = new KamuroAppMain();
        // 获取所有月份
        IKVList<String, String> allMonth = appMain.getAllMonth(URL);
        for (IKVList.Entry<String, String> entry : allMonth.entrySet()) {
            logger.info("新的一个月份，key：{}，value：{}", entry.getKey(), entry.getValue());
            // 通过路径重新初始化
            appMain.init(String.format(FILE_PATH, entry.getKey()));
            // 判断是否有月份完成标志
            if (appMain.monthIsOk()) {
                logger.info("{} 这个月份已经下载完成。", entry.getKey());
                continue;
            }
            // 先扫描一下本地
            String oldNextPage = appMain.scanLocal();
            // 解析
            appMain.parser(oldNextPage != null ? oldNextPage : entry.getValue());
            // 写入月份完成标志
            appMain.saveMonthTag(entry.getKey());
        }
        // 停止并发线程池
        appMain.stop();
    }

    /**
     * 重新初始化
     *
     * @param filePath
     */
    public void init(String filePath) {
        if (filePath == null || filePath.length() == 0) throw new NullPointerException("filePath不能为空！");
        this.filePath = filePath;
        this.oldNextPagefileUtil = new FileUtil(filePath);
        // 如果本地目录不存在，创建一下
        this.oldNextPagefileUtil.mkdir(filePath);
        // 重置月份下载量
        this.monthLong = new AtomicLong(0L);
    }

    /**
     * 扫描本地路径，防止重复下载<br>
     * 从上一个记录的页面继续
     */
    public String scanLocal() {
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
        // 从硬盘记录获取最后处理的页面
        return oldNextPagefileUtil.getDiskNextPage();
    }

    /**
     * 获取所有月份
     *
     * @return
     * @throws IOException
     */
    public IKVList<String, String> getAllMonth(String url) throws IOException {
        final IKVList<String, String> months = new KVList<>();
        // 解析月份
        Elements elements = httpParserUtil.parserGetElements(url, timeout, ".widget");
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
                                logger.info("href：{}，isEnd：{}", href, isEnd);
                                months.put(year + mm, href);
                            }
                        }
                );
            }
        }
        return months;
    }

    /**
     * 保存月份下载完成的标志，并打印大小
     *
     * @param month 月份
     */
    public void saveMonthTag(String month) {
        oldNextPagefileUtil.saveMonthEnd();
        logger.info("月份{}下载完成，大小：{}"
                , month
                , HttpParserUtil.getFormatSize(monthLong.get(), EnumSizeUnit.MB));
    }

    /**
     * 判断月份是否下载完成
     *
     * @return
     */
    public boolean monthIsOk() {
        return oldNextPagefileUtil.isMonthDown();
    }

    /**
     * 下一页处理
     *
     * @param url
     * @throws IOException
     */
    public void nextPage(final String url) throws IOException {
        // 下一页处理
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
        // 等待当前页并发下载完成
        callableTool.await();
        Long tmpPageSize;
        Long pageSize = 0L;
        while ((tmpPageSize = callableTool.pollResult()) != null) {
            pageSize += tmpPageSize;
        }
        logger.info("当前页{}下载完成，大小：{}"
                , url
                , HttpParserUtil.getFormatSize(pageSize, EnumSizeUnit.KB));
        // 累计到月份下载量
        monthLong.addAndGet(pageSize);
        // 下一页
        nextPage(url);
    }

    /**
     * 停止并发线程池
     */
    public void stop() {
        callableTool.stop();
    }

    /**
     * 如果下载完成，需要生成ok.txt标志
     *
     * @param fileUtil
     * @param isEnd
     */
    private void saveEnd(FileUtil fileUtil, boolean isEnd) {
        saveEnd(fileUtil, isEnd, null);
    }

    /**
     * 如果下载完成，需要生成ok.txt标志，并打印图书大小
     *
     * @param fileUtil
     * @param isEnd
     * @param bookSize
     */
    private void saveEnd(FileUtil fileUtil, boolean isEnd, Long bookSize) {
        if (isEnd) {
            fileUtil.saveEnd();
            if (bookSize != null) {
                logger.info("图书{}下载完成，本书大小：{}"
                        , fileUtil.getTitle()
                        , HttpParserUtil.getFormatSize(bookSize, EnumSizeUnit.KB));
            }
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
                    // 把下一页写入硬盘文件nextpage.txt
                    oldNextPagefileUtil.saveNextPageToDisk(nextPageUrl);
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
            final String bookUrl = child.attr("href");// 书的地址
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

                // 图片解析类
                final ParserBookHttpParserUtilDeal parserBookHttpParserUtilDeal = new ParserBookHttpParserUtilDeal(fileUtil);
                // 并发下载
                callableTool.submitCallable(new ICallableTool<Long>(newBookName) {
                    @Override
                    public Long icall() throws Exception {
                        // 图片解析类设置心跳
                        parserBookHttpParserUtilDeal.setiCallBack(new ICallBack() {
                            @Override
                            public void callBack() {
                                // 心跳，需要在回调里执行
                                heartbeat();
                            }
                        });
                        // 解析书中的图片并下载
                        httpParserUtil.parser(bookUrl
                                , timeout
                                , ".alignnone"
                                , null
                                , null
                                , parserBookHttpParserUtilDeal
                        );
                        // 一本书共用一个处理对象，最后结果是累加的结果
                        return parserBookHttpParserUtilDeal.getFileSize();
                    }
                });
            }
        }
    }

    /**
     * 图书处理流程
     */
    public class ParserBookHttpParserUtilDeal extends AbstractHttpParserUtilDeal {
        private FileUtil fileUtil;
        private AtomicLong atomicLong = new AtomicLong(0L);

        public ParserBookHttpParserUtilDeal(FileUtil fileUtil) {
            this.fileUtil = fileUtil;
        }

        @Override
        public void deal(Element parent, Element child, String childCcsQuery, boolean isEnd) throws IOException {
            // 调用回调
            super.deal(parent, child, childCcsQuery, isEnd);
            // 获得图片地址
            String jpgUrl = child.attr("src");
            // 生成图片保存地址
            String jpgSaveName = fileUtil.getSaveImgName();
            // 判断是否可以下载，有的没有图片地址，不提供下载
            if (jpgUrl == null || jpgUrl.length() == 0) {
                logger.info("【图片】地址不存在，无法下载，跳过");
                // 如果都下载完成，但是缺了ok.txt标志，需要生成ok.txt标志
                saveEnd(fileUtil, isEnd);
                return;
            }
            // 判断是否下载
            if (fileUtil.isImgDown(jpgSaveName)) {
                logger.info("【图片】{}已经下载", jpgSaveName);
                // 如果都下载完成，但是缺了ok.txt标志，需要生成ok.txt标志
                saveEnd(fileUtil, isEnd);
                return;
            }
            // 下载图片（内部自动重连）
            long fileSize = httpParserUtil.download(jpgUrl, timeout, jpgSaveName);
            // 进行累计操作，因为一本书使用一个处理对象
            atomicLong.addAndGet(fileSize);
            logger.info("【图片大小】{}，【图片地址】{}，【图片保存地址】{}，已下载大小：{}"
                    , HttpParserUtil.getFormatSize(fileSize, EnumSizeUnit.BYTE)
                    , jpgUrl, jpgSaveName
                    , HttpParserUtil.getFormatSize(getFileSize(), EnumSizeUnit.BYTE));
            // 如果下载完成，需要生成ok.txt标志
            saveEnd(fileUtil, isEnd, getFileSize());
        }

        @Override
        public void noDataDeal() {
            // 调用回调
            super.noDataDeal();
            // 没有资源，需要生成ok.txt标志
            saveEnd(fileUtil, true);
        }

        public long getFileSize() {
            return atomicLong.get();
        }
    }
}
