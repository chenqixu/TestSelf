package com.cqx.yaoqi.work;

import com.cqx.bean.RestParam;
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
    private HttpsUtil httpsUtil = new HttpsUtil();
    private BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue;

    public PageWork(BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue) {
        this.titleAndUrlBlockingQueue = titleAndUrlBlockingQueue;
    }

    public void run() {
        //读取首页，识别到每本书
        Object pageObj = httpsUtil.httpRequest(new RestParam(
                "https://m.yaoqi99.com/mh/",
                "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        //吐到待下载队列
        List<TitleAndUrl> titleAndUrls = YaoqiParser.getATitle(pageObj.toString(), titleAndUrlBlockingQueue);
        while (titleAndUrls.size() > 1) {
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
            } else {
                break;
            }
        }
        //完成
        complete();
    }

}
