package com.cqx.common.utils.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * YamlParser
 *
 * @author chenqixu
 */
public class YamlParser {
    private static final String URL_HEADER = "file:///";

    private YamlParser() {
    }

    public static YamlParser builder() {
        return new YamlParser();
    }

    /**
     * 解析yaml文件，转换成Map
     *
     * @param path
     * @return
     * @throws IOException
     */
    public Map parserConfToMap(String path) throws IOException {
        Yaml yaml;
        InputStream is = null;
        Map map;
        try {
            // 加载yaml配置文件
            yaml = new Yaml();
            if (!path.startsWith(URL_HEADER)) path = URL_HEADER + path;
            URL url = new URL(path);
            is = url.openStream();
            map = yaml.loadAs(is, Map.class);
            is.close();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return map;
    }

    /**
     * Map转Yaml文件
     *
     * @param param
     * @param fileName
     * @throws IOException
     */
    public void dump(Map param, String fileName) throws IOException {
        Yaml yaml;
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            yaml = new Yaml();
            yaml.dump(param, fw);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    /**
     * Map转String
     *
     * @param param
     * @return
     */
    public String dump(Map param) {
        return new Yaml().dump(param);
    }
}
