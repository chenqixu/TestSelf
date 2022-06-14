package com.bussiness.bi.bigdata.string;

import java.util.Arrays;

/**
 * StringArray
 *
 * @author chenqixu
 */
public class StringArray {
    public static void main(String[] args) throws Exception {
        new StringArray().arrayDeal();
    }

    public void arrayDeal() throws Exception {
        String[] arr = {"a", "b", "c"};
        System.out.println(Arrays.asList(arr));
//        System.out.println("length：" + arr.length);
//        for (int i = 0; i < arr.length; i++) {
//            if ((i + 1) < arr.length)
//                System.out.println(i + " " + arr[i] + ",");
//            else
//                System.out.println(i + " " + arr[i]);
//        }

//        //获取系统默认编码
//        System.out.println("系统默认编码：" + System.getProperty("file.encoding"));//查询结果GBK
//        //系统默认字符编码
//        System.out.println("系统默认字符编码:" + Charset.defaultCharset()); //查询结果GBK
//        //操作系统用户使用的语言
//        System.out.println("系统默认语言:" + System.getProperty("user.language")); //查询结果zh
//        //定义字符串包含数字和中文
//        String t = "1a我";
//        //通过getBytes方法获取默认的编码
//        System.out.println("默认编码格式:");
//        byte[] b = t.getBytes();//ASCII,GBK,UTF-8对数字和英文字母的编码相同,对汉字的编码不同,unicode的编码跟前面三项都不同
//        //打印默认编码
//        for (byte c : b) {
//            System.out.print(c + ",\t");
//        }
//        System.out.println();
//        //打印GBK编码
//        System.out.println("GBK编码格式:");
//        b = t.getBytes("GBK");
//        for (byte c : b) {
//            System.out.print(c + ",\t");
//        }
//        System.out.println();
//        //打印GBK编码
//        System.out.println("UTF-8编码格式:");
//        b = t.getBytes("UTF-8");
//        for (byte c : b) {
//            System.out.print(c + ",\t");
//        }
//        System.out.println();
//        //打印GBK编码
//        System.out.println("ASCII编码格式:");
//        b = t.getBytes("ASCII");
//        for (byte c : b) {
//            System.out.print(c + ",\t");
//        }
//        System.out.println();
//        //打印GBK编码
//        System.out.println("UNICODE编码格式:");
//        b = t.getBytes("UNICODE");
//        for (byte c : b) {
//            System.out.print(c + ",\t");
//        }
    }
}
