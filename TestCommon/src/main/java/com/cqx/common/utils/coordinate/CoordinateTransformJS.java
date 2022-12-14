package com.cqx.common.utils.coordinate;

import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * 使用javascript脚本进行坐标系转换
 * <pre>
 *     只能执行javascript的标准库 ，有很多默认对象不支持
 * </pre>
 *
 * @author chenqixu
 */
public class CoordinateTransformJS {
    private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformJS.class);
    private ScriptEngineManager factory;
    private ScriptEngine engine;
    private Invocable inv;
    private Object gcoord;

    public CoordinateTransformJS(String scriptPath) throws ScriptException, FileNotFoundException {
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
            // 获取对象
            gcoord = engine.get("gcoord");
        }
    }

    /**
     * WGS84 坐标 转 GCJ02
     *
     * @param lng 经度
     * @param lat 纬度
     * @return GCJ02 坐标：[经度，纬度]
     */
    public double[] transformWGS84ToGCJ02(double lng, double lat) throws ScriptException, NoSuchMethodException {
        if (gcoord != null) {
            double[] vals = {lng, lat};
            Object result = inv.invokeMethod(gcoord, "transform", vals, "WGS84", "GCJ02");
            logger.info("result={}, Class={}", result, result.getClass().getName());
        }
        return new double[]{};
    }
}
