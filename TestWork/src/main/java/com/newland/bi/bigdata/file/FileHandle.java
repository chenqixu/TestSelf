package com.newland.bi.bigdata.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * FileHandle
 *
 * @author chenqixu
 */
public class FileHandle {

    public static final String fileSeparator = System.getProperty("file.separator");
    public static final String filenameSuffix = ".txt";
    private Map<String, File> fileMap;

    private FileHandle() {
        init();
    }

    public static FileHandle builder() {
        return new FileHandle();
    }

    public static void main(String[] args) throws IOException {
        FileHandle fileHandle = FileHandle.builder();
        fileHandle.createFile("d:/tmp/a", 10);
        fileHandle.releaseFile();
    }

    /**
     * 输入目录filepath，创建filecount个文件，打开，保存在Map中
     *
     * @param filepath
     * @param filecount
     * @throws IOException
     */
    public void createFile(String filepath, int filecount) throws IOException {
        for (int i = 0; i < filecount; i++) {
            String filename = filepath + fileSeparator + i + filenameSuffix;
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
                OutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write("123".getBytes());
                } finally {
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                }
            }
            fileMap.put(filename, file);
        }
    }

    /**
     * 释放以及删除创建的文件
     */
    public void releaseFile() {
        Iterator<Map.Entry<String, File>> iterator = fileMap.entrySet().iterator();
        while (iterator.hasNext()) {
            File file = iterator.next().getValue();
            if (file != null) {
                file.deleteOnExit();
            }
        }
    }

    public void init() {
        fileMap = new HashMap<>();
    }
}
