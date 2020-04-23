package com.cqx.common.utils.xml;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class XMLParserTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(XMLParserTest.class);
    private XMLParser xmlParser;

    @Before
    public void setUp() {
        xmlParser = new XMLParser();
    }

    @Test
    public void xmlParser() throws Exception {
        xmlParser.setFileName("d:\\Work\\ETL\\天空\\组件脚本调用\\101169491131\\node297342823.xml");
        xmlParser.init();
        List<XMLParserElement> actionList = xmlParser.parseRootChildElement("action");
        List<XMLParserElement> dogList = xmlParser.getChildElement(actionList, "dog");
        for (XMLParserElement xmlParserElement : dogList) {
            logger.info("xml：{}", xmlParserElement.toXml());
        }
    }

    @Test
    public void xmlToMap() throws Exception {
        String param = XMLParser.XML_HEADER + "<dog xmlns=\"uri:dog\" id=\"node3834\" type=\"STREAM_SPOUT\">\r\n"
                + "      <desc><![CDATA[SFTP文本上报至6期集团网关]]></desc>\n"
                + "      <component name=\"send-ftp-jk\" version=\"1.0\">\n"
                + "        <param name=\"redis_cfg_env\"><![CDATA[Redis_collect]]></param>\n"
                + "        <param name=\"auto_create_null_file\"><![CDATA[yes]]></param>\n"
                + "        <param name=\"ftp_cfg_env\"><![CDATA[sftp_10.46.131.136_jingfen]]></param>\n"
                + "        <param name=\"dst_path\"><![CDATA[/bi/data/IF_UPLOAD_BOSS/HOME_Log_Server/FJ/YDFJQ00003/]]></param>\n"
                + "        <param name=\"local_bak_path\"><![CDATA[/tmp/bi/databackup/if_upload_hb_netlog/${run_date}/]]></param>\n"
                + "        <param name=\"dst_link_path\"><![CDATA[/tmp/bi/dataprocess/data_temporary/if_upload_hb_netlog/${run_date}/]]></param>\n"
                + "        <param name=\"extension\"><![CDATA[01-${device_id}-${seq}-${file_start_time}-${file_end_time}-${record_count}-${md5}-${file_size}.txt.gz]]></param>\n"
                + "        <param name=\"max_line\"><![CDATA[10000]]></param>\n"
                + "        <param name=\"interface_id\"><![CDATA[if_upload_iptrace_jitian]]></param>\n"
                + "        <param name=\"max_time\"><![CDATA[15]]></param>\n"
                + "      </component>"
                + "    </dog>";
        xmlParser.setXmlData(param);
        xmlParser.init();
        List<XMLParserElement> componentList = xmlParser.parseRootChildElement("component");
        List<XMLParserElement> paramList = xmlParser.getChildElement(componentList, "param");
        for (XMLParserElement xmlParserElement : paramList) {
            logger.info("param.put(\"{}\", \"{}\");", xmlParserElement.getAttributeName(), xmlParserElement.getElementText());
        }
    }
}