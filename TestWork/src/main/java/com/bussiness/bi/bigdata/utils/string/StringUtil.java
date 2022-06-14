package com.bussiness.bi.bigdata.utils.string;


/*
 * @(#) $Id: StringUtil.java,v 1.1 2016/07/29 03:09:10 chenjianzeng Exp $
 *
 * Copyright (c) 2006 福建新大陆软件工程有限公司 版权所有
 * Newland Co. Ltd. All rights reserved.

 * This software is the confidential and proprietary
 * information of Newland Co. Ltd.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Newland Co. Ltd
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

/**
 * StringUtil.java
 * <p>
 * description: 字符串操作通用工具
 * </p>
 *
 * @author 孔扬
 * @version 1.0.0
 * @since Jan 13, 2007
 * history 1.0.0 2007-01-13 created by kongyang<br>
 * 1.0.1 2007-01-25 modify by zhangjh  <br>
 * 1 增加 将String转换为int Str2Int(String str) 将String转换为double Str2double(String str)
 * 1.0.2 2007-6-7  modify by guoss <br>
 * 增加过滤时对"+"处理
 * 1.0.3 2007-09-10 modified by kongyang<br>
 * 1 新增压缩字节和字符串的函数
 * 1.0.4 2007-10-25 modify by sury
 * 1 增加isBlank方法
 */
public class StringUtil {
    public static final String nullString = "null";

    public StringUtil() {
    }

    /**
     * 提供替换字符串的功能,区分大小写
     *
     * @param sb      为要被处理的StringBuffer
     * @param sStr    为被替换的子字符串
     * @param sRepStr 为替换进去的子字符串
     * @return 无
     */
    public static void replaceStr(StringBuffer sb, String sStr, String sRepStr) {
        try {
            if ((sb == null) || (sStr == null) || (sRepStr == null))
                return;

            if ((sb.length() == 0) || (sStr.length() == 0))
                return;

            int iStartIndex = 0;
            int iLen = sb.length();
            //int iEndIndex = 0;
            int iLen2 = sStr.length();

            while (iStartIndex < iLen) {
                if (sb.substring(iStartIndex, iLen2 + iStartIndex).equals(sStr)) {
                    sb.replace(iStartIndex, iLen2 + iStartIndex, sRepStr);
                    iLen = sb.length();
                    iStartIndex = iStartIndex + sRepStr.length();
                } else
                    iStartIndex++;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 提供替换字符串的功能,区分大小写
     *
     * @param sSrcStr 为要被处理的字符串
     * @param sStr    为被替换的子字符串
     * @param sRepStr 为替换进去的子字符串
     * @return String 替换后的字符串,出现异常返回原字符串
     */
    public static String replaceStr(String sSrcStr, String sStr, String sRepStr) {
        try {
            if ((sSrcStr == null) || (sStr == null) || (sRepStr == null))
                return sSrcStr;

            if ((sSrcStr.length() == 0) || (sStr.length() == 0))
                return sSrcStr;

            StringBuffer sb = new StringBuffer(sSrcStr);
            replace(sb, sStr, sRepStr);
            return new String(sb);
        } catch (Exception e) {
            return sSrcStr;
        }
    }


    /**
     * 判断字符串是否数字
     *
     * @param str 字符串
     * @return boolean true - 是数字组成 false - 非完全数字组成
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 过滤HTML标签，把<, >, ", &, 进行转换
     *
     * @param input 输入字符串
     * @return 转换后的字符串
     */
    public static String filterHTMLTag(String input) {
        StringBuffer buffer = new StringBuffer(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '<') {
                buffer.append("&lt;");
                continue;
            } else if (c == '>') {
                buffer.append("&gt;");
                continue;
            } else if (c == '"') {
                buffer.append("&quot;");
                continue;
            } else if (c == '+') { //V1.0.2
                buffer.append("&#43;");
                continue;
            } else if (c == '&')
                buffer.append("&amp;");
            else
                buffer.append(c);
        }

        return buffer.toString();
    }

    /**
     * 获取某个月份的天数
     *
     * @param mon 月份
     * @return int 天数
     */
    public static int getDays(int mon) {
        int mon_mm = mon % 100;
        int mon_yyyy = mon / 100;
        int count;
        if (mon_mm < 13) {
            if ((mon_yyyy % 400 == 0 || mon_yyyy % 4 == 0 && mon_yyyy % 100 > 0) && mon_mm == 2)
                count = 29;
            else if (mon_mm == 1 || mon_mm == 3 || mon_mm == 5 || mon_mm == 7 || mon_mm == 8
                    || mon_mm == 10 || mon_mm == 12)
                count = 31;
            else if (mon_mm == 4 || mon_mm == 6 || mon_mm == 9 || mon_mm == 11)
                count = 30;
            else
                count = 28;
        } else {
            count = 1;
        }
        return count;
    }

    /**
     * 字符串转换，把'->'', 为了sql语句使用
     *
     * @param s
     * @return String 转换后的字符串
     */
    public static String convertStr(String s) {
        String oldStr = s;
        String newStr;
        if (oldStr.indexOf("'") != -1)
            newStr = oldStr.replaceAll("\\'", "''");
        else
            newStr = oldStr;
        return newStr;
    }

    /**
     * 字符串转换，ISO-》GBK
     *
     * @param msg
     * @return
     */
    public static String convertString(String msg) {
        try {
            String convertMsg = new String(msg.getBytes("ISO-8859-1"), "GBK");
            String s = convertMsg;
            return s;
        } catch (UnsupportedEncodingException ex) {
            String s1 = "can't not encoding code";
            return s1;
        }
    }

    /**
     * 取Cookie的值
     *
     * @param cookieName cookie名称
     * @param req        Http请求对象
     * @return String 具体值
     */
    public static String getCookieValue(String cookieName, HttpServletRequest req) {
        Cookie cookies[] = req.getCookies();
        if (cookies == null)
            return "";
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equals(cookie.getName()))
                return cookie.getValue();
        }

        return "";
    }

