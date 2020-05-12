package com.cqx.common.utils.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * RAFFileMangerCenter
 *
 * @author chenqixu
 */
public class RAFFileMangerCenter {

    public static final String END_TAG = "#RAFFileMangerCenter#END#";
    private static final Logger logger = LoggerFactory.getLogger(RAFFileMangerCenter.class);
    private int header_len = 20;
    private int header_half_len = header_len / 2;
    private int header_pos_next = 0;
    private byte[] null_byte = new byte[header_len];
    private String NULL_VALUE = new String(null_byte);
    private MyRandomAccessFile myRandomAccessFile;

    public RAFFileMangerCenter(String file_name) throws FileNotFoundException {
        myRandomAccessFile = new MyRandomAccessFile(file_name);
        myRandomAccessFile.setLock(true);
    }

    public void write(String msg) {
        int header_pos = header_pos_next;
        String header = fillZero(header_pos + header_len, header_half_len)
                + fillZero(msg.getBytes().length, header_half_len);
        try {
            myRandomAccessFile.write(header_pos, (header + msg).getBytes());
            header_pos_next = header_pos + header_len + msg.getBytes().length;
            logger.info("【write】msg：{}，header：{}，header_pos：{}，header_pos_next：{}", msg, header, header_pos, header_pos_next);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String read() {
        int header_pos = header_pos_next;
        String msg = null;
        //先读一个块
        try {
            String header = myRandomAccessFile.read(header_pos, header_len);
            if (NULL_VALUE.equals(header)) {
                return null;
            }
            //把header分为2部分
            String pos = header.substring(0, header_half_len);
            String len = header.substring(header_half_len, header_len);
            msg = myRandomAccessFile.read(Long.valueOf(pos), Integer.valueOf(len));
            header_pos_next = header_pos + header_len + Integer.valueOf(len);
            logger.info("【read】header：{}，pos：{}，len：{}，msg：{}，header_pos：{}，header_pos_next：{}", header, pos, len, msg, header_pos, header_pos_next);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return msg;
    }

    public void close() throws IOException {
        if (myRandomAccessFile != null) myRandomAccessFile.close();
    }

    /**
     * 补0
     *
     * @param value 需要补0的字符串
     * @param len   总位数
     * @return
     */
    private String fillZero(int value, int len) {
        String result = String.valueOf(value);
        if (result.length() < len) {
            int surplus = len - result.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < surplus; i++) {
                sb.append("0");
            }
            sb.append(result);
            return sb.toString();
        }
        return result;
    }

    public int getHeader_pos() {
        return header_pos_next;
    }

    public void setHeader_pos(int header_pos) {
        this.header_pos_next = header_pos;
    }
}
