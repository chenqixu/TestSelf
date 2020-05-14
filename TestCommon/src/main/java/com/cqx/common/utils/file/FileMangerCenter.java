package com.cqx.common.utils.file;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

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

    private static final MyLogger logger = MyLoggerFactory.getLogger(FileMangerCenter.class);
    private final int flushNum = 1000;
    private int now_flushNum = 0;
    private String fileName;
    private BufferedWriter writer;
    private BufferedReader reader;
    private String lineSplit = "\n";

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

    public void writeSingle(String msg) throws IOException {
        write(msg, "", false);
    }

    public void write(String msg) throws IOException {
        write(msg, false);
    }

    public void write(String msg, boolean forceFlush) throws IOException {
        write(msg, "\r\n", forceFlush);
    }

    public void write(String msg, String lineSplit, boolean forceFlush) throws IOException {
        writer.write(msg + lineSplit);
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

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public String readByte() throws IOException {
        int bufNum = 1024;
        //读到缓存区，遇到换行符就换行，得判断前一个是否是回车，回车就不换行
        char[] cache = new char[bufNum];
        int result;
        int num = 0;
        boolean flag = false;
        while ((result = reader.read()) != -1) {
            flag = true;
//            logger.debug("read num：{}，int：{}", num, result);
            if (result == 10) {//10换行、13回车、1310回车换行
                //判断上一个是否回车
                if (num > 0 && cache[num - 1] == 13) {//不换行
                    cache[num] = (char) result;
                } else {//换行
                    break;
                }
            } else {
                cache[num] = (char) result;
            }
            num++;
            int now_length = cache.length;
            if (num + 10 > now_length) {//扩容缓存区
                char[] cp_cache = new char[cache.length + bufNum];
                System.arraycopy(cache, 0, cp_cache, 0, num);
                cache = cp_cache;
//                logger.debug("扩容缓存区，当前大小：{}，扩容后大小：{}", now_length, cp_cache.length);
            }
        }
        if (flag) {
            char[] new_cache = new char[num];
            System.arraycopy(cache, 0, new_cache, 0, num);
            return String.valueOf(new_cache);
        } else return null;
    }

    public void close() throws IOException {
        if (writer != null) writer.close();
        if (reader != null) reader.close();
    }
}
