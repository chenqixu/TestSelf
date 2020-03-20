package com.cqx.yaoqi;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * YaoqiParser
 *
 * @author chenqixu
 */
public class YaoqiParser {
    private static final MyLogger logger = MyLoggerFactory.getLogger(YaoqiParser.class);
    private static final String F1 = "<li><a  title=\"";
    private static final String F2 = "href=\"";
    private static final String F3 = "\">";
    private static final String F4 = "下一页";

    private static final String IMAGE1 = "<img";
    private static final String IMAGE2 = "href=";
    private static final String IMAGE3 = "正文";
    private static final String IMAGE4 = "src=\"";

    public static List<TitleAndUrl> getATitle(String str) {
        //获取下一页地址
        String nextPageUrl = getNextPage(str);
        logger.info("下一页地址：" + nextPageUrl);
        List<TitleAndUrl> titleAndUrlList = new ArrayList<>();
        //找到<li><a  title=
        //获取title
        //获取href
        //查找下一个
        String _str = str;
        int index_f1 = -1;
        while ((index_f1 = _str.indexOf(F1)) >= 0) {
            String _new_str = _str.substring(index_f1 + F1.length());
            int index_f1_end = _new_str.indexOf("\"");
            String title = _new_str.substring(0, index_f1_end);
            int index_href = _new_str.indexOf(F2);
            int index_href_end = _new_str.indexOf(F3);
            String href = _new_str.substring(index_href + F2.length(), index_href_end);
            TitleAndUrl titleAndUrl = new TitleAndUrl();
            titleAndUrl.setTitle(title);
            titleAndUrl.setTitle_url(href);
            titleAndUrl.setNext_page_url(nextPageUrl);
            titleAndUrlList.add(titleAndUrl);
            logger.debug(titleAndUrl.toString());
            _str = _new_str;
        }
        return titleAndUrlList;
    }

    public static List<TitleAndUrl> getATitle(String str, BlockingQueue<TitleAndUrl> titleAndUrlBlockingQueue) {
        List<TitleAndUrl> titleAndUrls = getATitle(str);
        for (TitleAndUrl titleAndUrl : titleAndUrls) {
            try {
                titleAndUrlBlockingQueue.put(titleAndUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("吐到待下载队列大小：" + titleAndUrls.size());
        return titleAndUrls;
    }

    public static String getNextPage(String str) {
        String tmp_str = str;
        //找下一页
        int next_index = tmp_str.indexOf(F4);
        tmp_str = tmp_str.substring(0, next_index);
        //倒序查找href=
        char[] next_strs = tmp_str.toCharArray();
        //缓存
        char[] caches = IMAGE2.toCharArray();
        //缓存位置
        int caches_index = caches.length - 1;
        //找到的起始位置
        int next_href_index = -1;
        for (int i = (next_strs.length - 1); i > -1; i--) {
            if (next_strs[i] == caches[caches_index]) {
                if (caches_index > 0) caches_index--;
                else {
                    //完全匹配
                    next_href_index = i;
                    String new_next_str = tmp_str.substring(next_href_index);
                    //替换
                    new_next_str = new_next_str.replaceFirst(IMAGE2, "");
                    new_next_str = new_next_str.replaceAll(">", "");
                    new_next_str = new_next_str.replaceAll("\"", "");
                    new_next_str = new_next_str.replaceAll("'", "");
                    return new_next_str;
                }
            } else {
                //缓存从头开始
                caches_index = caches.length - 1;
            }
        }
        return "";
    }

    public static ImageAndNext getImageAndNext(String str) {
        ImageAndNext imageAndNext = new ImageAndNext();
        //先找正文
        int index_image3 = str.indexOf(IMAGE3);
        if (index_image3 > 0) {
            String image3_str = str.substring(index_image3 + IMAGE3.length());
            //然后找第一个<img
            int index_image1 = image3_str.indexOf(IMAGE1);
            if (index_image1 > 0) {
                String imaget1_str = image3_str.substring(index_image1 + IMAGE1.length());
                //然后找src=
                int index_image4 = imaget1_str.indexOf(IMAGE4);
                if (index_image4 > 0) {
                    //获取图片地址
                    String image_str = imaget1_str.substring(index_image4 + IMAGE4.length());
                    int index_image1_end = image_str.indexOf("\"");
                    String image_url = image_str.substring(0, index_image1_end);
                    imageAndNext.setImage_url(image_url);
                    //获取下一个图片的网址
                    String next_image_str = str.substring(index_image3, index_image3 + index_image1);
                    //倒着逐个读取
                    char[] next_image_strs = next_image_str.toCharArray();
                    //缓存
                    char[] caches = IMAGE2.toCharArray();
                    //缓存位置
                    int caches_index = caches.length - 1;
                    //找到的起始位置
                    int next_image_index = -1;
                    for (int i = (next_image_strs.length - 1); i > -1; i--) {
                        if (next_image_strs[i] == caches[caches_index]) {
                            if (caches_index > 0) caches_index--;
                            else {
                                //完全匹配
                                next_image_index = i;
                                String new_next_image_str = next_image_str.substring(next_image_index);
                                //替换
                                new_next_image_str = new_next_image_str.replaceFirst(IMAGE2, "");
                                new_next_image_str = new_next_image_str.replaceAll(">", "");
                                new_next_image_str = new_next_image_str.replaceAll("\"", "");
                                new_next_image_str = new_next_image_str.replaceAll("'", "");
                                imageAndNext.setNext_image_url(new_next_image_str);
                                break;
                            }
                        } else {
                            //缓存从头开始
                            caches_index = caches.length - 1;
                        }
                    }
                }
            }
        }
        logger.debug(imageAndNext.toString());
        return imageAndNext;
    }
}
