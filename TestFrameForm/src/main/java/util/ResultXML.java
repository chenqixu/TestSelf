package util;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.Document;
import org.dom4j.Element;

public class ResultXML {
    public Document nlxDoc;
    public String call_serverCode;
    public int result_code;
    public String result_itemcode;
    public String accept_id;
    public String response_time;
    public String result_desc;
    public String result_desc_forJs;
    public String result_detail;
    public String result_dealinfo;
    public XMLData xmldata;
    public boolean bXmldata;
    public String rtData;
    public String rtCheck_result;
    public String aFieldName[];
    public String rtDebugInfo;
    public String rtDebugInfo1;
    public String rtDebugInfo2;
    public String rtCallBsspTime;
    public String rtXmlCreate;
    public String rtXmlParse;
    public String recNlxID;
    public boolean rtFlag;
    private Properties pPropData;
    public boolean bParsePool;
    public boolean bShowMessage;
    public Vector oMessage;
    public static final int DEF_MESSAGE_TYPE = 0;
    public static final int DEF_MESSAGE_MEMO = 1;
    public boolean bRetfragment;
    public String sRetfragment_code;
    public String sRetfragment_file;
    
    public ResultXML()
    {
        nlxDoc = null;
        call_serverCode = "";
        result_code = 0;
        result_itemcode = "";
        accept_id = "";
        response_time = "";
        result_desc = "";
        result_desc_forJs = "";
        result_detail = "";
        result_dealinfo = "";
        xmldata = null;
        bXmldata = false;
        rtData = null;
        rtCheck_result = "";
        aFieldName = null;
        rtDebugInfo = "";
        rtDebugInfo1 = "";
        rtDebugInfo2 = "";
        rtCallBsspTime = "0";
        rtXmlCreate = "0";
        rtXmlParse = "0";
        recNlxID = "";
        rtFlag = false;
        pPropData = null;
        bParsePool = false;
        bShowMessage = false;
        oMessage = new Vector();
        bRetfragment = false;
        sRetfragment_code = "";
        sRetfragment_file = "";
    }

    private void readObject(ObjectInputStream ois)
        throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException
    {
        oos.defaultWriteObject();
    }

    /*public Result CopySelf()
    {
        ObjectInputStream oi;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(this);
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        oi = new ObjectInputStream(bi);
        return (Result)oi.readObject();
        Exception ex;
        ex;
        return null;
    }*/

    public String[] getColumnsName()
    {
        String reta[] = null;
        if(bXmldata)
            reta = xmldata.GetColumnsName();
        return reta;
    }

    public XMLData GetColumnsNode(String sColumnsName)
    {
        if(bXmldata)
            return xmldata.GetColumnsNode(sColumnsName);
        else
            return null;
    }

    public ResultXML GetColumnsResultXML()
    {
        ResultXML rxCurr = new ResultXML();
        if(bXmldata)
        {
            Element el = xmldata.GetColumnsElement();
            if(el != null)
            {
                try
                {
                    rxCurr.xmldata = new XMLData(el);
                    rxCurr.bXmldata = true;
                    rxCurr.rtFlag = true;
                    rxCurr.result_code = 0;
                }
                catch(Exception ex)
                {
                    rxCurr.result_code = -1;
                    rxCurr.result_desc = "\u65B0\u5EFA\u65B0\u7684Rx\u6570\u636E\u5305\u51FA\u9519!";
                    rxCurr.result_detail = (new StringBuilder()).append("\u65B0\u5EFA\u65B0\u7684Rx\u6570\u636E\u5305\u51FA\u9519!\u8BE6\u7EC6\u4FE1\u606F\uFF1A").append(ex.toString()).toString();
                    rxCurr.rtFlag = false;
                    //mLogger.error(result_detail);
                    System.out.println(result_detail);
                }
            } else
            {
                rxCurr.result_code = -1;
                rxCurr.result_desc = "\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684\u6570\u636E\u96C6\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728";
                rxCurr.result_detail = "\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684content\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728";
                rxCurr.rtFlag = false;
                //mLogger.error(result_detail);
                System.out.println(result_detail);
            }
        }
        return rxCurr;
    }

