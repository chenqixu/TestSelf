package com.cqx.common.utils.xml;

import com.cqx.common.utils.string.StringUtil;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * XMLParser
 *
 * @author chenqixu
 */
public class XMLParser {

    private static final Logger logger = LoggerFactory.getLogger(XMLParser.class);
    private String fileName;
    private String xmlData;
    private Document document;

    public void init() throws FileNotFoundException {
        if (StringUtil.isNotEmpty(fileName)) {
            document = loadXml(fileName);
        } else {
            document = loadXmlData(xmlData);
        }
    }

    private Document loadXml(String fileName) throws FileNotFoundException {
        InputStream is;
        is = new FileInputStream(new File(fileName));
        SAXReader reader = new SAXReader();
        try {
            return reader.read(is);
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private Document loadXmlData(String xmlData) {
        try {
            return DocumentHelper.parseText(xmlData);
        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public List<XMLParserElement> parseDocumentToElementList() {
        List<XMLParserElement> dogList = new ArrayList<>();
        if (document != null) {
            Element root = document.getRootElement();
            Iterator iterable = root.elementIterator();
            while (iterable.hasNext()) {
                Element element = (Element) iterable.next();
                String name = element.getName();
                if (name.contains("action")) {
                    Iterator actionIterable = element.elementIterator();
                    while (actionIterable.hasNext()) {
                        Element actionChildElement = (Element) actionIterable.next();
                        String actionChildName = actionChildElement.getName();
                        if (actionChildName.contains("dog")) {
                            dogList.add(new XMLParserElement(actionChildElement));
                        }
                    }
                }
            }
            for (XMLParserElement xmlParserElement : dogList) {
                logger.info("xml：{}", xmlParserElement.toXml());
            }
        }
        return dogList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }
}
