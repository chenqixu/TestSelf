package com.bussiness.bi.bi_svc;

import com.bussiness.aig2.MD5;
import com.bussiness.bi.bigdata.log.LogBackUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(PswEncrypt.class);

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
            logger.info("[MD5.toMD5] passwd={}, md5pwd={}", passwd, md5pwd);

            char[] oldpwdstr = md5pwd.toCharArray();
            int oldlen = oldpwdstr.length;
            char[] keystr = operatorid.toCharArray();
            int keylen = keystr.length;
            //1.0.1
            char newpwdstrtemp[] = new char[oldlen * 2 + 1];
            char cAscII = '0';

            // 循环md5字符串
            for (int i = 0; i < oldlen; i++) {

                int temp = 0;
                // 循环user_id
                for (int j = 0; j < keylen; j++) {
                    temp += keystr[j];
                    temp += 5 * i + 7 * j + i * j + 11 + Math.abs(i - j) * 17;
//                    logger.debug("[循环user_id] temp={}", temp);
                }
                cAscII = oldpwdstr[i];
                cAscII = (char) ((((int) cAscII) + temp + 3 * i + 1) % 128);

                int quot = div(cAscII, 0x3e, 0);
                int rem = cAscII % 0x3e;

                // 拼接第一个char
                newpwdstrtemp[i * 2] = (char) (0x61 + quot);

                // 拼接第二个char
                if (rem < 26) {
                    newpwdstrtemp[i * 2 + 1] = (char) (0x61 + rem);
                } else if (rem < 36) {
                    newpwdstrtemp[i * 2 + 1] = (char) (0x30 + rem - 26);
                } else {
                    newpwdstrtemp[i * 2 + 1] = (char) (0x41 + rem - 36);
                }
            }
            logger.debug("[build newpwdstrtemp] newpwdstrtemp={}", new String(newpwdstrtemp));
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

    public static void PswDecrypt(String operatorid, String psw) {
        if (psw != null && psw.trim().length() > 0 && operatorid != null && operatorid.trim().length() > 0) {
            logger.debug("0x3e={}", 0x3e);

            char[] keystr = operatorid.toCharArray();
            int keylen = keystr.length;

            char[] pswCS = psw.toCharArray();
            char[] dePsw = new char[psw.length() / 2];
            int quot = -1;
            int rem = -1;
            int _user_id = 0;
            for (int i = 0; i < pswCS.length; i++) {
                logger.debug("{}", pswCS[i]);
                if (i % 2 == 0) {
                    // 计算quot
                    quot = pswCS[i] - 0x61;
                    logger.debug("quot={}", quot);

                    _user_id = 0;
                    int temp = 0;
                    // 0-0, 2-1, 4-2, 6-3, 8-4, 10-5
                    int _i = i / 2;
                    // 循环user_id
                    for (int j = 0; j < keylen; j++) {
                        temp += keystr[j];
                        temp += 5 * _i + 7 * j + _i * j + 11 + Math.abs(_i - j) * 17;
//                        logger.debug("[循环user_id] temp={}", temp);
                    }
                    // 先计算_user_id，方便下面逆向求cAscII_1
                    // cAscII = (char) ((((int) cAscII) + temp + 3 * i + 1) % 128);
                    _user_id = temp + 3 * _i + 1;
                } else {
                    // 计算rem
                    int unRemResult1 = pswCS[i] - 0x61;
                    int unRemResult2 = pswCS[i] - 0x30 + 26;
                    int unRemResult3 = pswCS[i] - 0x41 + 36;
                    if (unRemResult1 >= 0 && unRemResult1 < 26) {// 0<rem<26
                        rem = unRemResult1;
                    } else if (unRemResult2 >= 0 && unRemResult2 < 36) {// 0<rem<36
                        rem = unRemResult2;
                    } else {// rem>=36
                        rem = unRemResult3;
                    }
                    logger.debug("unRemResult1={}, unRemResult2={}, unRemResult3={}, rem={}"
                            , unRemResult1, unRemResult2, unRemResult3, rem);

                    // 计算cAscII，需要逆向取模和逆向div
                    logger.debug("quot={}, rem={}", quot, rem);
                    int cAscII;
                    int a = 0;
                    while (true) {
                        // 逆向取模
                        // 1...x=>a, a*62+rem=cAscII, 循环求x
                        cAscII = a * 62 + rem;
                        // 需要满足div
                        int _quot = div(cAscII, 0x3e, 0);
                        if (_quot == quot) {
                            logger.debug("[逆向div] cAscII={}, a={}, _quot={}", cAscII, a, _quot);
                            break;
                        }
                        a++;
                    }

                    // 逆向求cAscII_1
                    // cAscII = oldpwdstr[i];
                    // cAscII = (char) ((((int) cAscII) + temp + 3 * i + 1) % 128);
                    // (cAscII_1 + _user_id) % 128 = cAscII_2
                    // (cAscII_1 + _user_id - cAscII_2) / 128 = a, 1...x=>a
                    // a * 128 + cAscII_2 = cAscII_1 + _user_id
                    // a * 128 + cAscII_2 - _user_id = cAscII_1, 1...x=>a, 求x
                    int cAscII_1 = -1;
                    int x = 1;
                    while (cAscII_1 < 0) {
                        cAscII_1 = x * 128 + cAscII - _user_id;
                        x++;
                    }
                    logger.debug("cAscII_1={}", cAscII_1);

                    // 1-0, 3-1, 5-2, 7-3, 9-4, 11-5
                    int _j = (i - 1) / 2;
                    dePsw[_j] = (char) cAscII_1;
                }
            }
            logger.info("operatorid={}, unPswMD5={}", operatorid, new String(dePsw));
        }
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
        String oldStr = str;
        String result;
        if (str != null && str.length() > 1) {
            result = str.substring(0, str.length() - 1);
        } else {
            result = str;
        }
        logger.debug("[removelaststr] oldStr={}, result={}", oldStr, result);
        return result;
    }

    /**
     * 单元测试<br>
     * md5请跳转到网站https://www.somd5.com/
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LogBackUtil.init("I:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\logback.xml");
//		String passwd = "000000";
        // 二次加密后密码
        if (args.length == 2) {
            String user_id = args[0];
            String passwd = args[1];
            System.out.println(String.format("args[0]: %s, args[1]: %s", user_id, passwd));
            System.out.println(PswEncrypt.MD5PswEncrypt(passwd, user_id));
        } else {
            System.out.println("please input user_id & passwd");
        }
//		System.out.println(PswEncrypt.MD5PswEncrypt("111111","9990035"));
//        System.out.println(PswEncrypt.MD5PswEncrypt("123qweA~~", "9990424"));
//		System.out.println(PswEncrypt.MD5PswEncrypt("111111","9999022"));
//        System.out.println(PswEncrypt.MD5PswEncrypt("Ww16873313~~", "9990986"));
//        System.out.println(PswEncrypt.MD5PswEncrypt("123qweA~~", "9990158"));
//        System.out.println(PswEncrypt.MD5PswEncrypt("123qweA~~", "9990888"));
//        System.out.println(PswEncrypt.MD5PswEncrypt("123qweA~~", "1001531"));
        String newPws = PswEncrypt.MD5PswEncrypt("Qwe123@@", "9990986");
        logger.info("newPws={}", newPws);
        PswEncrypt.PswDecrypt("9990986", newPws);
        PswEncrypt.PswDecrypt("5003824", "aqbTbscbblbCbNbPa9b3ahbKbLbpbqaOaTaxaxbZaaaJb6bhbebZaDamaYaYbUbx");
        PswEncrypt.PswDecrypt("9990306", "bIagb0aQaabbbsaObCbBa3bWbZaNaqbfbhaDaAalaWbNa7bsafacaYa2bpbmaeaN");//Qwe123@@
        PswEncrypt.PswDecrypt("9990122", "bEacbwaMcaaXboaKb8b7azbSbVaJambbbda9a6ahaSbJa3boabccaUayblbiaaaJ");//Qwe123@@
        PswEncrypt.PswDecrypt("5004936", "bBbrbuaabjbHbVaDaIb6aZbNbPaCbyakaVa0a6bwagaPbJbmbmaHaHbDasbebWaH");
        PswEncrypt.PswDecrypt("5003824", "b4bWbmaCbQaNbeaAbybxapbIbLa9acaRaTazawcbaIb9atbebVbSaKaobbaYbUa9");
        PswEncrypt.PswDecrypt("5003824", "b6bnbZaFbdaObabQbvagaibJbIbsbqaOaTbFbnbmcdaLbAbfbhbYbzb5bbbha5a7");
        // 29ae1402ab2c0d0b53e2534c36a5e9a1
        // newland123

//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9990122")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990122);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9990424")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990424);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"8000005")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (8000005);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9991749")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9991749);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9990986")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990986);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000227")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000227);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000119")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000119);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000264")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000264);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"8000003")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (8000003);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9804245")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9804245);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000106")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000106);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000224")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000224);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9993110")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993110);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9993111")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993111);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9802490")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9802490);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9993112")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993112);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9990072")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990072);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"11000102")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (11000102);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9993113")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9993113);");
//		System.out.println("update sm_user set passwd='"+PswEncrypt.MD5PswEncrypt(md5passwd,"9990306")+"',passwd_repeat_cnt=0,lock_flag=0 where user_id in (9990306);");
    }
}
