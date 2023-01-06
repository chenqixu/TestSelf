package com.cqx.common.test;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.config.YamlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * TestBase
 *
 * @author chenqixu
 */
public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);

    /**
     * 获取资源的文件路径，前面带file://
     *
     * @param fileName
     * @return
     */
    protected String getResourceClassPath(String fileName) {
        // 优化
        // getClasss()可以供非本工程的继承类使用
        // new Object().getClass()在IDEA运行的时候，在非本工程的继承类中会有问题
//        Object obj = new Object();
        URL url = getClass().getResource("/");
        if (url != null) {
            String path = "file://" + url.getPath() + fileName;
            logger.info("加载到配置文件：{}", path);
            return path;
        } else {
            logger.error("加载不到配置文件：{}", fileName);
            return null;
        }
    }

    /**
     * 获取资源的文件路径
     *
     * @param fileName
     * @return
     */
    protected String getResourcePath(String fileName) {
//        Object obj = new Object();
        String tmpFileName = fileName;
        if (!fileName.startsWith("/")) {
            tmpFileName = "/" + fileName;
        }
        URL url = getClass().getResource(tmpFileName);
        if (url != null) {
            String path;
            if (Utils.isWindow()) {
                path = url.getPath().replaceFirst("/", "");
            } else {
                path = url.getPath();
            }
            logger.info("加载到资源文件：{}", path);
            return path;
        } else {
            logger.error("加载不到资源文件：{}", fileName);
            return null;
        }
    }

    /**
     * 通过资源获取配置参数
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    protected Map getParam(String fileName) throws IOException {
        String confPath = getResourceClassPath(fileName);
        return YamlParser.builder().parserConfToMap(confPath);
    }

    /**
     * 从JVM参数中获取，使用方式：-Dkey=xxx
     *
     * @param key 关键字
     * @return
     */
    protected String getValueFromJVMParam(String key) {
        String value = System.getProperty(key);
        if (value == null || value.length() == 0) {
            throw new NullPointerException(String.format("值为空，请设置-D%s=xxx", key));
        } else {
            return value;
        }
    }
}
