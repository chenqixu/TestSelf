package com.cqx.yaoqi.work;

import com.cqx.bean.RestParam;
import com.cqx.common.utils.log.LogUtil;
import com.cqx.yaoqi.TitleAndUrl;
import com.cqx.yaoqi.YaoqiParser;
import com.cqx.yaoqi.http.HttpsUtil;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * PageWork
 *
 * @author chenqixu
 */
public class PageWork extends BaseWork {
    private static final LogUtil logger = LogUtil.getLogger(PageWork.class);
    private HttpsUtil httpsUtil = new HttpsUtil();
    private BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue;
    private int max_down_page_count = 1;//默认下载一页

    public PageWork(int max_down_page_count, BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue) {
        this.max_down_page_count = max_down_page_count;
        this.titleAndUrlBlockingQueue = titleAndUrlBlockingQueue;
    }

    public void run() {
        int down_page_count = 0;
        //读取首页，识别到每本书
        Object pageObj = httpsUtil.httpRequest(new RestParam(
                "https://m.yaoqi99.com/mh/",
                "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        //吐到待下载队列
        List<TitleAndUrl> titleAndUrls = YaoqiParser.getATitle(pageObj.toString(), titleAndUrlBlockingQueue);
        down_page_count++;
        //小于最大下载队列才能进行下载
        while (titleAndUrls.size() > 1 && down_page_count < max_down_page_count) {
            String nextPageUrl = titleAndUrls.get(0).getNext_page_url();
            //如果有下一页
            if (nextPageUrl.length() > 0) {
                pageObj = httpsUtil.httpRequest(new RestParam(
                        nextPageUrl,
                        "GET", null, "https", "", "",
                        "", "",
                        "", ""), "string", null);
                //吐到待下载队列
                titleAndUrls = YaoqiParser.getATitle(pageObj.toString(), titleAndUrlBlockingQueue);
                down_page_count++;
            } else {
                break;
            }
        }
        //完成
        complete();
    }

}
