package com.cqx.download.pupil;

import com.cqx.common.utils.http.HttpParserUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 数学
 *
 * @author chenqixu
 */
public class Math {

    public static void main(String[] args) throws Exception {
        final int timeout = 15000;// 超时，单位毫秒
        HttpParserUtil httpParserUtil = new HttpParserUtil();
        String url = "file:///e:\\Self\\课本\\初中\\七年级\\下册\\人教版七年级（下）数学电子课本（2024年新版）.htm";
        final String saveImgPath = "e:\\Self\\课本\\初中\\七年级\\下册\\七年级（下）数学\\%s.jpg";
        // div
        Elements elements = httpParserUtil.parserGetElements(url, timeout, ".rich_media_content");
        // 选择div下的所有img
        Elements imgs = elements.select("img");
        int name = 1000;
        for (Element element : imgs) {
            // jpeg
            String jpeg = element.attr("data-src");
            // 下载图片（内部自动重连）
            String _savePath = String.format(saveImgPath, name++);
            long fileSize = httpParserUtil.download(jpeg, timeout, _savePath);
            System.out.println(String.format("保存图片%s到%s，图片大小 %s B", jpeg, _savePath, fileSize));
        }
    }
}
