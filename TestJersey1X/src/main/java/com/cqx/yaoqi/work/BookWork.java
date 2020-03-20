package com.cqx.yaoqi.work;

import com.cqx.bean.RestParam;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.yaoqi.FileUtil;
import com.cqx.yaoqi.ImageAndNext;
import com.cqx.yaoqi.TitleAndUrl;
import com.cqx.yaoqi.YaoqiParser;
import com.cqx.yaoqi.http.HttpsUtil;

/**
 * BookWork
 *
 * @author chenqixu
 */
public class BookWork extends BaseWork {
    private static final MyLogger logger = MyLoggerFactory.getLogger(BookWork.class);
    private TitleAndUrl titleAndUrl;
    private HttpsUtil httpsUtil = new HttpsUtil();

    public BookWork(TitleAndUrl titleAndUrl) {
        this.titleAndUrl = titleAndUrl;
    }

    public String getTitle() {
        return titleAndUrl.getTitle();
    }

    public void run() {
        logger.info("下载新书：" + titleAndUrl);
        FileUtil fileUtil = new FileUtil();
        //设置标题
        fileUtil.setTitle(titleAndUrl.getTitle());
        //解析每本书，下载图片
        Object bookObj = httpsUtil.httpRequest(new RestParam(
                titleAndUrl.getTitle_url(),
                "GET", null, "https", "", "",
                "", "",
                "", ""), "string", null);
        ImageAndNext imageAndNext = YaoqiParser.getImageAndNext(bookObj.toString());
        httpsUtil.httpRequest(new RestParam(
                imageAndNext.getImage_url(),
                "GET", null, imageAndNext.getImage_urlIShttpOrhttps(),
                "", "", "", "",
                "", ""), "file", fileUtil);
        while (imageAndNext.getNext_image_url().length() > 0) {
            //下一页
            bookObj = httpsUtil.httpRequest(new RestParam(
                    imageAndNext.getNext_image_url(),
                    "GET", null, imageAndNext.getNext_image_urlIShttpOrhttps(),
                    "", "", "", "",
                    "", ""), "string", null);
            imageAndNext = YaoqiParser.getImageAndNext(bookObj.toString());
            httpsUtil.httpRequest(new RestParam(
                    imageAndNext.getImage_url(),
                    "GET", null, imageAndNext.getImage_urlIShttpOrhttps(),
                    "", "", "", "",
                    "", ""), "file", fileUtil);
        }
        logger.info(titleAndUrl.getTitle() + "，下载完成。");
        //完成
        complete();
    }

}
