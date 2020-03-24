package com.cqx.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {
    //    private final static String spltstr = File.separator;
//    private final static String rootPath = System.getProperty("user.dir");
//    private String logpath = rootPath + spltstr + "conf" + spltstr + "log4j.properties";
    private Map<String, String> propertyMap;

    public PropertyUtil(String logpath) {
        propertyMap = new HashMap<>();
        init(logpath);
    }

    /**
     * 获取属性
     *
     * @param name
     * @param defaultsValue
     * @return
     */
    public String getProperty(String name, String defaultsValue) {
        String value = propertyMap.get(name);
        return (value == null || value.equals("")) ? defaultsValue : value;
    }

    /**
     * 初始化加载文件
     *
     * @param logpath
     * @return
     */
    private String init(String logpath) {
        String result = "";
        File f = null;
        FileInputStream pInStream = null;
        Properties p = null;
        try {
            f = new File(logpath);
            pInStream = new FileInputStream(f);
            p = new Properties();
            p.load(pInStream);
            Enumeration<?> enuVersion = p.propertyNames();
            while (enuVersion.hasMoreElements()) {
                enuVersion.nextElement();
            }
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                propertyMap.put(entry.getKey().toString(), entry.getValue().toString());
            }
            f = null;
            pInStream.close();
            p.clear();
            p = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (f != null)
                f = null;
            if (pInStream != null)
                try {
                    pInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (p != null) {
                p.clear();
                p = null;
            }
        }
        return result;
    }
}
