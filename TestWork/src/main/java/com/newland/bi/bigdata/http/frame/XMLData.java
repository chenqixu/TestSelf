package com.newland.bi.bigdata.http.frame;

import java.util.List;
import org.dom4j.*;

/**
 * @ClassName XMLData
 * @Description XML数据,引用BOSS框架
 * @author 陈棋旭
 * @version 1.0 2015-10-17 创建
 * */
public class XMLData {
    private Document document;
    private String rootNodeName;
    private Element rootElement;
    private Element parentPointer;
    private List Rowlist;
    private boolean bFlag;
    private Element elemPointer;
    private Element eRowData;
    private int iPosi;
    private int iRowCount;
    public String ROWNAME;
    public String CountRowTime;
    
    public XMLData(Element eRowData)
    {
        document = null;
        rootNodeName = "RootNode";
        rootElement = null;
        parentPointer = null;
        Rowlist = null;
        bFlag = true;
        elemPointer = null;
        this.eRowData = null;
        iPosi = 0;
        iRowCount = 0;
        ROWNAME = "row";
        CountRowTime = "0";
        //bFlag = BossSysConf.getbCharstate();
        rootElement = eRowData;
        elemPointer = rootElement;
        parentPointer = rootElement;
        this.eRowData = eRowData;
        iRowCount = GetDataCount();
    }

