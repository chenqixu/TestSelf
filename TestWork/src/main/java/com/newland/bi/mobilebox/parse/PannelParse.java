package com.newland.bi.mobilebox.parse;

import com.cqx.common.utils.string.StringUtil;
import com.newland.bi.mobilebox.bean.Pannel;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * pannel Xml解析
 *
 * @author chenqixu
 */
public class PannelParse {

    private static Logger logger = LoggerFactory.getLogger(PannelParse.class);
    private String fileName;
    private Document document;

    public static void main(String[] args) throws FileNotFoundException {
        PannelParse pannelParse = new PannelParse();
        pannelParse.setFileName("src/main/resources/data/mobilebox1_pannel.xml");
        pannelParse.init();
        pannelParse.parseDocumentToString();
    }

    public void init() throws FileNotFoundException {
        if (StringUtil.isNotEmpty(fileName)) {
            document = loadXml(fileName);
        }
    }

    private Document loadXml(String fileName) throws FileNotFoundException {
        InputStream is = null;
        is = new FileInputStream(new File(fileName));
        SAXReader reader = new SAXReader();
        try {
            return reader.read(is);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document loadXmlData(String xmldata) {
        try {
            return DocumentHelper.parseText(xmldata);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
//        SAXReader reader = new SAXReader();
//        try {
//            return reader.read(new ByteArrayInputStream(xmldata.getBytes()));
//        } catch (DocumentException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public Map<String, Pannel> parseDocument(String xmldata) {
        document = loadXmlData(xmldata);
        return parseDocument();
    }

    public Map<String, Pannel> parseDocument() {
        Map<String, Pannel> resultlist = null;
        if (document != null) {
            resultlist = new HashMap<>();
            Element root = document.getRootElement();
            Iterator iterable = root.elementIterator();
            while (iterable.hasNext()) {
                Element element = (Element) iterable.next();
                String id = element.attribute("id").getValue();
                String imgurl = element.element("imgurl").getText();
                String tile = "";
                if (id.length() < 5) {
                    tile = element.element("voicetitle").getText();
                } else {
                    tile = element.element("title").getText();
                }
                Pannel pannel = Pannel.newbuilder().setId(id).setImgurl(imgurl).setTitle(tile).build();
                resultlist.put(pannel.getId(), pannel);
                logger.info("pannelJson：{}，pannel：{}", pannel.toJson(), Pannel.jsonToBean(pannel.toJson()));
            }
        }
        return resultlist;
    }

    public String parseDocumentToString(String xmldata) {
        document = loadXmlData(xmldata);
        return parseDocumentToString();
    }

    public String parseDocumentToString() {
        if (document != null) {
            List<Pannel> pannelList = new ArrayList<>();
            Element root = document.getRootElement();
            Iterator iterable = root.elementIterator();
            while (iterable.hasNext()) {
                Element element = (Element) iterable.next();
                String id = element.attribute("id").getValue();
                String type = element.attribute("type") == null ? "" : element.attribute("type").getValue();
                logger.info("type：{}", type);
                Pannel pannel = null;
                if (type.equals(PannelType.video.getCode())) {
                    logger.info("item：{}", element.element("item"));
                    Element item = element.element("item");
                    if (item != null) {
                        pannel = getImgUrlTitle(item, id);
                    }
                } else {
                    pannel = getImgUrlTitle(element, id);
                }
                pannelList.add(pannel);
                logger.info("pannelJson：{}，pannel：{}", pannel.toJson(), Pannel.jsonToBean(pannel.toJson()));
            }
            return Pannel.listToJson(pannelList);
        }
        return null;
    }

    public Pannel getImgUrlTitle(Element element, String id) {
        String imgurl = getElementText(element, "imgurl");
        String tile;
        if (id.length() < 5) {
            tile = getElementText(element, "voicetitle");
        } else {
            tile = getElementText(element, "title");
        }
        return Pannel.newbuilder().setId(id).setImgurl(imgurl).setTitle(tile).build();
    }

    public String getElementText(Element element, String name) {
        return element.element(name) == null ? "" : element.element(name).getText();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public enum PannelType {
        icon("icon"),
        video("video"),
        ;

        private final String code;

        PannelType(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}
