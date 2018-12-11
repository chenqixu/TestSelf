package com.newland.bi.bigdata.xml.util;

import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;
import com.newland.bi.ResultXML;
import com.newland.bi.XMLData;
import com.newland.bi.bigdata.changecode.ChangeCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML解析工具
 *
 * @author chenqixu
 * @date 2018/12/4 11:49
 */
public class XMLParserUtil {

    private static Logger logger = LogInfoFactory.getInstance(XMLParserUtil.class);
    private ChangeCode changeCode = null;
    private ResultXML rx = null;
    private XMLData xd = null;

    public XMLParserUtil() {
        this.changeCode = new ChangeCode();
    }

    /**
     * 补充XML头部和尾部
     *
     * @param xmlValueList
     * @return
     */
    public String createXmlData(List<String> xmlValueList) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<test>");
        for (String xmlValue : xmlValueList)
            sb.append(xmlValue);
        sb.append("</test>");
        return sb.toString();
    }

    /**
     * 按照NODE解析XML
     *
     * @param xmlData
     * @param nodeName
     * @return
     */
    public Map<String, String> readNode(String xmlData, String nodeName) {
        Map<String, String> map = new HashMap<>();
        this.rx = new ResultXML();
        this.xd = new XMLData(xmlData);
        rx.rtFlag = true;
        rx.bXmldata = true;
        rx.xmldata = xd;
        rx.setbFlag(false);
        rx.setRowFlagInfo(nodeName);
        rx.First();
        int i = 0;
        while (!rx.isEof()) {
            map.put(rx.node(nodeName, i).getAttribute("name"), rx.getRowValue());
            rx.Next();
            i++;
        }
        return map;
    }

    /**
     * 读取文件到List
     *
     * @param xmlPath
     * @param readCode
     * @param rule
     * @return
     */
    public List<String> readToLine(String xmlPath, String readCode, String rule) {
        List<String> resultlist = new ArrayList<>();
        changeCode.setRead_code(readCode);
        for (String value : changeCode.read(xmlPath)) {
            if (value.indexOf(rule) > 0) {
                resultlist.add(value.trim());
            }
        }
        return resultlist;
    }

    /**
     * 更新map
     *
     * @param map
     * @param keylist
     */
    public void mapUpdate(Map<String, String> map, String[] keylist) {
        Map<String, String> resultmap = new HashMap<>();
        for (String key : keylist) {
            resultmap.put(key, map.get(key));
        }
        map.clear();
        map.putAll(resultmap);
    }

    /**
     * 比较2个xml
     *
     * @param xmlPath1
     * @param xmlPath2
     * @param readCode
     * @param rule
     */
    public void compare(String xmlPath1, String xmlPath2, String readCode, String rule, String[] comparekey) {
        List<String> resultlist1 = readToLine(xmlPath1, readCode, rule);
        List<String> resultlist2 = readToLine(xmlPath2, readCode, rule);
        Map<String, String> map1 = readNode(createXmlData(resultlist1), rule);
        Map<String, String> map2 = readNode(createXmlData(resultlist2), rule);
        mapUpdate(map1, comparekey);
        mapUpdate(map2, comparekey);
        Component c1 = new Component();
        Component c2 = new Component();
        c1.setParamMap(map1);
        c2.setParamMap(map2);
        boolean isqueals = c1.equals(c2);
        logger.info("c1.equals(c2)：{}", isqueals);
        if (!isqueals)
            c1.diff(c2);
    }

    /**
     * 根据规则打印参数
     *
     * @param xmlPath
     * @param readCode
     * @param rule
     * @param comparekey
     */
    public void printSomeValue(String xmlPath, String readCode, String rule, String[] comparekey) {
        List<String> resultlist = readToLine(xmlPath, readCode, rule);
        Map<String, String> map = readNode(createXmlData(resultlist), rule);
        mapUpdate(map, comparekey);
        Component c1 = new Component();
        c1.setParamMap(map);
        c1.print();
    }

}
