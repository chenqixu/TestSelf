package com.cqx.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {

    private Map<String, String> map = new HashMap<>();

    public PropertyUtil(String path) {
        File f = null;
        FileInputStream pInStream = null;
        Properties p = null;
        try {
            f = new File(path);
            pInStream = new FileInputStream(f);
            p = new Properties();
            p.load(pInStream);
            Enumeration<?> enuVersion = p.propertyNames();
            while (enuVersion.hasMoreElements()) {
                enuVersion.nextElement();
            }
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue().toString());
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
    }

    public String getProperty(String name) {
        return map.get(name);
    }
}
