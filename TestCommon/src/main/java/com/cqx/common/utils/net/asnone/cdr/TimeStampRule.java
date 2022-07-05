package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * Bytes To TimeStamp
 *
 * @author chenqixu
 */
public class TimeStampRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) throws Exception {
        // 表示UTC压缩格式的本地时间
        // -- YY: 	00~99年	BCD 编码
        // -- MM: 	01~12月	BCD 编码
        // -- DD: 	01~31天	BCD 编码
        // -- hh: 	00~23小时	BCD 编码
        // -- mm: 	00~59分钟	BCD 编码
        // -- ss: 	00~59秒	BCD 编码
        // -- S:  	“+”或“-”	ASCII编码
        // -- hh: 	00~23小时	BCD编码
        // -- mm:  	00~59分钟	BCD编码
        // 比如：码流：19 11 22 11 28 08 2B 08 00（16进制），表示19年11月22日11时28分08秒+8小时00分。
        // 具体可参见TS 32.298。
        //
        // BCD码（Binary-Coded Decimal‎），用4位二进制数来表示1位十进制数中的0~9这10个数码，
        // 是一种二进制的数字编码形式，用二进制编码的十进制代码。
        // BCD码这种编码形式利用了四个位元来储存一个十进制的数码，
        // 使二进制和十进制之间的转换得以快捷的进行。
        String str = ByteUtil.bytesToHexStringH(bytes);
        String yy = str.substring(0, 2);
        String mm = str.substring(2, 4);
        String dd = str.substring(4, 6);
        String hh = str.substring(6, 8);
        String mi = str.substring(8, 10);
        String ss = str.substring(10, 12);
        String _s = str.substring(12, 14);
        String s = new String(ByteUtil.hexStringToBytes(_s), StandardCharsets.UTF_8);
        String s_hh = str.substring(14, 16);
        String s_mi = str.substring(16, 18);
        return String.format("20%s-%s-%s %s:%s:%s %s%s %s", yy, mm, dd, hh, mi, ss, s, s_hh, s_mi);
    }
}