    public String getColumnsValue(String sColumnName)
    {
        String ret = "";
        if(bXmldata)
            ret = xmldata.GetColumnsValue(sColumnName);
        return ret;
    }

    public String getRowValue()
    {
        String ret = "";
        if(bXmldata)
            ret = xmldata.GetRowValue();
        return ret;
    }

    public String getColumnsValue(String sColumnName, int iPosition)
    {
        String ret = "";
        if(bXmldata)
            ret = xmldata.GetColumnsValue(sColumnName, iPosition);
        return ret;
    }

    private ResultXML getNewResultXML(String sNode, String sRowName)
    {
        ResultXML rx = new ResultXML();
        if(bXmldata)
            if(FirstNode().isCurrentDataBlock())
                rx = getNewResultXMLAll(sNode, sRowName, true);
            else
                rx = getNewResultXMLAll(sNode, sRowName, false);
        return rx;
    }

    private ResultXML getNewResultXMLAll(String sNode, String sRowName, boolean bFlag)
    {
        ResultXML rxCurr = new ResultXML();
        if(sNode == null || sNode.equals(""))
            return rxCurr;
        if(sRowName == null || sRowName.equals(""))
            return rxCurr;
        if(bXmldata)
        {
            Element el = null;
            if(bFlag)
                el = xmldata.GetColumnsElement();
            else
                el = xmldata.getParentPointer();
            if(el != null)
            {
                try
                {
                    Element elRowdata = null;
                    if(bFlag)
                        elRowdata = el;
                    else
                        elRowdata = el.element(sNode);
                    if(elRowdata != null)
                    {
                        rxCurr.xmldata = new XMLData(elRowdata);
                        rxCurr.xmldata.setRowFlagInfo(sRowName);
                        rxCurr.bXmldata = true;
                    }
                    if(elRowdata == null)
                    {
                        rxCurr.result_code = -1;
                        rxCurr.result_desc = "\u672A\u627E\u5230\u8BE5\u8282\u70B9";
                        rxCurr.result_desc = "\u672A\u627E\u5230\u8BE5\u8282\u70B9";
                        rxCurr.rtFlag = false;
                    }
                }
                catch(Exception ex)
                {
                    rxCurr.result_code = -1;
                    rxCurr.result_desc = "content\u8282\u70B9\u7ED3\u6784\u9519\u8BEF!!!";
                    rxCurr.result_detail = ex.getMessage();
                    rxCurr.rtFlag = false;
                    //mLogger.error(ex.getMessage());
                    System.out.println(ex.getMessage());
                }
            } else
            {
                rxCurr.result_code = -1;
                rxCurr.result_desc = "\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684\u6570\u636E\u96C6\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728";
                rxCurr.result_detail = "\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684content\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728";
                rxCurr.rtFlag = false;
                //mLogger.error("\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684content\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728");
                System.out.println("\u5F53\u524D\u8282\u70B9\u4E0D\u662F\u6709\u6548\u7684content\u8282\u70B9,\u6216\u8005\u8282\u70B9\u4E0D\u5B58\u5728");
            }
        }
        return rxCurr;
    }

    public int getCount()
    {
        int ret = 0;
        if(bXmldata)
            ret = xmldata.GetCount();
        return ret;
    }

    public int getPosition()
    {
        int ret = 0;
        if(bXmldata)
            ret = xmldata.getPosition();
        return ret;
    }

    public void Frist()
    {
        if(bXmldata)
            xmldata.Frist();
    }

    public void First()
    {
        if(bXmldata)
            xmldata.First();
    }

    public void Last()
    {
        if(bXmldata)
            xmldata.Last();
    }

    public void Next()
    {
        if(bXmldata)
            xmldata.Next();
    }

    public void Prior()
    {
        if(bXmldata)
            xmldata.Prior();
    }

    public void GoToPosition(int iPosi)
    {
        if(bXmldata)
            xmldata.GoToPosition(iPosi);
    }

    public boolean isEof()
    {
        boolean ret = true;
        if(bXmldata)
            ret = xmldata.isEof();
        return ret;
    }

    public String getPropData(String sParamName)
    {
        String ret = "";
        if(!pPropData.equals(null) && pPropData.size() != 0)
            ret = pPropData.getProperty(sParamName, "");
        return ret;
    }

