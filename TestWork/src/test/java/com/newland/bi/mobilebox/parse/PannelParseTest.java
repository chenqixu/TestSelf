package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.Pannel;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class PannelParseTest {

    private PannelParse pannelParse;

    @Before
    public void setUp() {
        pannelParse = new PannelParse();
    }

    @Test
    public void toJsonStr() {
        System.out.println(Pannel.newbuilder().setId("1").setImgurl("http://test").setTitle("test").build().toJson());
    }

    @Test
    public void jsonToBean() {
        System.out.println(Pannel.jsonToBean(Pannel.newbuilder().setId("1").setImgurl("http://test").setTitle("test").build().toJson()));
    }

    @Test
    public void parseDocument() throws FileNotFoundException {
        pannelParse.setFileName("src/main/resources/data/mobilebox1_pannel.xml");
        pannelParse.init();
        pannelParse.parseDocument();
    }

    @Test
    public void parseDocumentToString() throws FileNotFoundException {
        pannelParse.setFileName("src/main/resources/data/mobilebox1_pannel.xml");
        pannelParse.init();
        pannelParse.parseDocumentToString();
    }

    @Test
    public void parseDocumentByData() {
        String xmldata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<boxs>\n" +
                "<box id=\"1_1\" type=\"icon\" auto=\"false\" canfocus=\"true\" showround=\"false\" showtitle=\"false\">\n" +
                "<title><![CDATA[]]></title>\n" +
                "<titlecomment><![CDATA[]]></titlecomment>\n" +
                "<imgurl><![CDATA[http://images.center.bcs.ottcn.com:8080/images/ysten/images/2018/10/8/20181008182112_9.jpg]]></imgurl>\n" +
                "<videourl><![CDATA[]]></videourl>\n" +
                "<content>\n" +
                "<varstr><![CDATA[]]></varstr>\n" +
                "</content>\n" +
                "<openAppUrl><![CDATA[]]></openAppUrl>\n" +
                "<action><![CDATA[OpenUrl]]></action>\n" +
                "<actionurl><![CDATA[http://sns.center.bcs.ottcn.com/RecentlyWatched/index.html]]></actionurl>\n" +
                "<openAppExt><![CDATA[]]></openAppExt>\n" +
                "<Installurl><![CDATA[]]></Installurl>\n" +
                "<voicetitle><![CDATA[播放记录]]></voicetitle>\n" +
                "<leftTitleIcon><![CDATA[]]></leftTitleIcon>\n" +
                "</box>\n" +
                "</boxs>";
        pannelParse.parseDocument(xmldata);
    }
}