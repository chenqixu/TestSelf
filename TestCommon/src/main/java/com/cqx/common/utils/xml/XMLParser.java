package com.cqx.common.utils.xml;

import com.cqx.common.utils.string.StringUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XMLParser
 *
 * @author chenqixu
 */
public class XMLParser {

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
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

    private XMLParserElement parseDocument() {
        if (document != null) {
            return new XMLParserElement(document.getRootElement());
        }
        return null;
    }

    public List<XMLParserElement> parseRootChildElement(String elementName) {
        List<XMLParserElement> resultList = new ArrayList<>();
        //从root解析
        XMLParserElement xmlParserElement = parseDocument();
        if (xmlParserElement != null) {
            List<XMLParserElement> childElementList = xmlParserElement.getChildElementList();
            for (XMLParserElement child : childElementList) {
                if (child.getElementName().equals(elementName)) {
                    resultList.add(child);
                }
            }
        }
        return resultList;
    }

    public List<XMLParserElement> getChildElement(List<XMLParserElement> elementList, String elementName) {
        List<XMLParserElement> resultList = new ArrayList<>();
        for (XMLParserElement element : elementList) {
            List<XMLParserElement> childList = element.getChildElementList();
            for (XMLParserElement child : childList) {
                if (child.getElementName().equals(elementName)) {
                    resultList.add(child);
                }
            }
        }
        return resultList;
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
