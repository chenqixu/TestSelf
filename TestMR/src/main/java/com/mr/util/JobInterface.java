package com.mr.util;

import java.io.IOException;

/**
 * JobInterface
 *
 * @author chenqixu
 */
public interface JobInterface {
    JobInterface setLocalMode();

    JobInterface setDBInputFormat(Class cls, String tableName, String conditions, String orderBy, String... fieldNames);

    JobInterface setMapper(Class mapperClass, Class outKeyClass, Class outvalueClass);

    JobInterface setReducer(Class reducerClass, Class outKeyClass, Class outvalueClass);

    JobInterface setNullReducer(Class outKeyClass, Class outvalueClass);

    JobInterface deleteAndSetOutPutPath(String outputPath, Class outputFormatClass) throws IOException;

    boolean waitForCompletion() throws InterruptedException, IOException, ClassNotFoundException;
}
