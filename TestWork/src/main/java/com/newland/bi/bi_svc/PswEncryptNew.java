package com.newland.bi.bi_svc;

import com.newland.aig2.MD5;
import com.newland.bi.util.common.StringText;

import java.math.BigDecimal;

/**
 * PswEncryptNew
 *
 * @author chenqixu
 */
public class PswEncryptNew {

    public PswEncryptNew() {
    }

    public static String MD5PswEncrypt(String md5pwd, String operatorid) {
        String newpwdstr = "";
        if (md5pwd != null && md5pwd.trim().length() > 0 && operatorid != null && operatorid.trim().length() > 0) {
            char[] oldpwdstr = md5pwd.toCharArray();
            int oldlen = oldpwdstr.length;
            char[] keystr = operatorid.toCharArray();
            int keylen = keystr.length;
            char[] newpwdstrtemp = new char[oldlen * 2 + 1];
//            char cAscII = true;

            int i;
            int k;
            for (i = 0; i < oldlen; ++i) {
                k = 0;

                int quot;
                for (quot = 0; quot < keylen; ++quot) {
                    k += keystr[quot];
                    k += 5 * i + 7 * quot + i * quot + 11 + Math.abs(i - quot) * 17;
                }

                char cAscII = oldpwdstr[i];
                cAscII = (char) ((cAscII + k + 3 * i + 1) % 128);
                quot = div(cAscII, 62, 0);
                int rem = cAscII % 62;
                newpwdstrtemp[i * 2] = (char) (97 + quot);
                if (rem < 26) {
                    newpwdstrtemp[i * 2 + 1] = (char) (97 + rem);
                } else if (rem < 36) {
                    newpwdstrtemp[i * 2 + 1] = (char) (48 + rem - 26);
                } else {
                    newpwdstrtemp[i * 2 + 1] = (char) (65 + rem - 36);
                }
            }

            newpwdstrtemp[2 * oldlen] = 0;
            i = newpwdstrtemp.length;

            for (k = 0; k < i; ++k) {
                newpwdstr = newpwdstr + newpwdstrtemp[k];
            }

            return newpwdstr;
        } else {
            return null;
        }
    }

    public static int div(int v1, int v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精度数值必须是正整数或者0");
        } else {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.divide(b2, scale, 1).intValue();
        }
    }

    public static void main(String[] args) {
        String md5pwd = "96e79218965eb72c92a549dd5a330112";//MD5.toMD5("123qwe");
        String operatorid = "9990424";
        String passwd = PswEncryptNew.MD5PswEncrypt(md5pwd, operatorid);
        System.out.println(passwd);
        System.out.println(MD5.toMD5("111111"));
        System.out.println(MD5.toMD5("123qwe"));

        boolean auth_flag = true;
        SqlReqBean sqlBean = new SqlReqBean();
        sqlBean.setPasswd(passwd);
        SmUserBean user = new SmUserBean();
        user.setUser_id(9990424L);
        user.setPasswd("bSbzaoakb0bbbqahaWbHa0caaHaOb7bebmbTbyb4anbga7bvbyaQaUbIazboccaN");
        if (auth_flag
                && (!PswEncryptNew.MD5PswEncrypt(
                        sqlBean.getPasswd(), StringText.getStr(user.getUser_id())).trim()
                .equals(user.getPasswd().trim()))) {
            System.out.println("ok");
        }
    }
}
