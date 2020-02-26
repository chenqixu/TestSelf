package com.cqx.yaoqi.work;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.yaoqi.AppMain;
import com.cqx.yaoqi.TitleAndUrl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadManagerment
 *
 * @author chenqixu
 */
public class ThreadManagerment {
    //待下载队列
    private BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue = new LinkedBlockingQueue<>();
    //是否下载
    private boolean isDownload = true;
    private int downloadNum = 10;//下载并发
    private int max_down_page_count = 1;//默认下载一页

    public ThreadManagerment() {
    }

    public ThreadManagerment(boolean isDownload, int downloadNum, int max_down_page_count) {
        this.isDownload = isDownload;
        this.downloadNum = downloadNum;
        this.max_down_page_count = max_down_page_count;
    }

    public ThreadManagerment(boolean isDownload) {
        this(isDownload, 10, 1);
    }

    public ThreadManagerment(int downloadNum) {
        this(true, downloadNum, 1);
    }

    public ThreadManagerment(boolean isDownload, int max_down_page_count) {
        this(isDownload, 10, max_down_page_count);
    }

    public ThreadManagerment(int downloadNum, int max_down_page_count) {
        this(true, downloadNum, max_down_page_count);
    }

    public Map<String, String> scanLocalFile() {
        Map<String, String> map = new HashMap<>();
        //扫描本地路径，防止重复下载
        File file = new File(AppMain.FILE_PATH);
        for (String str : file.list()) {
            //确认下是不是目录，是的话，加入map
            if (new File(AppMain.FILE_PATH + str).isDirectory()) {
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
                    System.out.println(titleAndUrl.getTitle() + ",[isDownload]" + isDownload +
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
