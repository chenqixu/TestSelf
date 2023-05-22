package com.cqx.common.utils.http;

import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * java script 工具
 *
 * @author chenqixu
 */
public class JavaScriptUtil {
    private static final Logger logger = LoggerFactory.getLogger(JavaScriptUtil.class);
    private ScriptEngineManager factory;
    private ScriptEngine engine;
    private Invocable inv;

    public JavaScriptUtil(String scriptPath) throws ScriptException, FileNotFoundException {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("JavaScript");
        if (!(engine instanceof Invocable)) {
            logger.warn("Invoking methods is not supported.");
        } else {
            for (ScriptEngineFactory available : factory.getEngineFactories()) {
                logger.info("EngineName={}, Names={}", available.getEngineName(), available.getNames());
            }

            inv = (Invocable) engine;
            for (File file : FileUtil.listFilesEndWith(scriptPath, ".js", "jquery")) {
                if (file.exists()) {
                    logger.info("load {}", file.getName());
                    engine.eval(new FileReader(file));
                }
            }
        }
    }

    public JavaScriptUtil(String scriptPath, String fileNameKeyWord) throws ScriptException, FileNotFoundException {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("JavaScript");
        if (!(engine instanceof Invocable)) {
            logger.warn("Invoking methods is not supported.");
        } else {
            for (ScriptEngineFactory available : factory.getEngineFactories()) {
                logger.info("EngineName={}, Names={}", available.getEngineName(), available.getNames());
            }

            inv = (Invocable) engine;
            for (File file : FileUtil.listFilesEndWith(scriptPath, ".js", "jquery")) {
                if (file.exists() && file.getName().contains(fileNameKeyWord)) {
                    logger.info("load {}", file.getName());
                    engine.eval(new FileReader(file));
                }
            }
        }
    }

    public Object exec(String script, String output) throws ScriptException {
        engine.eval(script);
        Object outputVal = engine.get(output);
        logger.info("{}={}", output, outputVal);
        return outputVal;
    }
}
