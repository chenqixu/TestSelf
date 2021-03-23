package com.cqx.common.utils.string;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * StringUtilsEx
 *
 * @author chenqixu
 */
public class StringUtilsEx {
    private static final Logger logger = LoggerFactory.getLogger(StringUtilsEx.class);

    public static String toEncodedString(byte[] bytes, Charset charset) {
        return bytes == null ? "" : new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

    public static String toEncodedString(byte[] bytes) {
        return toEncodedString(bytes, (Charset) null);
    }

    public static String[] fastSplit(String line, char delimiter) {
        String[] temp = new String[line.length() + 1];
        int colCount = 0;
        int i = 0;

        for (int j = line.indexOf(delimiter); j >= 0; j = line.indexOf(delimiter, i)) {
            temp[colCount++] = line.substring(i, j);
            i = j + 1;
        }

        temp[colCount++] = line.substring(i);
        String[] result = new String[colCount];
        System.arraycopy(temp, 0, result, 0, colCount);
        return result;
    }

    public static String[] fastSplit(String line, String delimiter) {
        int delimiterLength = delimiter.length();
        String[] temp = new String[line.length() + 1];
        int colCount = 0;
        int i = 0;

        for (int j = line.indexOf(delimiter); j >= 0; j = line.indexOf(delimiter, i)) {
            temp[colCount++] = line.substring(i, j);
            i = j + delimiterLength;
        }

        temp[colCount++] = line.substring(i);
        String[] result = new String[colCount];
        System.arraycopy(temp, 0, result, 0, colCount);
        return result;
    }

    public static String[] fastSplit(String line, String delimiter, int colCount) {
        int delimiterLength = delimiter.length();
        return fastSplit(line, delimiter, delimiterLength, colCount);
    }

    public static String[] fastSplit(String line, String delimiter, int delimiterLength, int colCount) {
        String[] result = new String[colCount];
        int iCount = 0;
        int i = 0;

        int j;
        for (j = line.indexOf(delimiter); j >= 0 && iCount < colCount - 1; j = line.indexOf(delimiter, i)) {
            result[iCount++] = line.substring(i, j);
            i = j + delimiterLength;
        }

        j = line.indexOf(delimiter, i);
        if (j == -1) {
            result[iCount] = StringUtils.substring(line, i);
        } else {
            result[iCount] = StringUtils.substring(line, i, j);
        }

        return result;
    }

    public static ArrayList<String> getStepVars(String str, int step) {
        ArrayList<String> vars = new ArrayList();
        if (str == null) {
            return vars;
        } else {
            for (int j = 1; j < step; ++j) {
                if (str.lastIndexOf("S:" + j + " ") >= 0) {
                    vars.add("S_" + j);
                }
            }

            return vars;
        }
    }

    public static String join(List list, String delimiter) {
        if (list != null && list.size() >= 1) {
            StringBuilder buf = new StringBuilder();
            Iterator i = list.iterator();

            while (i.hasNext()) {
                buf.append((String) i.next());
                if (i.hasNext()) {
                    buf.append(delimiter);
                }
            }

            return buf.toString();
        } else {
            return null;
        }
    }

    public static List<String> split(String str, int delimiter) {
        List<String> splitList = null;
        if (str == null) {
            return null;
        } else {
            String L = "";
            int pre = 0;
            int len = str.length();

            for (int i = 0; i < len; ++i) {
                if (str.charAt(i) == delimiter) {
                    L = str.substring(pre, i);
                    if (splitList == null) {
                        splitList = new ArrayList();
                    }

                    splitList.add(L);
                    pre = i + 1;
                }
            }

            if (pre != len) {
                L = str.substring(pre, len);
                splitList.add(L);
            }

            return splitList;
        }
    }

    public static List<String> split(String str, char delimiter) {
        List<String> splitList = null;
        if (str == null) {
            return null;
        } else if (str.equals("")) {
            return null;
        } else {
            String L = "";
            int pre = 0;
            int len = str.length();

            for (int i = 0; i < len; ++i) {
                if (str.charAt(i) == delimiter) {
                    L = str.substring(pre, i);
                    if (splitList == null) {
                        splitList = new ArrayList();
                    }

                    splitList.add(L);
                    pre = i + 1;
                }
            }

            L = str.substring(pre, len);
            if (splitList == null) {
                splitList = new ArrayList();
            }

            splitList.add(L);
            return splitList;
        }
    }

    public static List<String> split(String str, String delimiter) {
        if (delimiter.length() == 1) {
            return split(str, delimiter.charAt(0));
        } else {
            List<String> splitList = null;
            StringTokenizer st = null;
            if (str == null) {
                return null;
            } else {
                st = new StringTokenizer(str, delimiter);
                if (st.hasMoreTokens()) {
                    splitList = new ArrayList();

                    while (st.hasMoreTokens()) {
                        splitList.add(st.nextToken());
                    }
                }

                return splitList;
            }
        }
    }

    public static String replace(String mainString, String oldString, String newString) {
        if (mainString == null) {
            return null;
        } else {
            int i = mainString.lastIndexOf(oldString);
            if (i < 0) {
                return mainString;
            } else {
                StringBuilder mainSb;
                for (mainSb = new StringBuilder(mainString); i >= 0; i = mainString.lastIndexOf(oldString, i - 1)) {
                    mainSb.replace(i, i + oldString.length(), newString);
                }

                return mainSb.toString();
            }
        }
    }

    public static String replace(String mainString, int oldString, String newString) {
        if (mainString == null) {
            return null;
        } else {
            int i = mainString.lastIndexOf(oldString);
            if (i < 0) {
                return mainString;
            } else {
                StringBuilder mainSb;
                for (mainSb = new StringBuilder(mainString); i >= 0; i = mainString.lastIndexOf(oldString, i - 1)) {
                    mainSb.replace(i, i + 1, newString);
                }

                return mainSb.toString();
            }
        }
    }

    public static boolean nullOrBlank(String param) {
        return param == null || param.trim().equals("");
    }

    public static int parseInt(String param) {
        boolean var1 = false;

        int i;
        try {
            i = Integer.parseInt(param);
        } catch (Exception var3) {
            i = (int) parseFloat(param);
        }

        return i;
    }

    public static long parseLong(String param) {
        long l = 0L;

        try {
            l = Long.parseLong(param);
        } catch (Exception var4) {
            l = (long) parseDouble(param);
        }

        return l;
    }

    public static float parseFloat(String param) {
        float f = 0.0F;

        try {
            f = Float.parseFloat(param);
        } catch (Exception var3) {
        }

        return f;
    }

    public static double parseDouble(String param) {
        double d = 0.0D;

        try {
            d = Double.parseDouble(param);
        } catch (Exception var4) {
        }

        return d;
    }

    public static boolean parseBoolean(String param) {
        if (nullOrBlank(param)) {
            return false;
        } else {
            switch (param.charAt(0)) {
                case '1':
                case 'T':
                case 'Y':
                case 't':
                case 'y':
                    return true;
                default:
                    return false;
            }
        }
    }

    public static String convertGb2312(String input) {
        String str = input;

        try {
            str = new String(input.getBytes("ISO8859_1"), "gb2312");
        } catch (Exception var3) {
        }

        return str;
    }

    public static String isInteger(String input) {
        String ret = "1";

        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) < '0' || input.charAt(i) > '9') {
                ret = "0";
                break;
            }
        }

        return ret;
    }

    public static String Md5(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException var8) {
            System.out.println("NoSuchAlgorithmException caught!");
        } catch (UnsupportedEncodingException var9) {
            var9.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        byte[] var4 = byteArray;
        int var5 = byteArray.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            byte aByteArray = var4[var6];
            if (Integer.toHexString(255 & aByteArray).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & aByteArray));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & aByteArray));
            }
        }

        return md5StrBuff.toString().toUpperCase();
    }

    public static String replacePattern(String source, String regex, String replacement) {
        return Pattern.compile(regex, 32).matcher(source).replaceAll(replacement);
    }
}
