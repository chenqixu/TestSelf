package com.newland.bi.bi_svc;

/**
 * @(#)bi_svc : PswEncrypt.java
 * <p>
 * Copyright (c) 2006 福建新大陆软件工程有限公司 版权所有
 * Newland Co. Ltd. All rights reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of Newland Co. Ltd.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Newland Co. Ltd
 */

import com.newland.aig2.MD5;

import java.math.BigDecimal;

/**
 * 对MD5串进行再次加密
 * PswEncrypt.java
 *
 * @author Eric(陈双双)
 * @version 1.0.0
 * @since 1.0.0  2007-10-17 created by Eric
 * history 1.0.1  2007-10-18 modified by Eric
 * 1. 原数据库位为64，boss为128位 需要修改
 */
public class PswEncrypt {

    /**
     * 构造函数
     */
    public PswEncrypt() {

    }

    /**
     * 二次加密方法
     *
     * @param passwd     传入密码
     * @param operatorid 密钥
     * @return MD5串二次加密的字符串
     */
    public static String MD5PswEncrypt(String passwd, String operatorid) {

        String newpwdstr = "";

        if (passwd != null && passwd.trim().length() > 0 && operatorid != null && operatorid.trim().length() > 0) {
            // md5后的字符串
            String md5pwd = MD5.toMD5(passwd);

            char[] oldpwdstr = md5pwd.toCharArray();
            int oldlen = oldpwdstr.length;
            char[] keystr = operatorid.toCharArray();
            int keylen = keystr.length;
            //1.0.1
            char newpwdstrtemp[] = new char[oldlen * 2 + 1];
            char cAscII = '0';

            for (int i = 0; i < oldlen; i++) {

                int temp = 0;
                for (int j = 0; j < keylen; j++) {
                    temp += keystr[j];
                    temp += 5 * i + 7 * j + i * j + 11 + Math.abs(i - j) * 17;
                }
                cAscII = oldpwdstr[i];
                cAscII = (char) ((((int) cAscII) + temp + 3 * i + 1) % 128);

                int quot = div(cAscII, 0x3e, 0);
                int rem = cAscII % 0x3e;

                newpwdstrtemp[i * 2] = (char) (0x61 + quot);

                if (rem < 26) newpwdstrtemp[i * 2 + 1] = (char) (0x61 + rem);
                else if (rem < 36) newpwdstrtemp[i * 2 + 1] = (char) (0x30 + rem - 26);
                else newpwdstrtemp[i * 2 + 1] = (char) (0x41 + rem - 36);
            }
            //1.0.1
            newpwdstrtemp[2 * oldlen] = 0x00;
            int lentemp = newpwdstrtemp.length;
            for (int k = 0; k < lentemp; k++) {
                newpwdstr += newpwdstrtemp[k];
            }
            return removelaststr(newpwdstr);
        }
        return null;
    }

    /**
     * 除法方法
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 精度
     * @return 不四舍五入的int的商
     */
    public static int div(int v1, int v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "精度数值必须是正整数或者0");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_DOWN).intValue();
    }

    public static String removelaststr(String str) {
        if (str != null && str.length() > 1)
            return str.substring(0, str.length() - 1);
        else
            return str;
    }

    /**
     * 单元测试
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//		String passwd = "000000";
        //二次加密后密码
        PswEncrypt synenc = new PswEncrypt();
        if (args.length == 2) {
            String user_id = args[0];
            String passwd = args[1];
            System.out.println(String.format("args[0]: %s, args[1]: %s", user_id, passwd));
            System.out.println(synenc.MD5PswEncrypt(passwd, user_id));
        } else {
            System.out.println("please input user_id & passwd");
        }
//		System.out.println(synenc.MD5PswEncrypt("111111","9990035"));
        System.out.println(synenc.MD5PswEncrypt("123qweA~~", "9990424"));
//		System.out.println(synenc.MD5PswEncrypt("111111","9999022"));
        System.out.println(synenc.MD5PswEncrypt("Ww16873313~~", "9990986"));
        System.out.println(synenc.MD5PswEncrypt("123qweA~~", "9990158"));
        System.out.println(synenc.MD5PswEncrypt("123qweA~~", "9990888"));
        System.out.println(synenc.MD5PswEncrypt("123qweA~~", "1001531"));
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9990122")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990122);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9990424")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990424);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"8000005")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (8000005);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9991749")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9991749);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9990986")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990986);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000227")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000227);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000119")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000119);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000264")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000264);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"8000003")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (8000003);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9804245")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9804245);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000106")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000106);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000224")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000224);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9993110")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993110);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9993111")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993111);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9802490")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9802490);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9993112")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993112);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9990072")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990072);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"11000102")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000102);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9993113")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993113);");
//		System.out.println("update sm_user set passwd='"+synenc.MD5PswEncrypt(md5passwd,"9990306")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990306);");
    }
}
