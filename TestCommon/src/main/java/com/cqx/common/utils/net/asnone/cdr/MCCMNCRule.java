package com.cqx.common.utils.net.asnone.cdr;

import com.cqx.common.utils.system.ByteUtil;

/**
 * Bytes To MCC、MNC
 *
 * @author chenqixu
 */
public class MCCMNCRule implements ASNOneRule {

    @Override
    public String parse(byte[] bytes) throws Exception {
        // PLMN-Id ::=  OCTET STRING (SIZE(3))
        //	                 8	7	6	5	        4	3	2	1
        // Octet 1 |	MCC digit 2 |	MCC digit 1
        // Octet 2 |	MNC digit 3 |	MCC digit 3
        // Octet 3 |	MNC digit 2 |	MNC digit 1
        // -- MCC and MNC coded as defined in 3GPP TS 24.008 [32]
        String hexStr = ByteUtil.bytesToHexStringH(bytes);
        String octet1 = hexStr.substring(0, 2);
        String octet2 = hexStr.substring(2, 4);
        String octet3 = hexStr.substring(4, 6);
        String mcc1 = octet1.substring(1, 2);
        String mcc2 = octet1.substring(0, 1);
        String mcc3 = octet2.substring(1, 2);
        String mnc1 = octet3.substring(1, 2);
        String mnc2 = octet3.substring(0, 1);
        String mnc3 = octet2.substring(0, 1);
        String mcc = check(mcc1) + check(mcc2) + check(mcc3);
        String mnc = check(mnc1) + check(mnc2) + check(mnc3);
        return String.format("MCC=%s, MNC=%s", mcc, mnc);
    }

    private String check(String str) {
        if (str.equals("f") || str.equals("F")) {
            return "";
        } else {
            return str;
        }
    }
}
