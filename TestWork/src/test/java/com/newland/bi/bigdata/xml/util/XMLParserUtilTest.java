package com.newland.bi.bigdata.xml.util;

import org.junit.Before;
import org.junit.Test;

public class XMLParserUtilTest {

    private XMLParserUtil xmlParserUtil = null;
    private String[] comparekey = new String[]{"checkfilebakpath", "checkfilepath", "datafilepath", "errorfilebakpath", "sourcefilebakpath", "file_time_expression", "scanRule", "interface_id", "dst_path", "extension"};

    @Before
    public void setUp() {
        xmlParserUtil = new XMLParserUtil();
    }

    @Test
    public void compare() {
        // CELLMR
        String xmlpath1 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_CELLMR__d6251b682a434b63a3a02a0cadecea27/node-1757164349.xml";
        String xmlpath2 = "D:/tmp/聚合平台/分布式采集上线/1543886775774/NL-BD-ETL-BASE-COLLECT-RC-HW_CELLMR_FBSCJ__8ad3c50778c84773a114b629385725e9/node-1838390194.xml";
        xmlParserUtil.compare(xmlpath1, xmlpath2, "UTF-8", "param", comparekey);
        // MXDRUEMR
        xmlpath1 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_MXDRUEMR__18345de9eff240b787292a66abf2e2ba/node-1663055424.xml";
        xmlpath2 = "D:/tmp/聚合平台/分布式采集上线/1543886775774/NL-BD-ETL-BASE-COLLECT-RC-HW_MXDRUEMR_FBSCJ__a3083200419e4a129ae05d1ddc67abeb/node-588685593.xml";
        xmlParserUtil.compare(xmlpath1, xmlpath2, "UTF-8", "param", comparekey);
        // UEMR
        xmlpath1 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_UEMR__2a0e4214efd44f629c9f731799312af3/node301465081.xml";
        xmlpath2 = "D:/tmp/聚合平台/分布式采集上线/1543886775774/NL-BD-ETL-BASE-COLLECT-RC-HW_UEMR_FBSCJ__aafabd181c7d4dc780b853d965421a6f/node2063169434.xml";
        xmlParserUtil.compare(xmlpath1, xmlpath2, "UTF-8", "param", comparekey);
        // X2
        xmlpath1 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_X2__39fc50dd29d64767a649bf7f5cf7fe1e/node-588828482.xml";
        xmlpath2 = "D:/tmp/聚合平台/分布式采集上线/1543886775774/NL-BD-ETL-BASE-COLLECT-RC-HW_X2_FBSCJ__31ba36413dc249f4a0c7de988207d10f/node-1993902223.xml";
        xmlParserUtil.compare(xmlpath1, xmlpath2, "UTF-8", "param", comparekey);
        // UU && X2
        xmlpath1 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_X2__39fc50dd29d64767a649bf7f5cf7fe1e/node-588828482.xml";
        xmlpath2 = "D:/tmp/聚合平台/分布式采集上线/NL-BD-ETL-BASE-COLLECT-RC-HW_UU__28879596702c4568a1a2ebce6319b632/node-2014692784.xml";
        xmlParserUtil.compare(xmlpath1, xmlpath2, "UTF-8", "param", comparekey);
    }

    @Test
    public void print() {
        String xmlpath = "D:/tmp/聚合平台/分布式采集上线/1543886775774/NL-BD-ETL-BASE-COLLECT-RC-HW_CELLMR_FBSCJ__8ad3c50778c84773a114b629385725e9/node-1838390194.xml";
        xmlParserUtil.printSomeValue(xmlpath, "UTF-8", "param", comparekey);
    }
}