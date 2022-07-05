package com.cqx.common.utils.file;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.system.ByteUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.*;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.SimpleTestResult;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * <pre>
 *     The following code shows how to use BERTaggedObject from org.bouncycastle.asn1.
 *     Specifically, the code shows you how to use Java BouncyCastle BERTaggedObject
 *     BERTaggedObject(int tagNo, ASN1Encodable obj)
 * </pre>
 *
 * @author chenqixu
 */
public class EqualsAndHashCodeTest extends TestBase implements Test {
    private static final Logger logger = LoggerFactory.getLogger(EqualsAndHashCodeTest.class);

    @org.junit.Test
    public void performTest() {
        EqualsAndHashCodeTest test = new EqualsAndHashCodeTest();
        TestResult result = test.perform();

        logger.info("{}", result);
    }

    @org.junit.Test
    public void berTest() throws IOException {
        byte[] data = {0, 1, 0, 1, 0, 0, 1};
        ASN1Primitive values[] = {new BEROctetString(data)};
        try (ByteArrayOutputStream bOut = new ByteArrayOutputStream()) {
            ASN1OutputStream aOut = new ASN1OutputStream(bOut);
            for (int i = 0; i != values.length; i++) {
                aOut.writeObject(values[i]);
            }
            logger.info("{}", ByteUtil.bytesToHexStringH(bOut.toByteArray()));
        }
    }

    public TestResult perform() {
        byte[] data = {0, 1, 0, 1, 0, 0, 1};

        ASN1Primitive values[] = {new BEROctetString(data), new BERSequence(new DERPrintableString("hello world")),
                new BERSet(new DERPrintableString("hello world")),
                new BERTaggedObject(0, new DERPrintableString("hello world")), new DERApplicationSpecific(0, data),
                new DERBitString(data), new DERBMPString("hello world"), ASN1Boolean.getInstance(true),
                ASN1Boolean.getInstance(false), new ASN1Enumerated(100), new DERGeneralizedTime("20070315173729Z"),
                new DERGeneralString("hello world"), new DERIA5String("hello"), new ASN1Integer(1000),
                DERNull.INSTANCE, new DERNumericString("123456"), new ASN1ObjectIdentifier("1.1.1.10000.1"),
                new DEROctetString(data), new DERPrintableString("hello world"),
                new DERSequence(new DERPrintableString("hello world")),
                new DERSet(new DERPrintableString("hello world")), new DERT61String("hello world"),
                new DERTaggedObject(0, new DERPrintableString("hello world")), new DERUniversalString(data),
                new DERUTCTime(new Date()), new DERUTF8String("hello world"), new DERVisibleString("hello world"),
                new DERGraphicString(Hex.decode("deadbeef")),
                new DERVideotexString(Strings.toByteArray("Hello World"))};

        try {/*  w w w  .   d  e  m o    2 s   . c  o   m*/
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream aOut = new ASN1OutputStream(bOut);

            for (int i = 0; i != values.length; i++) {
                aOut.writeObject(values[i]);
            }

            try (FileOutputStream fos = new FileOutputStream(getResourcePath("asn") + "/eahc.dat")) {
                fos.write(bOut.toByteArray());
            }

            ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
            ASN1InputStream aIn = new ASN1InputStream(bIn);

            for (int i = 0; i != values.length; i++) {
                ASN1Primitive o = aIn.readObject();
                logger.info("{}", o);
                if (!o.equals(values[i])) {
                    return new SimpleTestResult(false, getName() + ": Failed equality test for " + o.getClass());
                }

                if (o.hashCode() != values[i].hashCode()) {
                    return new SimpleTestResult(false, getName() + ": Failed hashCode test for " + o.getClass());
                }
            }
        } catch (Exception e) {
            return new SimpleTestResult(false, getName() + ": Failed - exception " + e.toString(), e);
        }

        return new SimpleTestResult(true, getName() + ": Okay");
    }

    public String getName() {
        return "EqualsAndHashCode";
    }
}
