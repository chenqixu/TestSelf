package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To UserLocationInformation
 *
 * @author chenqixu
 */
public class UserLocationInformationRule implements ASNOneRule {
    private MCCMNCRule mccmncRule = new MCCMNCRule();

    @Override
    public String parse(byte[] bytes) throws Exception {
        // UserLocationInformation ::= OCTET STRING (SIZE(4..17))
        //	8	7	6	5	4	3	2	1
        // Octet 1	3GPP type = 22
        // Octet 2	3GPP Length= m
        // Octet 3	Geographic Location Type
        // Octet 4-m	Geographic Location (octet string)
        // Octet 1：固定编码为22（10进制）
        // Octet 2：整个OCTET STRING的长度（即=m）
        // Octet 3：0（10进制）表示2G:CGI；2（10进制）表示2G:RAI； 130（10进制）表示4G:TAI + 4G:ECGI；137（10进制）表示5G:TAI + 5G:NCGI
        // Octet 4~m：编码Geographic Location (octet string)，m最大值为17
        // 上述具体定义参见TS 29.061。
        // 2G CGI field（参见TS 29.274）：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	a	MCC digit 2	MCC digit 1
        //	a+1	MNC digit 3	MCC digit 3
        //	a+2	MNC digit 2	MNC digit 1
        //	a+3 to a+4	Location Area Code (LAC)
        //	a+5 to a+6	Cell Identity (CI)
        //
        // 2G RAI field（参见TS 29.274）：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	c	MCC digit 2	MCC digit 1
        //	c+1	MNC digit 3	MCC digit 3
        //	c+2	MNC digit 2	MNC digit 1
        //	c+3 to c+4	Location Area Code (LAC)
        //	c+5 to c+6	Routing Area Code (RAC)
        // 仅Octet c+5包含RAC，Octet c+6编码为11111111。
        //
        // 4G TAI field（参见TS 29.274）：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	d	MCC digit 2	MCC digit 1
        //	d+1	MNC digit 3	MCC digit 3
        //	d+2	MNC digit 2	MNC digit 1
        //	d+3 to d+4	Tracking Area Code (TAC)
        //
        // 4G ECGI field（参见TS 29.274,Spare填写0） ：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	e	MCC digit 2	MCC digit 1
        //	e+1	MNC digit 3	MCC digit 3
        //	e+2	MNC digit 2	MNC digit 1
        //	e+3	Spare	ECI
        //	e+4 to e+6	ECI (E-UTRAN Cell Identifier)
        //
        // 5G TAI field（参见TS 38.413）：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	d	MCC digit 2	MCC digit 1
        //	d+1	MNC digit 3	MCC digit 3
        //	d+2	MNC digit 2	MNC digit 1
        //	d+3 to d+5	Tracking Area Code (TAC)
        //
        // 5G NCGI field（参见TS 38.413）：
        //		Bits
        //	Octets	8	7	6	5	4	3	2	1
        //	e	MCC digit 2	MCC digit 1
        //	e+1	MNC digit 3	MCC digit 3
        //	e+2	MNC digit 2	MNC digit 1
        //	e+3	Spare	NRCI
        //	e+4 to e+7	NRCI (NR Cell Identifier)

        int length = bytes.length;
        // Octet 1：固定编码为22（10进制）
        int octet1 = Integer.valueOf(ByteUtil.bytesToHexStringH(new byte[]{bytes[0]}), 16);
        // Octet 2：整个OCTET STRING的长度（即=m）
        int octet2 = Integer.valueOf(ByteUtil.bytesToHexStringH(new byte[]{bytes[1]}), 16);
        // Octet 3：
        //     0（10进制）表示2G:CGI
        //     2（10进制）表示2G:RAI
        //     130（10进制）表示4G:TAI + 4G:ECGI
        //     137（10进制）表示5G:TAI + 5G:NCGI
        int octet3 = Integer.valueOf(ByteUtil.bytesToHexStringH(new byte[]{bytes[2]}), 16);
        String first = String.format("threeGPPType=%s, geographicLocationType=%s, ", octet1, octet3);
        String end = null;
        switch (octet3) {
            case 0:// 2G:CGI

                break;
            case 2:// 2G:RAI

                break;
            case 130:// 4G:TAI + 4G:ECGI

                break;
            case 137:// 5G:TAI + 5G:NCGI
                // TAI，6 bytes
                // MCC、MNC、TAC
                // NCGI，8 bytes
                // MCC、MNC、NRCI
                end = String.format("TAI {%s, TAC=H'%s}, "
                        , mccmncRule.parse(new byte[]{bytes[3], bytes[4], bytes[5]})
                        , ByteUtil.bytesToHexStringH(new byte[]{bytes[6], bytes[7], bytes[8]}))
                        + String.format("NCGI {%s, NRCI=H'%s}"
                        , mccmncRule.parse(new byte[]{bytes[9], bytes[10], bytes[11]})
                        , ByteUtil.bytesToHexStringH(new byte[]{bytes[12], bytes[13], bytes[14], bytes[15], bytes[16]}));
                break;
            default:
                throw new NullPointerException("未识别的octet3！" + octet3);
        }
        return first + end;
    }
}