    /**
     * 设置Cookie
     *
     * @param cookie
     * @param maxAge 最长存在时间
     * @param res    Http应答
     */
    public static void setCookie(Cookie cookie, int maxAge, HttpServletResponse res) {
        cookie.setMaxAge(maxAge);
        res.addCookie(cookie);
    }

    /**
     * 是否存在这个Cookie
     *
     * @param cookieName cookie名称
     * @param req        Http请求对象
     * @return boolean
     */
    public static boolean isSetCookie(String cookieName, HttpServletRequest req) {
        return getCookieValue(cookieName, req) != null;
    }

    /**
     * 整型转成16进制字符串
     *
     * @param i
     * @return
     */
    public static String toHexString(int i) {
        String sTemp = Integer.toHexString(i).toUpperCase();
        return sTemp.length() >= 2 ? sTemp : "0".concat(String.valueOf(String.valueOf(sTemp)));
    }

    /**
     * 加密密码
     *
     * @param pass
     * @return
     */
    public static String bindPassword(String pass) {
        String sRet = "";
        int strLen = pass.length();
        for (int i = 0; i < strLen; i++) {
            String sTemp = toHexString(pass.charAt(i) + "FMCCABT".charAt(i % 4));
            if (sTemp.length() != 2)
                sTemp = String.valueOf(String.valueOf((new StringBuffer("")).append(i + 1).append(
                        sTemp)));
            sRet = String.valueOf(sRet) + String.valueOf(sTemp);
        }

        for (int i = 0; i < 15 - strLen; i++)
            sRet = String.valueOf(sRet)
                    + String.valueOf(toHexString("FMCCABT".charAt(i % 4) + strLen));

        sRet = String.valueOf(sRet) + String.valueOf(toHexString(strLen + 6));
        return sRet;
    }

    /**
     * 16进制转成int
     *
     * @param sHex
     * @return
     */
    public static int hexStringToInt(String sHex) {
        try {
            int i = Integer.parseInt(sHex, 16);
            return i;
        } catch (NumberFormatException ex) {
            int j = 0;
            return j;
        }
    }

    /**
     * 解密函数
     *
     * @param pass
     * @return
     */
    public static String unBindPassword(String pass) {
        if (pass.length() != 32)
            return "";
        String sTemp = pass.substring(30, 32);
        int strLen = hexStringToInt(sTemp) - 6;
        if (strLen > 30)
            return "";
        String sRet = "";
        for (int i = 0; i < strLen; i++) {
            sTemp = pass.substring(i * 2, i * 2 + 2);
            sRet = String.valueOf(sRet)
                    + String.valueOf((char) (hexStringToInt(sTemp) - "FMCCABT".charAt(i % 4)));
        }

        return sRet;
    }

