package com.cqx.download.yaoqi.work;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.download.yaoqi.TitleAndUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadManagerment
 *
 * @author chenqixu
 */
public class ThreadManagerment {
    private static final Logger logger = LoggerFactory.getLogger(ThreadManagerment.class);
    // 待下载队列
    private BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue = new LinkedBlockingQueue<>();
    // 是否下载
    private boolean isDownload = true;
    // 下载并发
    private int downloadNum = 10;
    // 默认下载一页
    private int max_down_page_count = 1;
    // 扫描本地路径，防止重复下载
    private String scan_local_path;

    public ThreadManagerment() {
    }

    public ThreadManagerment(String scan_local_path, boolean isDownload, int downloadNum, int max_down_page_count) {
        this.scan_local_path = scan_local_path;
        this.isDownload = isDownload;
        this.downloadNum = downloadNum;
        this.max_down_page_count = max_down_page_count;
    }

    public ThreadManagerment(String scan_local_path, boolean isDownload) {
        this(scan_local_path, isDownload, 10, 1);
    }

    public ThreadManagerment(String scan_local_path, int downloadNum) {
        this(scan_local_path, true, downloadNum, 1);
    }

    public ThreadManagerment(String scan_local_path, boolean isDownload, int max_down_page_count) {
        this(scan_local_path, isDownload, 10, max_down_page_count);
    }

    public ThreadManagerment(String scan_local_path, int downloadNum, int max_down_page_count) {
        this(scan_local_path, true, downloadNum, max_down_page_count);
    }

    public Map<String, String> scanLocalFile(String scan_local_path) {
        this.scan_local_path = scan_local_path;
        return scanLocalFile();
    }

    public Map<String, String> scanLocalFile() {
        Map<String, String> map = new HashMap<>();
        //扫描本地路径，防止重复下载
        File file = new File(scan_local_path);
        for (String str : Objects.requireNonNull(file.list())) {
            //确认下是不是目录，是的话，加入map
            if (new File(scan_local_path + str).isDirectory()) {
                map.put(str, "ok");
            }
        }
        return map;
    }

    public void exec() {
        boolean flag = true;
        //扫描本地已下载文件，防止重复下载
        Map<String, String> localMap = scanLocalFile();
        //下载并发队列
        Map<String, BookWork> bookWorkMap = new ConcurrentHashMap<>();
        //启动首页线程，把数据吐到待下载队列中
        BaseWork pageWork = new PageWork(max_down_page_count, titleAndUrlBlockingQueue);
        pageWork.start();
        while (flag) {
            //启动图书下载线程，消费队列，最多维持在${downloadNum}个并发
            if (bookWorkMap.size() < downloadNum) {
                TitleAndUrl titleAndUrl = titleAndUrlBlockingQueue.poll();
                if (titleAndUrl != null) {
                    boolean isLocalDown = (localMap.get(titleAndUrl.getTitle()) == null);
                    logger.info(titleAndUrl.getTitle() + ",[isDownload]" + isDownload +
                            ",[是否能下载]" + isLocalDown);
                    //需要确认没有下载过，防止重复下载
                    if (isDownload && isLocalDown) {
                        BookWork bookWork = new BookWork(titleAndUrl);
                        bookWorkMap.put(titleAndUrl.getTitle(), bookWork);
                        bookWork.start();
                    }
                }
            }
            //判断图书下载线程是否完成，如果完成就从并发中移除，并加入本地缓存防止重复下载
            Iterator<BookWork> iterator = bookWorkMap.values().iterator();
            while (iterator.hasNext()) {
                BookWork bookWork = iterator.next();
                if (bookWork.isComplete()) {
                    //加入本地缓存防止重复下载
                    localMap.put(bookWork.getTitle(), "ok");
                    //从并发中移除
                    iterator.remove();
                }
            }
            //判断首页线程是否执行完成 && 待下载队列是否为0 && 并发队列是否为0
            if (pageWork.isComplete() &&
                    titleAndUrlBlockingQueue.size() == 0 &&
                    bookWorkMap.size() == 0) flag = false;
            //休眠
            SleepUtil.sleepMilliSecond(50);
        }
    }

}