    public XMLData(String eRowData)
    {
        document = null;
        rootNodeName = "RootNode";
        rootElement = null;
        parentPointer = null;
        Rowlist = null;
        bFlag = true;
        elemPointer = null;
        this.eRowData = null;
        iPosi = 0;
        iRowCount = 0;
        ROWNAME = "row";
        CountRowTime = "0";
        //bFlag = BossSysConf.getbCharstate();
        try
        {
            document = DocumentHelper.parseText(eRowData);
            document.setXMLEncoding("GBK");//BossSysConf.getEncoding()
            rootElement = document.getRootElement();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        elemPointer = rootElement;
        this.eRowData = rootElement;
        parentPointer = rootElement;
        iRowCount = GetDataCount();
    }

    public void setRowFlagInfo(String sRowName)
    {
        if(null == sRowName || "".equals(sRowName.trim()))
            return;
        ROWNAME = sRowName;
        if(bFlag)
            ROWNAME = ROWNAME.toLowerCase();
        iRowCount = GetDataCount();
    }

    public int GetCount()
    {
        return iRowCount;
    }

    public String GetColumnsValue(String sColumnName)
    {
        if(!isEof())
            return GetColumnsValue(sColumnName, iPosi);
        else
            return "";
    }

    public String getAttribute(String sAttrib)
    {
        String ret = "";
        ret = elemPointer.attributeValue(sAttrib);
        if(null == ret)
            ret = "";
        return ret;
    }

    public String GetColumnsValue(String sColumnName, int iPosition)
    {
        String ret = "";
        Element eNode = parentPointer;
        Element node = null;
        String sColName = sColumnName;
        if(bFlag)
            sColName = sColName.toLowerCase();
        if(eNode != null)
        {
            if(iPosition < iRowCount && iPosition > -1)
            {
                node = (Element)Rowlist.get(iPosition);
                //if(BossSysConf.getToolsDebug())
                //    mLogger.debug((new StringBuilder()).append("iPosition:").append(String.valueOf(iPosition)).append("node.asXML()").append(node.asXML()).toString());
                ret = GetElementValue(node.element(sColName));
            }
        } else {
        	System.out.println((new StringBuilder()).append("Node ").append(eNode.getName()).append(" is Null").toString());
        }
        return ret;
    }

    public String[] GetColumnsName()
    {
        String ret[] = new String[0];
        Element eNode = parentPointer;
        Element eDATA = null;
        if(eNode != null)
        {
            List list = eNode.elements(ROWNAME);
            if(list.size() > 0)
            {
                for(int i = 0; i < list.size(); i++)
                    eDATA = (Element)list.get(i);

                //if(BossSysConf.getToolsDebug())
                //    mLogger.debug((new StringBuilder()).append("eDATA.asXML()").append(eDATA.asXML()).toString());
                if(eDATA != null)
                {
                    List listEl = eDATA.elements();
                    ret = new String[listEl.size()];
                    for(int i = 0; i < listEl.size(); i++)
                    {
                        Element node = (Element)listEl.get(i);
                        ret[i] = node.getName().trim();
                    }

                }
            }
        } else {
        	System.out.println((new StringBuilder()).append("Node ").append(eNode.getName()).append(" is null").toString());
        }
        return ret;
    }

    public int getPosition()
    {
        return iPosi;
    }

    public void Frist()
    {
        iPosi = 0;
    }

    public void First()
    {
        iPosi = 0;
    }

    public void Last()
    {
        if(iRowCount > 0)
            iPosi = iRowCount - 1;
        if(iRowCount == 0)
            iPosi = 0;
    }

    public void Next()
    {
        if(iRowCount > iPosi)
            iPosi = iPosi + 1;
    }

    public void Prior()
    {
        if(iPosi > 0)
            iPosi = iPosi - 1;
    }

    private int GetDataCount()
    {
        int ret = 0;
        ret = CalDataCount(parentPointer);
        First();
        return ret;
    }

    public void GoToPosition(int iPosi)
    {
        if(iPosi > -1 && iPosi < iRowCount)
            this.iPosi = iPosi;
    }

    public boolean isEof()
    {
        boolean rt = true;
        if(iPosi > iRowCount - 1)
            rt = true;
        else
            rt = false;
        return rt;
    }

    public Element getRowData()
    {
        return eRowData;
    }

    private String GetElementValue(Element et)
    {
        String ret = "";
        if(et != null)
            ret = et.getText();
        if(ret == null)
            ret = "";
        return ret;
    }

    private int CalDataCount(Element et)
    {
        int ret = 0;
        if(et != null)
        {
            long lTime = System.currentTimeMillis();
            Rowlist = et.elements(ROWNAME);
            ret = Rowlist.size();
            CountRowTime = String.valueOf(System.currentTimeMillis() - lTime);
        }
        return ret;
    }

    public XMLData node(String nodeName, int iPosi)
    {
        String sNodeName = nodeName.trim();
        String sError = "";
        if(sNodeName == null || sNodeName.equals(""))
        {
            sError = "\u8282\u70B9\u540D\u79F0\u4E3A\u7A7A";
            //mLogger.error(sError);
            System.out.println(sError);
        }
        if(bFlag)
            sNodeName = sNodeName.toLowerCase();
        if(iPosi < 0)
        {
            sError = "iPosi\u5E94\u5927\u4E8E\u6216\u7B49\u4E8E0";
            //mLogger.error(sError);
            System.out.println(sError);
        }
        List list = elemPointer.elements(sNodeName);
        int iNodeSize = list.size();
        if(iNodeSize == 0)
        {
            sError = (new StringBuilder()).append("\u5F53\u524D\u6307\u9488:").append(elemPointer.getName()).append(",\u8282\u70B9:").append(sNodeName).append("\u4E0D\u5B58\u5728,iPosi \u4F4D\u7F6E\u51FA\u9519(\u5F53\u524DiPosi=").append(iPosi).append(")").toString();
            //mLogger.info(sError);
            System.out.println(sError);
        } else
        if(iPosi < iNodeSize)
        {
            elemPointer = (Element)list.get(iPosi);
        } else
        {
            sError = (new StringBuilder()).append("\u5F53\u524D\u6307\u9488:").append(elemPointer.getName()).append(",\u8282\u70B9:").append(sNodeName).append("\u5B58\u5728,\u4F46iPosi \u4F4D\u7F6E\u51FA\u9519,(\u5F53\u524DiPosi=").append(iPosi).append("\uFF0C\u6700\u5927\u4E3A\uFF1A").append(iNodeSize).append(")").toString();
            //mLogger.info(sError);
            System.out.println(sError);
        }
        return this;
    }

    public XMLData node(String nodeName)
    {
        XMLData nodeD = null;
        nodeD = node(nodeName, 0);
        return nodeD;
    }

    public XMLData FirstNode()
    {
        if(parentPointer == null)
            elemPointer = rootElement;
        else
            elemPointer = parentPointer;
        return this;
    }

    public void setParentPointer()
    {
        setParentPointer("");
    }

    public void setParentPointer(String sRowName)
    {
        parentPointer = elemPointer;
        iRowCount = 0;
        if(sRowName != null && !sRowName.trim().equals(""))
            setRowFlagInfo(sRowName);
    }

    public void resetParentPointer()
    {
        resetParentPointer("");
    }

    public void resetParentPointer(String sRowName)
    {
        parentPointer = rootElement;
        elemPointer = rootElement;
        iRowCount = 0;
        if(sRowName != null && !sRowName.trim().equals(""))
            setRowFlagInfo(sRowName);
    }

    public String getValue()
    {
        String ret = "";
        ret = elemPointer.getTextTrim();
        return ret;
    }

    public int getNodeCurrCount(String nodeName)
    {
        String sNodeName = nodeName;
        if(bFlag)
            sNodeName = sNodeName.toLowerCase();
        List list = elemPointer.elements(sNodeName);
        return list.size();
    }

    public String getDocXML()
    {
        return (new StringBuilder()).append("<?xml version='1.0' encoding='GBK' ?>").append(rootElement.asXML()).toString();
    }

    public XMLData GetColumnsNode(String sColumnsName)
    {
        elemPointer = parentPointer;
        if(iPosi < iRowCount && iPosi > -1)
        {
            elemPointer = (Element)Rowlist.get(iPosi);
            elemPointer = elemPointer.element(sColumnsName);
            if(elemPointer == null)
                elemPointer = parentPointer;
        }
        return this;
    }

    protected Element GetColumnsElement()
    {
        Element el = parentPointer;
        if(iPosi < iRowCount && iPosi > -1)
            el = (Element)Rowlist.get(iPosi);
        return el;
    }

    protected Element getCurrentElement()
    {
        Element eNode = parentPointer;
        Element rtNode = null;
        boolean retb = false;
        if(iPosi < iRowCount && iPosi > -1)
            rtNode = (Element)Rowlist.get(iPosi);
        return rtNode;
    }

    public String GetRowValue()
    {
        return GetElementValue(getCurrentElement());
    }

    public Element getParentPointer()
    {
        return parentPointer;
    }

    public boolean isCurrentDataBlock()
    {
        boolean rt = false;
        if(iRowCount > 0)
            rt = true;
        return rt;
    }

    public boolean isExistNode(String sNodeName)
    {
        boolean rt = false;
        if(sNodeName == null || sNodeName.equals(""))
            return rt;
        Element eNode = elemPointer;
        List list = eNode.elements(sNodeName);
        if(list != null && list.size() > 0)
            rt = true;
        return rt;
    }

    public void setbFlag(boolean bFlag)
    {
        this.bFlag = bFlag;
    }
}