    /**
     * 格式化long类型的日期串
     *
     * @param dt
     * @return
     */
    public static String formatDateNumber(long dt) {
        new String();
        String datestr = String.valueOf(dt);
        String ret = "";
        if (datestr.length() == 8) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            ParsePosition pp = new ParsePosition(0);
            java.util.Date d = sdf.parse(datestr, pp);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            ret = sd.format(d);
        } else if (datestr.length() == 6) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            ParsePosition pp = new ParsePosition(0);
            java.util.Date d = sdf.parse(datestr, pp);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
            ret = sd.format(d);
        } else {
            ret = "format error";
        }
        return ret;
    }

    /**
     * 格式化百分比类型double
     *
     * @param per
     * @return
     */
    public static String formatPercent(double per) {
        String ret = "";
        NumberFormat nf = NumberFormat.getPercentInstance(Locale.PRC);
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(1);
        ret = nf.format(per);
        return ret;
    }

    /**
     * 解析错误ID的描述，0 - 等待， 1 - 运行中， 2 - 错误， 9 - 正确， 其他 - 未知
     *
     * @param errid
     * @return
     */
    public static String decodeError(int errid) {
        String ret = "";
        switch (errid) {
            case 0: // '\0'
                ret = "Waiting...";
                break;

            case 1: // '\001'
                ret = "Running...";
                break;

            case 2: // '\002'
                ret = "Error!";
                break;

            case 9: // '\t'
                ret = "Normal";
                break;

            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
            case 6: // '\006'
            case 7: // '\007'
            case 8: // '\b'
            default:
                ret = "Unknown";
                break;
        }
        return ret;
    }

    /**
     * 解析类型描述 1 - 日 2 - 周 5 - 月 其他
     *
     * @param type
     * @return
     */
    public static String decodeType(int type) {
        String ret = "";
        switch (type) {
            case 1: // '\001'
                ret = "日";
                break;

            case 2: // '\002'
                ret = "周";
                break;

            case 5: // '\005'
                ret = "月";
                break;

            case 3: // '\003'
            case 4: // '\004'
            default:
                ret = "其他";
                break;
        }
        return ret;
    }

    /**
     * 解析ETL类型 D-日 W-周 M-月 Y-年
     *
     * @param type
     * @return
     */
    public static String decodeETLType(String type) {
        String ret = "";
        if (type.equals("D"))
            ret = "日";
        else if (type.equals("W"))
            ret = "周";
        else if (type.equals("M"))
            ret = "月";
        else if (type.equals("Y"))
            ret = "年";
        else
            ret = "其他";
        return ret;
    }

    /**
     * 解析数值，字符串第一个为0-9的数值则返回true
     *
     * @param param
     * @return
     */
    public static boolean parseNumber(String param) {
        if (param.equals("") || param == null)
            return false;
        switch (param.charAt(0)) {
            case 48: // '0'
            case 49: // '1'
            case 50: // '2'
            case 51: // '3'
            case 52: // '4'
            case 53: // '5'
            case 54: // '6'
            case 55: // '7'
            case 56: // '8'
            case 57: // '9'
                return true;
        }
        return false;
    }

    /**
     * 格式化生日
     *
     * @param birthday
     * @return String 例如：'1975-10-10'
     */
    public static String formatBirthday(String birthday) {
        String result = "";
        String yy = "";
        String mm = "";
        String dd = "";
        try {
            yy = birthday.substring(0, 4);
            mm = birthday.substring(4, 6);
            dd = birthday.substring(6, 8);
            result = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String
                    .valueOf(yy)))).append("-").append(mm).append("-").append(dd)));
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 取生日的年份
     *
     * @param birthday
     * @return
     */
    public static String getBirthYear(String birthday) {
        return birthday.substring(0, 4);
    }

    /**
     * 取生日月份
     *
     * @param birthday
     * @return
     */
    public static String getBirthMonth(String birthday) {
        return birthday.substring(4, 6);
    }

    /**
     * 取生日日期
     *
     * @param birthday
     * @return
     */
    public static String getBirthDay(String birthday) {
        return birthday.substring(6, 8);
    }

    /**
     * 取xml文件生成Document
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static Document getDocument(String filePath) throws Exception {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new File(filePath));
        return doc;
    }

//    /**
//     * 访问CGI，返回应答的xml串，转换为Document对象
//     * @param svrUrl 服务URL
//     * @param strXML 请求的xml串
//     * @return Document
//     * @throws Exception
//     */
//	public static Document callCGI(String svrUrl, String strXML) throws Exception {
//		URL url;
//		try {
//			url = new URL(svrUrl);
//		} catch (MalformedURLException ex) {
//			throw new Exception(ex);
//		}
//		try {
//			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
//			httpUrlConn.setRequestMethod("POST");
//			httpUrlConn.setDoInput(true);
//			if (strXML != null && !strXML.equals("")) {
//				httpUrlConn.setDoOutput(true);
//				OutputStream sendStream = null;
//				try {
//					sendStream = httpUrlConn.getOutputStream();
//					sendStream.write(strXML.getBytes());
//					sendStream.flush();
//					sendStream.close();
//				} finally {
//					if (sendStream != null)
//						try {
//							sendStream.close();
//						} catch (Exception exception1) {
//						}
//				}
//			}
//			int respCode = httpUrlConn.getResponseCode();
//			if (respCode == 200)
//				try {
//					InputStream is = httpUrlConn.getInputStream();
//					Document document = XMLUtil.buildDocument(is);
//					return document;
//				} catch (Exception ex) {
//					throw new Exception("parse xml error. ".concat(String.valueOf(String
//							.valueOf(ex))));
//				}
//			else
//				throw new Exception(String.valueOf(String.valueOf((new StringBuffer(
//						"HTTP Server Error, ")).append(respCode).append(": ").append(
//						httpUrlConn.getResponseMessage()))));
//		} catch (IOException ex) {
//			throw new Exception(ex);
//		}
//	}

    /**
     * 将源字符串中的特殊字符(如',"等)替换为符合内容html输出的相应符号
     *
     * @param sSourceString String 源字符串
     * @return String 返回符合html内容输出的字符串
     */
    public static String htmlEscape(String sSourceString) {
        if (sSourceString == null)
            return "";

        String sDestString = sSourceString;

        try {
            sDestString = replace(sDestString, "\\", "\\\\");
            sDestString = replace(sDestString, "\"", "\\\"");
            sDestString = replace(sDestString, "\'", "\\\'");
            sDestString = replace(sDestString, "\r", "\\r");
            sDestString = replace(sDestString, "\n", "\\n");
            sDestString = replace(sDestString, "<", "&lt;");
            sDestString = replace(sDestString, ">", "&gt;");
            sDestString = replace(sDestString, "<br>", "\\r\\n");
        } catch (Exception e) {
            return sSourceString;
        }

        return sDestString;
    }

    /**
     * 提供替换字符串的功能,区分大小写
     *
     * @param sSrcStr 为要被处理的字符串
     * @param sStr    为被替换的子字符串
     * @param sRepStr 为替换进去的子字符串
     * @return String 替换后的字符串,出现异常返回原字符串
     */
    public static String replace(String sSrcStr, String sStr, String sRepStr) {
        try {
            if ((sSrcStr == null) || (sStr == null) || (sRepStr == null))
                return sSrcStr;

            if ((sSrcStr.length() == 0) || (sStr.length() == 0))
                return sSrcStr;

            StringBuffer sb = new StringBuffer(sSrcStr);
            replace(sb, sStr, sRepStr);
            return new String(sb);
        } catch (Exception e) {
            return sSrcStr;
        }
    }

    /**
     * 提供替换字符串的功能,区分大小写
     *
     * @param sb      为要被处理的StringBuffer
     * @param sStr    为被替换的子字符串
     * @param sRepStr 为替换进去的子字符串
     * @return 无
     */
    public static void replace(StringBuffer sb, String sStr, String sRepStr) {
        try {
            if ((sb == null) || (sStr == null) || (sRepStr == null))
                return;

            if ((sb.length() == 0) || (sStr.length() == 0))
                return;

            int iStartIndex = 0;
            int iLen = sb.length();
            // int iEndIndex = 0;
            int iLen2 = sStr.length();

            while (iStartIndex < iLen) {
                if (sb.substring(iStartIndex, iLen2 + iStartIndex).equals(sStr)) {
                    sb.replace(iStartIndex, iLen2 + iStartIndex, sRepStr);
                    iLen = sb.length();
                    iStartIndex = iStartIndex + sRepStr.length();
                } else
                    iStartIndex++;
            }
        } catch (Exception e) {
        }
    }

    /**
     * funcion turnStr purpose 把null转成空格
     *
     * @param s 传的的串
     * @return String
     */
    // V1.0.1
    public static String turnStr(String s) {
        if (s == null)
            return "";

        return s.trim();
    }


    /**
     * 获取类下所有方法名称
     *
     * @param clazz
     * @return
     */
    public static List getClassMethodName(Class clazz) {
        List<String> methodList = new ArrayList<String>();

        Method[] methods = clazz.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method m = (Method) methods[i];
            methodList.add(m.getName());
        }
        return methodList;

    }

    /**
     * 将String转换为int
     *
     * @param str
     * @return
     */
    public static int str2Int(String str) {
        int k = 0;
        try {
            if (str != null && str.trim().length() > 0) {
                k = Integer.parseInt(str);
            }
        } catch (Exception e) {
        }
        return k;
    }

    /**
     * 将String转换为double
     *
     * @param str
     * @return
     */
    public static double str2double(String str) {
        double d = 0;
        try {
            if (str != null && str.trim().length() > 0) {
                d = Double.parseDouble(str);
            }
        } catch (Exception e) {
        }
        return d;
    }

    /**
     * 将int转换维string
     *
     * @param i
     * @return
     */
    public static String getStr(int i) {
        try {
            String str = String.valueOf(i);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将long转换维string
     *
     * @param l
     * @return
     */
    public static String getStr(long l) {
        try {
            String str = String.valueOf(l);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将double转换维string
     *
     * @param d
     * @return
     */
    public static String getStr(double d) {
        try {
            String str = String.valueOf(d);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 规范浮点字符串的显示格式
     *
     * @param d
     * @param format
     * @return
     */
    public static String getStr(double d, String format) {
        try {
            String str = "";
            if (format != null && format.trim().length() > 0) {
                DecimalFormat myFormatter = new DecimalFormat(format);
                str = myFormatter.format(d);

            } else {
                str = String.valueOf(d);
            }
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param str
     * @param def
     * @return
     */
    public static String getStr(String str, String def) {
        if (str == null || str.trim().length() == 0) {
            if (def != null && def.trim().length() > 0) {
                str = def;
            } else {
                str = "";
            }
        }

        return str;
    }

    /**
     * 将String转换维long
     *
     * @param str
     * @return
     */
    public static long str2long(String str) {
        long l = 0;
        try {
            if (str != null && str.trim().length() > 0) {
                l = Long.parseLong(str);
            }
        } catch (Exception e) {
        }
        return l;
    }

    /**
     * 判断对象是否为空！(null,"", "null")
     *
     * @param obj
     * @return
     */
    public static boolean isBlank(String obj) {
        if (obj == null || "".equals(obj.trim()) || nullString.equals(obj)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断对象是否为空！(null,"", "null")
     *
     * @param obj
     * @return
     */
    public static boolean isBlank(Object obj) {
        if (obj == null || "".equals(obj) || nullString.equals(obj)) {
            return true;
        } else {
            return false;
        }
    }

    public static String CRLFToHtml(String content) {
        String s = content;
        s = s.replaceAll("\r\n", "<br/>");
        s = s.replaceAll("\t", "　　");
        return s;
    }

    /**
     * 数据格式化，保留2位小数
     *
     * @param value
     * @return
     */
    public static String format(Number value) {
        return format(value, 2);
    }

    /**
     * 数据格式化
     *
     * @param value
     * @param decimal-小数位数
     * @return
     */
    public static String format(Number value, int decimal) {
        String len = "";
        for (int i = 0; i < decimal; i++)
            len += "0";

        if (value == null && decimal == 0)
            return "0";
        if (value == null && decimal > 0)
            return "0." + len;

        DecimalFormat format = new DecimalFormat("##,###,###,###,##0" + len);
        return format.format(value);
    }

    /**
     * 字符串转化为整形
     *
     * @param value
     * @return
     */
    public static int stringToInt(String value) {
        return stringToInt(value, 0);
    }

    /**
     * 字符串转化为整形
     *
     * @param value
     * @param def   默认值
     * @return
     */
    public static int stringToInt(String value, int def) {
        if (isBlank(value) || !isNumeric(value))
            return def;
        return Integer.parseInt(value);
    }

    /**
     * java的escape编码
     *
     * @param src
     * @return
     */
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    /**
     * java的unescape解码
     *
     * @param src
     * @return
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 字符串压缩（对于网络流有问题）
     *
     * @param sourcecode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String compressString(String sourcecode) throws UnsupportedEncodingException {
        ByteArrayOutputStream encodeout = new ByteArrayOutputStream();
        try {
            //    待压缩的字符 
            ByteArrayInputStream bin = new ByteArrayInputStream(sourcecode.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(bin));
            //    保存压缩后的字符             
            BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(encodeout));
            //    开始compress 
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            in.close();
            bin.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeout.toString();//(压缩后的内容转成String)

    }

    /**
     * 字符串压缩（对于网络流有问题）
     *
     * @param sourcecode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static OutputStream compress(String sourcecode) throws UnsupportedEncodingException {
        ByteArrayOutputStream encodeout = new ByteArrayOutputStream();
        try {
            //    待压缩的字符 
            ByteArrayInputStream bin = new ByteArrayInputStream(sourcecode.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(bin));
            //    保存压缩后的字符             
            BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(encodeout));
            //    开始compress 
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            in.close();
            bin.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeout;//(压缩后的内容转成String)

    }

    /**
     * 解压字符串（对于网络流有问题）
     *
     * @param encode
     * @return
     */
    public static String uncompressString(String encode) {
        String decode = "";
        try {
            //    开始uncompress 
            ByteArrayInputStream bain = new ByteArrayInputStream(encode.getBytes());
            BufferedReader bin = new BufferedReader(new InputStreamReader(new GZIPInputStream(bain)));

            String tmpStr = null;
            while ((tmpStr = bin.readLine()) != null) {
                decode = decode + tmpStr;
                tmpStr = null;
            }
            bin.close();
            bain.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode;
    }

    /**
     * 解压字符串（对于网络流有问题）
     *
     * @param encodeStream
     * @return
     */
    public static String uncompress(InputStream encodeStream) {
        String decode = "";
        try {
            //    开始uncompress 
            //ByteArrayInputStream bain = new ByteArrayInputStream(encode);
            BufferedReader bin = new BufferedReader(new InputStreamReader(new GZIPInputStream(encodeStream)));

            String tmpStr = null;
            while ((tmpStr = bin.readLine()) != null) {
                decode = decode + tmpStr;
                tmpStr = null;
            }
            bin.close();
            encodeStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decode;
    }

    /**
     * 解压缩字节（可用）
     *
     * @param zipBytes
     * @return
     * @throws IOException
     */
    public static byte[] unzip(byte[] zipBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
        //String entryName = new String("servletservice");
        ZipInputStream zis = new ZipInputStream(bais);
        zis.getNextEntry();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int BUFSIZ = 4096;
        byte inbuf[] = new byte[BUFSIZ];
        int n;
        while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
            baos.write(inbuf, 0, n);
        }
        byte[] data = baos.toByteArray();
        zis.close();
        return data;
    }

    /**
     * 压缩字节(可用)
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] zip(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipEntry ze = new ZipEntry("servletservice");
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(ze);
        zos.write(data, 0, data.length);
        zos.close();
        byte[] zipBytes = baos.toByteArray();
        return zipBytes;
    }

    public static String sqlStr(String sql) {
        String ret = StringUtils.replaceChars(sql, "'", "''''");
        return ret;
    }

//    /**
//     * js页面传递中文参数到jsp的转码
//     * @param param
//     * @return
//     */
//    public static String jsToJspEncode(String param){
//        if(param==null){
//            param = "";
//        }else{
//            try {
//                if(ParameterConfig.getValue("IS_ENCODE").equals("true")){
//                    param = new String(param.getBytes("ISO8859-1"));
//                }
//            } catch (UnsupportedEncodingException e) {
//                System.out.println("转码失败");
//                e.printStackTrace();
//            }
//        }
//        return param;
//    }

    /**
     * 构造维度字符串
     *
     * @param sb
     * @param key
     * @param value
     * @return
     */
    public static String appendDimStr(String sb, String key, String value) {
        if (sb == null || sb.equals("")) {
            return key + "=" + value;
        } else {
            return sb + "0x09" + key + "=" + value;
        }
    }

    /**
     * 数组转列表
     *
     * @param arr
     * @return
     */
    public static List arrToList(Object[] arr) {
        List arrList = new ArrayList();
        for (int i = 0; i < arr.length; i++) {
            arrList.add(arr[i]);
        }
        return arrList;
    }

    /**
     * 对字符串做操作系统的编码处理
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String systemEncode(String str) {
        FTPClient ftpClient = new FTPClient();
        try {
            str = new String(str.getBytes(), ftpClient.getControlEncoding());
        } catch (UnsupportedEncodingException e) {
            System.out.println("!!!!!!!!!!!!!!!!编码失败");
            e.printStackTrace();
        }
        return str;
    }
}
