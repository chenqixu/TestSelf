package com.cqx.common.utils.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 流工具
 *
 * @author chenqixu
 */
public class IOUtil {

    private String fileName;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;
    private String readLineCache;
    private byte[] read1Cache;
    private List<byte[]> read1CacheList = new ArrayList<>();

    public IOUtil(String fileName) {
        this.fileName = fileName;
    }

    public void newWrite() throws FileNotFoundException {
        this.fileOutputStream = new FileOutputStream(fileName);
    }

    public void newRead() throws FileNotFoundException {
        this.fileInputStream = new FileInputStream(fileName);
    }

    private void checkWrite() {
        if (fileOutputStream == null) throw new NullPointerException(fileName + " 初始化失败！");
    }

    private void checkRead() {
        if (fileInputStream == null) throw new NullPointerException(fileName + " 初始化失败！");
    }

    public void write(byte[] b) throws IOException {
        checkWrite();
        fileOutputStream.write(b);
    }

    public void write(int b) throws IOException {
        checkWrite();
        fileOutputStream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        checkWrite();
        fileOutputStream.write(b, off, len);
    }

    public void flush() throws IOException {
        checkWrite();
        fileOutputStream.flush();
    }

    public void closeWrite() throws IOException {
        checkWrite();
        fileOutputStream.close();
    }

    public int read() throws IOException {
        checkRead();
        return fileInputStream.read();
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        checkRead();
        return fileInputStream.read(b, off, len);
    }

    private String read0() throws IOException {
        int r;
        int len = 3;
        byte[] buffer = new byte[len];
        int blen = 0;
        while ((r = read()) >= 0) {
            if (r < 128) {//ascii 0-127
                return String.valueOf((char) r);
            } else {//中文3位
                buffer[blen] = (byte) r;
                blen++;
                if (blen == 3) {
                    return new String(buffer);
                }
            }
        }
        return null;
    }

    public String readLine(int buffer_len) throws IOException {
        return read1(buffer_len);
    }

    private String read1(int buffer_len) throws IOException {
//        int buffer_len = 8192;
        byte[] b = new byte[buffer_len];
        int r;
        if (read1Cache != null && read1Cache.length > 0) {//缓存有数据
            String result = readDeal(read1Cache);
            if (result != null) return result;
        }
        while ((r = read(b)) >= 0) {
            if (r < buffer_len) {//内容小于缓存
                byte[] _b = Arrays.copyOf(b, r);
                String result = readDeal(_b);
                if (result != null) return result;
            } else {//内存超出缓存，逐个缓存进行处理
                String result = readDeal(b);
                if (result != null) return result;
            }
        }
        if (read1CacheList.size() > 0) {//既不是回车也没有换行
            byte[] news = copyArrays(null);
            return new String(news);
        }
        return null;
    }

    private byte[] copyArrays(byte[] end) {
        byte[] news = null;
        byte[] first = null;
        if (read1CacheList.size() > 0) {
            for (int i = 0; i < read1CacheList.size(); i++) {
                byte[] currents = read1CacheList.get(i);
                //如果currents最后一位是13或10
                if (currents[currents.length - 1] == 13) {
                    currents = Arrays.copyOf(currents, currents.length - 1);
                }
                if (i == 0) {
                    first = currents;
                    news = first;
                } else {
                    news = Arrays.copyOf(first, first.length + currents.length);
                    System.arraycopy(currents, 0, news, first.length, currents.length);
                    first = news;
                }
            }
            read1CacheList.clear();
        }
        if (end != null) {//最后把news和end做合并
            if (news != null) {
                news = Arrays.copyOf(first, first.length + end.length);
                System.arraycopy(end, 0, news, first.length, end.length);
            } else {
                news = end;
            }
        }
        return news;
    }

    private String readDeal(byte[] _b) {
        for (int i = 0; i < _b.length; i++) {
            if (_b[i] == 13) {//回车
                //需要多往下读一个字符，如果不是换行就结束
                if ((i + 1) < _b.length) {
                    if (_b[i + 1] == 10) {//换行，结束
                        //缓存数组，下次读取先读缓存
                        read1Cache = Arrays.copyOfRange(_b, i + 2, _b.length);
                        return new String(copyArrays(Arrays.copyOf(_b, i)));
                    } else {//非换行，也结束
                        read1Cache = Arrays.copyOfRange(_b, i + 1, _b.length);
                        return new String(copyArrays(Arrays.copyOf(_b, i)));
                    }
                } else {//无法往下继续读取
                    //缺陷1，需要清除缓存
                    read1Cache = null;
                    //缺陷2，这个刚好卡在13，下面还有内容，结果返回了
//                    return new String(copyArrays(_b));
                    read1CacheList.add(Arrays.copyOf(_b, _b.length));
                    return null;
                }
            } else if (_b[i] == 10) {//换行
                read1Cache = Arrays.copyOfRange(_b, i + 1, _b.length);
                return new String(copyArrays(Arrays.copyOf(_b, i)));
            }
        }
        //既不是回车也没有换行，需要加入到另外的缓存中
        read1Cache = null;
        read1CacheList.add(Arrays.copyOf(_b, _b.length));
        return null;
    }

    public String readLine() throws IOException {
        // \r    13  回车
        // \n   10  换行
        // \r\n 13 10  回车换行
        StringBuilder sb = new StringBuilder();
        String str;
        boolean isR = false;
        if (readLineCache != null) {//从多读取的缓存中恢复
            sb.append(readLineCache);
            readLineCache = null;//清空缓存
        }
        while ((str = read0()) != null) {
            byte[] bs = str.getBytes();
            if (bs.length == 1) {
                if (bs[0] == 13) {//回车
                    //需要多往下读一个字符，如果不是换行就结束
                    isR = true;
                    continue;
                } else if (bs[0] == 10) {//换行
                    break;
                }
            }
            if (isR) {//回车后没有遇到换行，需要换行
                readLineCache = str;//缓存多读取的字符
                break;
            }
            sb.append(str);
        }
        if (sb.length() == 0) return null;
        else return sb.toString();
    }

    public void closeRed() throws IOException {
        checkRead();
        fileInputStream.close();
    }

}
