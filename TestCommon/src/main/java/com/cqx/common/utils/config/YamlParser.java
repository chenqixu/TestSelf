package com.cqx.common.utils.config;

import org.yaml.snakeyaml.Yaml;

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
            if (is != null)
                is.close();
        }
        return map;
    }
}
