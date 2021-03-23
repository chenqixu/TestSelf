package com.cqx.common.utils.file;

import java.util.List;

/**
 * FileManager
 *
 * @author chenqixu
 */
public class FileManager {
    private static FileUtil fileUtil = new FileUtil();

    public static String readFileHeader(String fileName, String code) {
        List<String> contents = fileUtil.read(fileName, code);
        if (contents.size() > 0) return contents.get(0);
        return null;
    }
}
