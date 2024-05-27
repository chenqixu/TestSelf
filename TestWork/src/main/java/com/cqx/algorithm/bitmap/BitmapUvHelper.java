package com.cqx.algorithm.bitmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class BitmapUvHelper {

    /**
     * 将bitmap 存到目标文件中
     *
     * @param bitmapUv
     * @param tagetFile
     * @throws Exception
     */
    public static void bitmap2Txt(BitmapUv bitmapUv, File tagetFile) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(tagetFile);) {
            Iterator<Long> msisdns = bitmapUv.iterator();
            long c = 0;
            while (msisdns.hasNext()) {
                fos.write((msisdns.next() + "\n").getBytes(StandardCharsets.UTF_8));
                c++;
                if (c % 50000 == 0) {
                    fos.flush();
                }
            }
        }
    }

    public static void bitmapPrint(BitmapUv bitmapUv) throws Exception {
        Iterator<Long> msisdns = bitmapUv.iterator();
        while (msisdns.hasNext()) {
            System.out.println(msisdns.next());
        }
    }

    /**
     * txt 清数据存储到bitmap 中
     *
     * @param sourceFile
     * @param bitmapUv
     * @param bitMapFilter
     * @throws Exception
     */
    public static void txt2Bitmap(File sourceFile, BitmapUv bitmapUv, Txt2BitMapFilter bitMapFilter) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String msisdn = bitMapFilter.filter(line);
                if (msisdn != null) {
                    try {
                        bitmapUv.add(Long.valueOf(msisdn));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                line = bufferedReader.readLine();
            }
        }
    }

    public static void txt2Bitmap(File soreceFile, BitmapUv bitmapUv) throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(soreceFile))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                if (line != null) {
                    try {
                        bitmapUv.add(Long.valueOf(line.trim()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                line = bufferedReader.readLine();
            }
        }
    }

    public static interface Txt2BitMapFilter {
        public String filter(String msisdn);
    }
}
