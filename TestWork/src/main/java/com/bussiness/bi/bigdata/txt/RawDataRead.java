package com.bussiness.bi.bigdata.txt;

import java.io.FileInputStream;
import java.math.BigInteger;

/**
 * RawDataRead
 *
 * @author chenqixu
 */
public class RawDataRead {

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String fileName = args[0];
            try (FileInputStream fis = new FileInputStream(fileName)) {
                byte[] len = new byte[2];
                // 先读长度，无符号整形
                int readlen = fis.read(len);
                int _len = new BigInteger(len).intValue();
                System.out.println(String.format("readlen: %s, _len: %s", readlen, _len));
                byte[] filenames = new byte[_len];
                // 读文件名
                readlen = fis.read(filenames);
                String filename_ = new String(filenames, "ISO_8859_1");
                filename_ = filename_.trim();
                System.out.println(String.format("readlen: %s, filename_: %s", readlen, filename_));
            }
        } else {
            System.err.println("请输入文件名！");
            System.exit(-1);
        }
    }
}
