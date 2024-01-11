package com.cqx.common.utils.xml;

import com.cqx.common.utils.file.FileCount;
import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLParserTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(XMLParserTest.class);
    private XMLParser xmlParser;

    @Before
    public void setUp() {
        xmlParser = new XMLParser();
    }

    @Test
    public void xmlParser() throws Exception {
        Map<String, Map<String, String>> params = new HashMap<>();
        xmlParser.setFileName("d:\\Work\\ETL\\天空\\组件脚本调用\\101169491131\\node297342823.xml");
        xmlParser.init();
        List<XMLParserElement> actionList = xmlParser.parseRootChildElement("action");
        List<XMLParserElement> dogList = xmlParser.getChildElement(actionList, "dog");
        for (XMLParserElement xmlParserElement : dogList) {
            String id = xmlParserElement.getAttributeValueByID("id");
            logger.info("id：{}，xml：{}", id, xmlParserElement.toXml());
            Map<String, String> param = new HashMap<>();
            XMLParser x = new XMLParser();
            x.setXmlData(xmlParserElement.toXml());
            x.init();
            List<XMLParserElement> componentList = x.parseRootChildElement("component");
            List<XMLParserElement> paramList = x.getChildElement(componentList, "param");
            for (XMLParserElement xe : paramList) {
                logger.info("param.put(\"{}\", \"{}\");", xe.getAttributeName(), xe.getElementText());
                param.put(xe.getAttributeName(), xe.getElementText());
            }
            params.put(id, param);
        }
        logger.info("input_data：{}", params.get("node1498383198742").get("input_data"));
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

    @Test
    public void parserOrder() throws Exception {
        xmlParser.setFileName("d:\\Work\\实时\\实时中台\\B域需求\\2023年关于实时运营场景的搭建需求\\10210023.xml");
        xmlParser.init();
        Map<String, Map<String, String>> productMap = new HashMap<>();
        List<XMLParserElement> order_param_infoList = xmlParser.parseRootChildElement("order_param_info");
        for (XMLParserElement order_param_info : order_param_infoList) {
            for (XMLParserElement _x : order_param_info.getChildElementList()) {
                if (_x.getElementName().equals("biz_info")) {
                    List<XMLParserElement> user_productList = xmlParser.getChildElement(_x.getChildElementList(), "user_product");
                    // 多个user_product
                    for (XMLParserElement user_product : user_productList) {
                        Map<String, String> user_productMap = new HashMap<>();
                        for (XMLParserElement _u : user_product.getChildElementList()) {
                            String key = _u.getElementName();
                            String value = _u.getElementText();
                            if (key.equals("product_id")) {
                                productMap.put(value, user_productMap);
                            }
                            user_productMap.put(key, value);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : productMap.entrySet()) {
            logger.info("{}", entry);
        }
    }

    @Test
    public void parserOrderList() throws Exception {
        AtomicInteger s1 = new AtomicInteger();
        AtomicInteger s2 = new AtomicInteger();
        FileUtil fileUtil = new FileUtil();
        fileUtil.setReader("d:\\Work\\实时\\实时中台\\B域需求\\2023年关于实时运营场景的搭建需求\\oc_user_order0.txt");
        try {
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    xmlParser.setXmlData(content);
                    xmlParser.init();
                    Map<String, Map<String, String>> productMap = new HashMap<>();
                    List<String> productList = new ArrayList<>();
                    List<XMLParserElement> order_param_infoList = xmlParser.parseRootChildElement("order_param_info");
                    for (XMLParserElement order_param_info : order_param_infoList) {
                        for (XMLParserElement _x : order_param_info.getChildElementList()) {
                            if (_x.getElementName().equals("biz_info")) {
                                List<XMLParserElement> user_productList = xmlParser.getChildElement(_x.getChildElementList(), "user_product");
                                // 多个user_product
                                for (XMLParserElement user_product : user_productList) {
                                    Map<String, String> user_productMap = new HashMap<>();
                                    for (XMLParserElement _u : user_product.getChildElementList()) {
                                        String key = _u.getElementName();
                                        String value = _u.getElementText();
                                        if (key.equals("product_id")) {
                                            productList.add(value);
                                            productMap.put(value, user_productMap);
                                        }
                                        user_productMap.put(key, value);
                                    }
                                }
                            }
                        }
                    }
                    logger.info("size={}, {}", productList.size(), productList);
                    if (productList.size() == 1) {
                        s1.incrementAndGet();
                    } else {
                        s2.incrementAndGet();
                    }
                }
            });
        } finally {
            fileUtil.closeRead();
        }
        logger.info("s1={} s2={}", s1.get(), s2.get());
    }
}