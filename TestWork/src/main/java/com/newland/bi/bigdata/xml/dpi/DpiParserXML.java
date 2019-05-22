package com.newland.bi.bigdata.xml.dpi;

import com.newland.bi.bigdata.xml.IXMLChildren;
import com.newland.bi.bigdata.xml.IXMLDefaultValue;
import com.newland.bi.bigdata.xml.Jdom2Paser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DpiParserXML
 *
 * @author chenqixu
 */
public class DpiParserXML {

    private static Logger logger = LoggerFactory.getLogger(DpiParserXML.class);
    private Jdom2Paser jdom2Paser;

    public IXMLChildren buildGnIXMLChildren() {
        return new DpiParserGNXML();
    }

    public void init(String path) throws Exception {
        jdom2Paser = new Jdom2Paser();
        jdom2Paser.init(path);
    }

    public void parser(String id) throws Exception {
        jdom2Paser.readXmlFile(id);
    }

    public void parser(String id, boolean isFirst) throws Exception {
        jdom2Paser.readXmlFile(id, isFirst);
    }

    public void parserByObject(String id, IXMLChildren ixmlChildren) throws Exception {
        jdom2Paser.readXmlFileByObject(id, ixmlChildren);
    }

    class DpiParserGNXML extends IXMLChildren {

        private String names = "name,keyWord,isDpi,value,dpirequestField,hwField,sinkField,rtmField,rtmkey,topic";

        public DpiParserGNXML() {
            initArray(names);
            initLinkedMap(names);
            addDefaultValueMap("keyWord", new IXMLDefaultValue() {
                @Override
                public String getValue() {
                    return valueMap.get("name").toUpperCase();
                }
            });
            addDefaultValueMap("isDpi", new IXMLDefaultValue() {
                @Override
                public String getValue() {
                    return "true";
                }
            });
            addDefaultValueMap("sinkField", new IXMLDefaultValue() {
                @Override
                public String getValue() {
                    String hwField = valueMap.get("hwField");
                    return valueMap.get("value") + (hwField != null && hwField.length() > 0 ? "," + valueMap.get("hwField") : "");
                }
            });
        }
    }
}
