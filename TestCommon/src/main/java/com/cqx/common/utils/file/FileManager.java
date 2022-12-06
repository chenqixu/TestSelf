package com.cqx.common.utils.file;

import java.util.List;

/**
 * FileManager
 *
 * @author chenqixu
 */
public class FileManager {
    private static FileUtil fileUtil = new FileUtil();

    /**
     * 读取文件首行，如果文件存在
     *
     * @param fileName
     * @param code
     * @return
     */
    public static String readFileHeader(String fileName, String code) {
        // 先判断文件是否存在
        if (FileUtil.isFile(fileName)) {
            List<String> contents = fileUtil.read(fileName, code);
            if (contents.size() > 0) return contents.get(0);
        }
        return null;
    }
}
