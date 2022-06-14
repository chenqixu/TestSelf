package com.bussiness.bi.bigdata.xml.dpi;

import com.bussiness.bi.bigdata.xml.dpi.DpiParserXML;
import org.junit.Before;
import org.junit.Test;

public class DpiParserXMLTest {

    private DpiParserXML dpiParserXML;
    private String conf = "d:\\Work\\实时\\DPI实时解析\\配置\\";
    private String[] type = {"HwLTEConf.xml", "HwLTETypeDef.xml", "GnXdrTypeDef.xml"};

    @Before
    public void setUp() throws Exception {
        dpiParserXML = new DpiParserXML();
    }

    @Test
    public void parserHwLTEConf() throws Exception {
        dpiParserXML.init(conf + type[0]);
        dpiParserXML.parser("commonDef");
    }

    @Test
    public void parserHwLTETypeDef() throws Exception {
        dpiParserXML.init(conf + type[1]);
        dpiParserXML.parser("typeDef");
    }

    @Test
    public void parserGnXdrTypeDef() throws Exception {
        dpiParserXML.init(conf + type[2]);
        dpiParserXML.parserByObject("typeDef", dpiParserXML.buildGnIXMLChildren());
    }
}