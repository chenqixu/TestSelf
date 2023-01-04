package com.cqx.download.pupil;

import com.cqx.common.utils.http.HttpParserUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 语文
 *
 * @author chenqixu
 */
public class Chinese {

    public static void main(String[] args) throws Exception {
        final int timeout = 15000;// 超时，单位毫秒
        HttpParserUtil httpParserUtil = new HttpParserUtil();
        String url = "file:///e:\\Self\\课本\\小学\\语文五年级上.html";
        final String saveImgPath = "e:\\Self\\课本\\小学\\语文五年级上\\%s.jpg";
        Elements elements = httpParserUtil.parserGetElements(url, timeout, ".rich_pages");
        int name = 1000;
        for (Element element : elements) {
            // jpeg
            String jpeg = element.attr("data-src");
            // 下载图片（内部自动重连）
            String _savePath = String.format(saveImgPath, name++);
            long fileSize = httpParserUtil.download(jpeg, timeout, _savePath);
            System.out.println(String.format("保存图片%s到%s，图片大小 %s B", jpeg, _savePath, fileSize));
        }
    }
}