    public int getPropSize()
    {
        int ret = 0;
        if(!pPropData.equals(null))
            ret = pPropData.size();
        return ret;
    }

    public void setPropData(String keyName, String Value)
    {
        pPropData.setProperty(keyName, Value);
    }

    public XMLData node(String nodeName, int iPosi)
    {
        XMLData ret = null;
        if(bXmldata)
            ret = FirstNode().node(nodeName, iPosi);
        return ret;
    }

    public XMLData node(String nodeName)
    {
        XMLData ret = null;
        if(bXmldata)
            ret = FirstNode().node(nodeName);
        return ret;
    }

    public XMLData FirstNode()
    {
        XMLData ret = null;
        if(bXmldata)
            ret = xmldata.FirstNode();
        return ret;
    }

    public int getNodeCurrCount(String nodeName)
    {
        int ret = 0;
        if(bXmldata)
            ret = FirstNode().getNodeCurrCount(nodeName);
        return ret;
    }

/*    public void ConvertNodeToBean(Object bean, String except_name, String rename)
    {
        if(bXmldata)
            if(FirstNode().isCurrentDataBlock())
                ConvertNodeToBeanAll(bean, except_name, rename, true);
            else
                ConvertNodeToBeanAll(bean, except_name, rename, false);
    }*/

/*    private void ConvertNodeToBeanAll(Object bean, String except_name, String rename, boolean bFlag)
    {
        if(!bXmldata) goto _L2; else goto _L1
_L1:
        Field aField[];
        String sRename[];
        String sExcept_name;
        String sFieldName;
        int i;
        aField = getAllDeclaredFields(bean);
        sRename = ExtString.split("", ";");
        if(rename != null)
            sRename = ExtString.split(rename.trim().toLowerCase(), ";");
        sExcept_name = "";
        if(except_name != null)
            sExcept_name = except_name.trim().toLowerCase();
        sFieldName = "";
        i = 0;
_L4:
        if(i >= aField.length)
            break; / * Loop/switch isn't completed * /
        sFieldName = aField[i].getName().trim();
        if(sExcept_name.indexOf(sFieldName.toLowerCase()) <= -1)
            try
            {
                Class cField = aField[i].getType();
                String sNodeName = getRename(sFieldName.toLowerCase(), sRename);
                String strValue = "";
                if(bFlag)
                    strValue = getColumnsValue(sNodeName);
                else
                    strValue = node(sNodeName).getValue();
                Object obj = getConertObj(cField, strValue);
                if(obj != null)
                    PropertyUtils.setProperty(bean, sFieldName, obj);
            }
            catch(Exception ex)
            {
                mLogger.warn((new StringBuilder()).append("\u5BF9\u50CF\uFF1A").append(bean.getClass().toString()).append("\u4E2D\u7684\u5C5E\u6027\uFF1A").append(sFieldName).append("\u53D1\u751F\u9519\u8BEF\u3002").toString());
            }
        i++;
        if(true) goto _L4; else goto _L3
_L2:
        mLogger.error("Init() \u6CA1\u6709\u6267\u884C");
_L3:
    }*/

    private Field[] getAllDeclaredFields(Object bean)
    {
        Vector vt = new Vector();
        getObjDeclaredFields(bean.getClass(), vt);
        Field aAllField[] = new Field[vt.size()];
        for(int i = 0; i < vt.size(); i++)
            aAllField[i] = (Field)vt.get(i);

        return aAllField;
    }

    private void getObjDeclaredFields(Class cClass, Vector vt)
    {
        Field aField[] = cClass.getDeclaredFields();
        for(int i = 0; i < aField.length; i++)
            vt.add(aField[i]);

        if(!cClass.getSuperclass().getName().equals("java.lang.Object"))
            getObjDeclaredFields(cClass.getSuperclass(), vt);
    }

