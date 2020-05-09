package com.cqx.common.utils.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理中心
 * <pre>
 *     打开一个文件
 *     写请求
 *     读请求
 * </pre>
 *
 * @author chenqixu
 */
public class FileMangerCenter {

    private final int flushNum = 1000;
    private int now_flushNum = 0;
    private String fileName;
    private BufferedWriter writer;
    private BufferedReader reader;

    public FileMangerCenter(String fileName) {
        this.fileName = fileName;
    }

    public void init() throws IOException {
        File file = new File(fileName);
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    public void initWriter() throws IOException {
        File file = new File(fileName);
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
    }

    public void initReader() throws IOException {
        File file = new File(fileName);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    public void write(String msg) throws IOException {
        write(msg, false);
    }

    public void write(String msg, boolean forceFlush) throws IOException {
        writer.write(msg + "\r\n");
        now_flushNum++;
        if (now_flushNum == flushNum || forceFlush) {
            writer.flush();
            now_flushNum = 0;
        }
    }

    public void setReaderLine(int lineNumber) throws IOException {
        int cnt = 1;
        while (reader.readLine() != null && lineNumber > 0 && cnt < lineNumber) {
            cnt++;
        }
    }

    public List<String> read() throws IOException {
        List<String> msg = new ArrayList<>();
        String tmp;
        while ((tmp = reader.readLine()) != null) {
            msg.add(tmp);
        }
        return msg;
    }

    public void close() throws IOException {
        if (writer != null) writer.close();
        if (reader != null) reader.close();
    }
}
