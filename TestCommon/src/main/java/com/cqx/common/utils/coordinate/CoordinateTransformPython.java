package com.cqx.common.utils.coordinate;

import org.python.core.PyFloat;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用python脚本进行坐标系转换，解决java的精度问题
 *
 * @author chenqixu
 */
public class CoordinateTransformPython implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CoordinateTransformPython.class);
    // python的解释器
    private PythonInterpreter interpreter;
    private PyFunction wgs84_to_bd09;
    private PyFunction wgs84_to_gcj02;

    public CoordinateTransformPython(String fileName) {
        interpreter = new PythonInterpreter();
        interpreter.execfile(fileName);
        wgs84_to_bd09 = interpreter.get("wgs84_to_bd09", PyFunction.class);
        wgs84_to_gcj02 = interpreter.get("wgs84_to_gcj02", PyFunction.class);
    }

    public void transformWGS84ToBD09(double lng, double lat) {
        // 调用函数，如果函数需要参数，在Java中必须先将参数转化为对应的“Python类型”
        PyObject pyobj = wgs84_to_bd09.__call__(new PyFloat(lng), new PyFloat(lat));
        logger.info("transformWGS84ToBD09: {}", pyobj);
    }

    public void transformWGS84ToGCJ02(double lng, double lat) {
        // 调用函数，如果函数需要参数，在Java中必须先将参数转化为对应的“Python类型”
        PyObject pyobj = wgs84_to_gcj02.__call__(new PyFloat(lng), new PyFloat(lat));
        logger.info("transformWGS84ToGCJ02: {}", pyobj);
    }

    @Override
    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}