    private Object getConertObj(Class cField, String strValue)
    {
        if(cField == java.lang.String.class)
        {
            if(strValue.equals(""))
                strValue = "";
            return strValue;
        }
        if(cField == java.util.Date.class)
            return this.getObjectDate(strValue);
        if(cField == Integer.TYPE || cField == java.lang.Integer.class)
        {
            if(strValue.equals(""))
                strValue = "0";
            return Integer.valueOf(strValue);
        }
        if(cField == Double.TYPE || cField == java.lang.Double.class)
        {
            if(strValue.equals(""))
                strValue = "0.0";
            return Double.valueOf(strValue);
        }
        if(cField == Float.TYPE || cField == java.lang.Float.class)
        {
            if(strValue.equals(""))
                strValue = "0.0";
            return Float.valueOf(strValue);
        }
        if(cField == Byte.TYPE || cField == java.lang.Byte.class)
        {
            if(strValue.equals(""))
                strValue = "0";
            return Byte.valueOf(strValue);
        }
        if(cField == Short.TYPE || cField == java.lang.Short.class)
        {
            if(strValue.equals(""))
                strValue = "0";
            return Short.valueOf(strValue);
        }
        if(cField == Long.TYPE || cField == java.lang.Long.class)
        {
            if(strValue.equals(""))
                strValue = "0";
            return Long.valueOf(strValue);
        }
        if(cField == Boolean.TYPE || cField == java.lang.Boolean.class)
        {
            if(strValue.equals(""))
                strValue = "0";
            return Boolean.valueOf(strValue);
        } else
        {
            return null;
        }
    }

 /*   public void ConvertNodeToBean(Object bean)
    {
        ConvertNodeToBean(bean, "", "");
    }*/

    private String getRename(String sFieldName, String sRepleaName[])
    {
        String ret = sFieldName;
        int i = 0;
        do
        {
            if(i >= sRepleaName.length)
                break;
            if(sRepleaName[i].indexOf(sFieldName) > -1)
            {
                //ret = ExtString.split(sRepleaName[i], ":")[1];
                ret = "";
                break;
            }
            i++;
        } while(true);
        return ret;
    }

    public void setRowFlagInfo(String rowName)
    {
        if(bXmldata)
            FirstNode().setRowFlagInfo(rowName);
    }

    public XMLData resetParent()
    {
        if(bXmldata)
        {
            resetParentPointer();
            return FirstNode();
        } else
        {
            return null;
        }
    }

    public void resetParentPointer()
    {
        if(bXmldata)
            FirstNode().resetParentPointer();
    }

    public void resetParentPointer(String sRowName)
    {
        if(bXmldata)
            FirstNode().resetParentPointer(sRowName);
    }

    public boolean isExistNode(String sNodeName)
    {
        boolean rt = false;
        if(bXmldata)
            rt = FirstNode().isExistNode(sNodeName);
        return rt;
    }

    public String getAttribute(String sAttrib)
    {
        String ret = "";
        if(bXmldata)
            ret = FirstNode().getAttribute(sAttrib);
        return ret;
    }

    public void setbFlag(boolean bFlag)
    {
        if(bXmldata)
            FirstNode().setbFlag(bFlag);
    }
    
    public static Date getObjectDate(String sDateTime)
    {
        Date dt = null;
        String sInputFormat = "";
        if(sDateTime.indexOf("/") > -1)
        {
            if(sDateTime.length() > 10)
                sInputFormat = "yyyy/MM/dd HH:mm:ss";
            else
                sInputFormat = "yyyy/MM/dd";
            dt = getStandDate(sInputFormat, sDateTime);
        }
        if(sDateTime.indexOf("-") > -1)
        {
            if(sDateTime.length() > 10)
                sInputFormat = "yyyy-MM-dd HH:mm:ss";
            else
                sInputFormat = "yyyy-MM-dd";
            dt = getStandDate(sInputFormat, sDateTime);
        }
        if(sDateTime.length() == 8 || sDateTime.length() == 14)
        {
            if(sDateTime.length() == 14)
                sInputFormat = "yyyyMMddHHmmss";
            else
                sInputFormat = "yyyyMMdd";
            dt = getStandDate(sInputFormat, sDateTime);
        }
        return dt;
    }
    

    public static Date getStandDate(String sFormat, String sTimeStr)
    {
        SimpleDateFormat df = new SimpleDateFormat(sFormat);
        try {
			return df.parse(sTimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}       
    }


}

