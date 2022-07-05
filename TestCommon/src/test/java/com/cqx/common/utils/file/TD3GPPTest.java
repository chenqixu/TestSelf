package com.cqx.common.utils.file;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.net.asnone.AsnOneUtil;
import com.cqx.common.utils.net.asnone.LengthOctetsBean;
import com.cqx.common.utils.net.asnone.TagBean;
import com.cqx.common.utils.net.asnone.cdr.*;
import com.cqx.common.utils.system.ByteUtil;
import org.bouncycastle.asn1.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 3gpp测试
 *
 * @author chenqixu
 */
public class TD3GPPTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TD3GPPTest.class);
    private int allLength = 0;

    @Test
    public void htmlTest() throws IOException {
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.setReader("d:\\Work\\10.知识库\\3gpp\\3GPP specification Release version matrix.htm");
            final String keyword = "<td bordercolor=\"#000000\"> <a href=";
            fileUtil.read(new FileCount() {
                @Override
                public void run(String content) throws IOException {
                    if (content.contains(keyword)) {
                        String[] contents = content.split(keyword, -1);
//             logger.info("content: {}", content);
                        for (String str : contents) {
                            //找到第一个</，并且后面是a>
                            int index = str.indexOf("</");
                            String tag = str.substring(index + 2, index + 4);
                            if (tag.equals("a>")) {
                                //找到第一个>前面的数据且不是</a>
                                //找到不是http开头
                                String[] newDatas = str.split(">", -1);
                                logger.info("tag: {}, newDatas[0]: {}", tag, newDatas[0]);
                            }
                        }
                    }
                }
            });
        } finally {
            fileUtil.closeRead();
        }
    }

    private void readTL(FileInputStream fis) throws IOException {
        // 读取标签
        TagBean tagBean = AsnOneUtil.parseTag(fis);
        int tagLength = tagBean.getTagLength();
        // 读取长度
        LengthOctetsBean lengthOctetsBean = AsnOneUtil.parseLengthOctets(fis);
        int bodyLength = lengthOctetsBean.getLength();
        int lengthOctetsLength = lengthOctetsBean.getLengthOctetsLength();
        int headerLength = tagLength + lengthOctetsLength;
        allLength -= headerLength;
        logger.info("flag: {}, length: {}, headerLength: {}, readTL.allLength: {}"
                , lengthOctetsBean.getFlag(), bodyLength, headerLength, allLength);
        switch (tagBean.getTagType()) {
            case Primitive:
                readV(fis, bodyLength);
                break;
            case Construct:
                logger.info("Construct.body.length: {}", bodyLength);
                readTL(fis);
                break;
            default:
                throw new NullPointerException(String.format("不认识的TagType！%s", tagBean.getTagType()));
        }
    }

    private void readV(FileInputStream fis, int length) throws IOException {
        // 读取内容
        AsnOneUtil.readByte(fis, length);
        allLength -= length;
        logger.info("readV.allLength: {}", allLength);
    }

    @Test
    public void abnormalReleaseTest() throws IOException {
        File f = new File(getResourcePath("asn/abnormalRelease/Disk0/B2022040101427.dat"));
        try (FileInputStream fis = new FileInputStream(f)) {
            // 读取标签
            TagBean tagBean = AsnOneUtil.parseTag(fis);
            // 读取长度
            LengthOctetsBean lengthOctetsBean = AsnOneUtil.parseLengthOctets(fis);
            logger.info("flag: {}, length: {}", lengthOctetsBean.getFlag(), lengthOctetsBean.getLength());

            // 读取标签
            AsnOneUtil.parseTag(fis);
            // 读取长度
            lengthOctetsBean = AsnOneUtil.parseLengthOctets(fis);
            logger.info("flag: {}, length: {}", lengthOctetsBean.getFlag(), lengthOctetsBean.getLength());
            // 读取内容
            AsnOneUtil.readByte(fis, lengthOctetsBean.getLength());

            // 读取标签
            AsnOneUtil.parseTag(fis);
            // 读取长度
            lengthOctetsBean = AsnOneUtil.parseLengthOctets(fis);
            logger.info("flag: {}, length: {}", lengthOctetsBean.getFlag(), lengthOctetsBean.getLength());
            // 读取内容
            AsnOneUtil.readByte(fis, lengthOctetsBean.getLength());

            // 读取标签
            AsnOneUtil.parseTag(fis);
            // 读取长度
            lengthOctetsBean = AsnOneUtil.parseLengthOctets(fis);
            logger.info("flag: {}, length: {}", lengthOctetsBean.getFlag(), lengthOctetsBean.getLength());
            // 读取内容
            AsnOneUtil.readByte(fis, lengthOctetsBean.getLength());
        }
    }

    @Test
    public void abnormalReleaseHexTest() throws IOException {
        File f = new File(getResourcePath("asn/abnormalRelease/Disk0/B2022040101427.dat"));
        try (FileInputStream fis = new FileInputStream(f)) {
            // 读取5个字节
            byte[] tmp = new byte[6];
            fis.read(tmp);
            logger.info("{}", ByteUtil.bytesToHexStringH(tmp));
        }
    }

    @Test
    public void abnormalReleaseTLV() throws IOException {
//        File f = new File(getResourcePath("asn/abnormalRelease/Disk0/B2022040101427.dat"));
        File f = new File(getResourcePath("asn/eahc.dat"));
        try (FileInputStream fis = new FileInputStream(f)) {
            allLength = fis.available();
            logger.info("allLength: {}", allLength);
            while (allLength > 0) {
                readTL(fis);
            }
        }
    }

    @Test
    public void tagNumber() {
        logger.info("{}", Integer.valueOf("0000001", 2));
        logger.info("{}", Integer.valueOf("1001000", 2));
        logger.info("{}", Integer.valueOf("00000011001000", 2));
        logger.info("{}", Integer.valueOf("01011", 2));
        logger.info("{}", Integer.valueOf("00101", 2));
        logger.info("{}", Integer.valueOf("00C8", 16));
        logger.info("{}", ByteUtil.bytesToHexStringH("+".getBytes()));
    }

//    private void read(DERObject readObj) {
//        logger.info("readObj: {}", readObj.getClass());
//
//    }

    private void read(ASN1Primitive readObj) throws IOException {
//        readObj.getEncoded(ASN1Encoding.BER);
//        logger.info("readObj: {}", readObj.getClass());
        if (readObj instanceof DERTaggedObject) {
            DERTaggedObject app = (DERTaggedObject) readObj;
            ASN1Primitive appObject = app.getObject();
            logger.info("tagNo: {}, object: {}", app.getTagNo(), appObject.getClass());
            read(appObject);
        } else if (readObj instanceof DLSequence) {
            DLSequence dlSequence = (DLSequence) readObj;
            Enumeration dlSeqEnum = dlSequence.getObjects();
            while (dlSeqEnum.hasMoreElements()) {
                ASN1Primitive seqObj = (ASN1Primitive) dlSeqEnum.nextElement();
                read(seqObj);
            }
        } else if (readObj instanceof DEROctetString) {

        } else if (readObj instanceof DERSequence) {
            DERSequence derSequence = (DERSequence) readObj;
            Enumeration derSeqEnum = derSequence.getObjects();
            while (derSeqEnum.hasMoreElements()) {
                ASN1Primitive seqObj = (ASN1Primitive) derSeqEnum.nextElement();
                read(seqObj);
            }
        } else if (readObj instanceof BEROctetString) {

        } else if (readObj instanceof BERSequence) {

        } else if (readObj instanceof BERSet) {

        } else if (readObj instanceof BERTaggedObject) {

        } else if (readObj instanceof DERApplicationSpecific) {

        } else if (readObj instanceof DERBitString) {

        } else if (readObj instanceof DERBMPString) {

        } else if (readObj instanceof ASN1Boolean) {

        } else if (readObj instanceof ASN1Enumerated) {

        } else if (readObj instanceof DERGeneralString) {

        } else if (readObj instanceof ASN1GeneralizedTime) {

        } else if (readObj instanceof DERIA5String) {

        } else if (readObj instanceof DERNull) {

        } else if (readObj instanceof DERNumericString) {

        } else if (readObj instanceof ASN1ObjectIdentifier) {

        } else if (readObj instanceof ASN1Integer) {

        } else {
            logger.warn("!! Not deal Obj: {}", readObj.getClass());
        }
    }

    private void read(ASN1Primitive readObj, ASNOneBean parent, int num) throws IOException {
        // 对象
        if (readObj instanceof ASN1TaggedObject) {
            ASN1TaggedObject app = (ASN1TaggedObject) readObj;
            ASN1Primitive appObject = app.getObject();
            num++;
            logger.debug("tagNo: {}, num: {}, parent: {}, object: {}", app.getTagNo(), num, parent, appObject.getClass());
            if (parent.isRoot()) {
                parent.setTagNo(app.getTagNo());
                parent.setRoot(false);
                read(appObject, parent, num);
            } else {
                ASNOneBean child = new ASNOneBean();
                child.setTagNo(app.getTagNo());
                parent.addChild(child);
                read(appObject, child, num);
            }
        }
        // 有序集合，sequence
        else if (readObj instanceof ASN1Sequence) {
            ASN1Sequence sequence = (ASN1Sequence) readObj;
            Enumeration seqEnum = sequence.getObjects();
            logger.debug("num: {}, parent: {}, object: {}", num, parent, readObj.getClass());
            while (seqEnum.hasMoreElements()) {
                ASN1Primitive seqObj = (ASN1Primitive) seqEnum.nextElement();
                read(seqObj, parent, num);
            }
        }
        // bit string
        else if (readObj instanceof ASN1OctetString) {
            ASN1OctetString octetString = (ASN1OctetString) readObj;
            parent.setLeaf(true);
            parent.setValue(octetString.getOctets());
        } else {
            logger.warn("!! Not deal Obj: {}", readObj.getClass());
        }
    }

    @Test
    public void bouncycastleTest() throws Exception {
        File f = new File(getResourcePath("asn/abnormalRelease/Disk0/B2022040101427.dat"));
//        File f = new File(getResourcePath("asn/eahc.dat"));

        // CHFRecord	::= CHOICE
        // --
        // -- Record values 200..201 are specific
        // --
        // {
        // 	chargingFunctionRecord			[200] ChargingRecord
        // }
        // ChargingRecord 	::= SET
        // {
        // 	recordType						[0] RecordType,
        // 	recordingNetworkFunctionID		[1] NetworkFunctionName,
        //	subscriberIdentifier			[2] SubscriptionID OPTIONAL,
        //	nFunctionConsumerInformation	[3] NetworkFunctionInformation,
        //	triggers						[4] SEQUENCE OF Trigger OPTIONAL,
        //	listOfMultipleUnitUsage			[5] SEQUENCE OF MultipleUnitUsage OPTIONAL,
        //	recordOpeningTime				[6] TimeStamp,
        //	duration						[7] CallDuration,
        //	recordSequenceNumber			[8] INTEGER OPTIONAL,
        //	causeForRecClosing				[9] CauseForRecClosing,
        //	diagnostics						[10] Diagnostics OPTIONAL,
        //	localRecordSequenceNumber		[11] LocalSequenceNumber OPTIONAL,
        //	recordExtensions				[12] ManagementExtensions OPTIONAL,
        //	pDUSessionChargingInformation	[13] PDUSessionChargingInformation OPTIONAL,
        //	roamingQBCInformation			[14] RoamingQBCInformation OPTIONAL,
        //	sMSChargingInformation			[15] SMSChargingInformation OPTIONAL,
        //	chargingSessionIdentifier		[16] ChargingSessionIdentifier OPTIONAL,
        //	recordSequenceNumberList [201] IPRecordSequenceNumber OPTIONAL,
        //	localRecordSequenceNumberList [202] IPLocalRecordSequenceNumber OPTIONAL,
        //	consolidationResult	[203] ConsolidationResult OPTIONAL
        // }

        ASNOneBean ChargingRecord = new ASNOneBean();
        ChargingRecord.setTagNo(200);
        ChargingRecord.setName("ChargingRecord");

        //===================================
        // cdr.asn
        // recordType						[0] RecordType,
        //===================================
        // [GenericChargingDataTypes.asn]
        // RecordType 	::= INTEGER
        // --
        // --	Record values 0..17 and 87,89  are CS specific. The contents are defined in TS 32.250 [10]
        // --
        ASNOneBean recordType = new ASNOneBean();
        recordType.setTagNo(0);
        recordType.setLeaf(true);
        recordType.setName("recordType");
        recordType.setAsnOneRule(new IntegerRule());
        ChargingRecord.addChild(recordType);

        //===================================
        // cdr.asn
        // recordingNetworkFunctionID		[1] NetworkFunctionName,
        //===================================
        // [cdr.asn]
        // NetworkFunctionName	::= IA5String (SIZE(1..40))
        // -- Shall be a Universally Unique Identifier (UUID) version 4, as described in IETF RFC 4122 [410]
        ASNOneBean recordingNetworkFunctionID = new ASNOneBean();
        recordingNetworkFunctionID.setTagNo(1);
        recordingNetworkFunctionID.setLeaf(true);
        recordingNetworkFunctionID.setName("recordingNetworkFunctionID");
        recordingNetworkFunctionID.setAsnOneRule(new IA5StringRule());
        ChargingRecord.addChild(recordingNetworkFunctionID);

        //===================================
        // cdr.asn
        // subscriberIdentifier			[2] SubscriptionID OPTIONAL,
        //===================================
        // [GenericChargingDataTypes.asn]
        // SubscriptionID	::= SET
        // --
        // -- used for ExternalIdentifier with SubscriptionIdType = END-User-NAI. See TS 23.003 [200]
        // --
        // {
        //	subscriptionIDType	[0]	SubscriptionIDType,
        //	subscriptionIDData	[1]	UTF8String
        // }
        //
        // SubscriptionIDType	::= ENUMERATED
        // {
        //	eND-USER-E164		(0),
        //	eND-USER-IMSI		(1),
        //	eND-USER-SIP-URI		(2),
        //	eND-USER-NAI			(3),
        //	eND-USER-PRIVATE		(4)
        // }
        ASNOneBean subscriberIdentifier = new ASNOneBean();
        subscriberIdentifier.setTagNo(2);
        subscriberIdentifier.setLeaf(false);
        subscriberIdentifier.setName("subscriberIdentifier");
        ChargingRecord.addChild(subscriberIdentifier);

        ASNOneBean subscriptionIDType = new ASNOneBean();
        subscriptionIDType.setTagNo(0);
        subscriptionIDType.setLeaf(true);
        subscriptionIDType.setName("subscriptionIDType");
        EnumeratedRule enumeratedRuleSubscriptionIDType = new EnumeratedRule();
        enumeratedRuleSubscriptionIDType.addEnum("0", "eND-USER-E164");
        enumeratedRuleSubscriptionIDType.addEnum("1", "eND-USER-IMSI");
        enumeratedRuleSubscriptionIDType.addEnum("2", "eND-USER-SIP-URI");
        enumeratedRuleSubscriptionIDType.addEnum("3", "eND-USER-NAI");
        enumeratedRuleSubscriptionIDType.addEnum("4", "eND-USER-PRIVATE");
        subscriptionIDType.setAsnOneRule(enumeratedRuleSubscriptionIDType);
        subscriberIdentifier.addChild(subscriptionIDType);

        ASNOneBean subscriptionIDData = new ASNOneBean();
        subscriptionIDData.setTagNo(1);
        subscriptionIDData.setLeaf(true);
        subscriptionIDData.setName("subscriptionIDData");
        subscriptionIDData.setAsnOneRule(new IA5StringRule());
        subscriberIdentifier.addChild(subscriptionIDData);

        //===================================
        // cdr.asn
        // nFunctionConsumerInformation	[3] NetworkFunctionInformation,
        //===================================
        // [cdr.asn]
        // NetworkFunctionInformation	::= SEQUENCE
        // {
        //	networkFunctionality				[0] NetworkFunctionality,
        //	networkFunctionName					[1] NetworkFunctionName OPTIONAL,
        //	networkFunctionIPv4Address			[2] IPAddress OPTIONAL,
        //	networkFunctionPLMNIdentifier	 	[3] PLMN-Id OPTIONAL,
        //	networkFunctionIPv6Address			[4] IPAddress,
        // -- if networkFunctionIPv6Address is not available a CHF configured value shall be used.
        //	networkFunctionFQDN					[5] NodeAddress OPTIONAL
        // }
        //
        // NetworkFunctionality	::= ENUMERATED
        // {
        //	cHF			(0),	-- this value is not used
        //	sMF			(1),
        //	aMF			(2),
        //	sMSF		(3),
        //	sGW		(4),
        // 	iSMF		(5),
        //	sGSN		(6)
        // }
        //
        // NetworkFunctionName	::= IA5String (SIZE(1..40))
        // -- Shall be a Universally Unique Identifier (UUID) version 4, as described in IETF RFC 4122 [410]
        //
        // [doc说明]
        // 9.5.2	IPAddress
        // IPAddress ::= CHOICE
        // {
        //    iPBinV4Address              [0] OCTET STRING (SIZE(4)),
        //    iPBinV6Address              [1] OCTET STRING (SIZE(16)),
        //    iPTextV4Address             [2] IA5String (SIZE(7..15)),
        //    iPTextV6Address             [3] IA5String (SIZE(15..45)),
        //   iPBinV6AddressWithPrefix     [4] IPBinV6AddressWithPrefixLength
        // }
        // IPBinV6AddressWithPrefixLength ::= SEQUENCE
        // {
        //       iPBinV6Address                   OCTET STRING (SIZE(16)),
        //       pDPAddressPrefixLength            PDPAddressPrefixLength DEFAULT 64
        // }
        // PDPAddressPrefixLength		::=INTEGER (1..64)
        //
        // 1）iPBinV4Address：固定编码为4个字节AABBCCDD（16进制），对应的解码结果是AA.BB.CC.DD(AA/BB/CC/DD转换为10进制），比如C0A80101解码结果是192.168.1.1
        // 2）iPBinV6Address：固定编码为16个字节，每2个字节一组，比如：AAAA BBBB CCCC DDDD EEEE FFFF GGGG HHHH，解码结果是AAAA:BBBB:CCCC:DDDD:EEEE:FFFF:GGGG:HHHH
        // 3）iPTextV4Address：字符串类型，长度范围7~15。
        // 4）iPTextV6Address：字符串类型,长度范15~45。
        // 5）iPBinV6AddressWithPrefix：SEQUENCE结构，包含iPBinV6Address和PDPAddressPrefixLength（即：PDP/PDN IPv6地址前缀长度），默认64。
        //	说明：不带tag（即没有[]定义的tag值）时，按照universal类型编码tag，比如OCTET STRING的universal tag索引值是04（码流中的tag也是04），INTEGER的universaltag索引值是02（码流中的tag也是02）。
        //
        // [doc说明]
        // 9.5.4	PLMN-Id
        // PLMN-Id ::=  OCTET STRING (SIZE(3))
        //	8	7	6	5	4	3	2	1
        // Octet 1	MCC digit 2	MCC digit 1
        // Octet 2	MNC digit 3	MCC digit 3
        // Octet 3	MNC digit 2	MNC digit 1
        // -- MCC and MNC coded as defined in 3GPP TS 24.008 [32]
        //
        // [GenericChargingDataTypes.asn]
        // NodeAddress ::= CHOICE
        // {
        //	iPAddress 	[0] IPAddress,
        //	domainName	[1] GraphicString
        // }
        //
        ASNOneBean nFunctionConsumerInformation = new ASNOneBean();
        nFunctionConsumerInformation.setTagNo(3);
        nFunctionConsumerInformation.setLeaf(false);
        nFunctionConsumerInformation.setName("nFunctionConsumerInformation");
        ChargingRecord.addChild(nFunctionConsumerInformation);

        ASNOneBean networkFunctionality = new ASNOneBean();
        networkFunctionality.setTagNo(0);
        networkFunctionality.setLeaf(true);
        networkFunctionality.setName("networkFunctionality");
        EnumeratedRule enumeratedRuleNetworkFunctionality = new EnumeratedRule();
        enumeratedRuleNetworkFunctionality.addEnum("0", "cHF");
        enumeratedRuleNetworkFunctionality.addEnum("1", "sMF");
        enumeratedRuleNetworkFunctionality.addEnum("2", "aMF");
        enumeratedRuleNetworkFunctionality.addEnum("3", "sMSF");
        enumeratedRuleNetworkFunctionality.addEnum("4", "sGW");
        enumeratedRuleNetworkFunctionality.addEnum("5", "iSMF");
        enumeratedRuleNetworkFunctionality.addEnum("6", "sGSN");
        networkFunctionality.setAsnOneRule(enumeratedRuleNetworkFunctionality);
        nFunctionConsumerInformation.addChild(networkFunctionality);

        ASNOneBean networkFunctionName = new ASNOneBean();
        networkFunctionName.setTagNo(1);
        networkFunctionName.setLeaf(true);
        networkFunctionName.setName("networkFunctionName");
        networkFunctionName.setAsnOneRule(new IA5StringRule());
        nFunctionConsumerInformation.addChild(networkFunctionName);

        ASNOneBean networkFunctionIPv4Address = new ASNOneBean();
        networkFunctionIPv4Address.setTagNo(2);
        networkFunctionIPv4Address.setLeaf(false);
        networkFunctionIPv4Address.setName("networkFunctionIPv4Address");
        nFunctionConsumerInformation.addChild(networkFunctionIPv4Address);

        ASNOneBean iPBinV4Address = new ASNOneBean();
        iPBinV4Address.setTagNo(0);
        iPBinV4Address.setLeaf(true);
        iPBinV4Address.setName("iPBinV4Address");
        iPBinV4Address.setAsnOneRule(new IPV4Rule());
        networkFunctionIPv4Address.addChild(iPBinV4Address);

        ASNOneBean iPBinV6Address = new ASNOneBean();
        iPBinV6Address.setTagNo(1);
        iPBinV6Address.setLeaf(true);
        iPBinV6Address.setName("iPBinV6Address");
        iPBinV6Address.setAsnOneRule(new IPV6Rule());
        networkFunctionIPv4Address.addChild(iPBinV6Address);

        ASNOneBean networkFunctionPLMNIdentifier = new ASNOneBean();
        networkFunctionPLMNIdentifier.setTagNo(3);
        networkFunctionPLMNIdentifier.setLeaf(true);
        networkFunctionPLMNIdentifier.setName("networkFunctionPLMNIdentifier");
        networkFunctionPLMNIdentifier.setAsnOneRule(new MCCMNCRule());
        nFunctionConsumerInformation.addChild(networkFunctionPLMNIdentifier);

        ASNOneBean networkFunctionIPv6Address = new ASNOneBean();
        networkFunctionIPv6Address.setTagNo(4);
        networkFunctionIPv6Address.setLeaf(false);
        networkFunctionIPv6Address.setName("networkFunctionIPv6Address");
        nFunctionConsumerInformation.addChild(networkFunctionIPv6Address);

        ASNOneBean networkFunctionFQDN = new ASNOneBean();
        networkFunctionFQDN.setTagNo(5);
        networkFunctionFQDN.setLeaf(false);
        networkFunctionFQDN.setName("networkFunctionFQDN");
        nFunctionConsumerInformation.addChild(networkFunctionFQDN);

        ASNOneBean domainName = new ASNOneBean();
        domainName.setTagNo(1);
        domainName.setLeaf(true);
        domainName.setName("domainName");
        domainName.setAsnOneRule(new IA5StringRule());
        networkFunctionFQDN.addChild(domainName);

        //===================================
        // cdr.asn
        // recordOpeningTime				[6] TimeStamp,
        //===================================
        ASNOneBean recordOpeningTime = new ASNOneBean();
        recordOpeningTime.setTagNo(6);
        recordOpeningTime.setLeaf(true);
        recordOpeningTime.setName("recordOpeningTime");
        recordOpeningTime.setAsnOneRule(new TimeStampRule());
        ChargingRecord.addChild(recordOpeningTime);

        //===================================
        // cdr.asn
        // duration						[7] CallDuration,
        //===================================
        // [GenericChargingDataTypes.asn]
        // CallDuration 			::= INTEGER
        // --
        // -- The call duration is counted in seconds.
        // -- For successful calls /sessions / PDP contexts, this is the chargeable duration.
        // -- For call attempts this is the call holding time.
        // --
        ASNOneBean duration = new ASNOneBean();
        duration.setTagNo(7);
        duration.setLeaf(true);
        duration.setName("duration");
        duration.setAsnOneRule(new IntegerRule());
        ChargingRecord.addChild(duration);

        //===================================
        // cdr.asn
        // recordSequenceNumber			[8] INTEGER OPTIONAL,
        //===================================
        ASNOneBean recordSequenceNumber = new ASNOneBean();
        recordSequenceNumber.setTagNo(8);
        recordSequenceNumber.setLeaf(true);
        recordSequenceNumber.setName("recordSequenceNumber");
        recordSequenceNumber.setAsnOneRule(new IntegerRule());
        ChargingRecord.addChild(recordSequenceNumber);

        //===================================
        // cdr.asn
        // causeForRecClosing				[9] CauseForRecClosing,
        //===================================
        // [GenericChargingDataTypes]
        // CauseForRecClosing	::= INTEGER
        // --
        // -- Cause codes 0 to 15 are defined 'CauseForTerm' (cause for termination)
        // -- There is no direct correlation between these two types.
        // --
        // -- LCS related causes belong to the MAP error causes acc. TS 29.002 [214]
        // --
        // -- In PGW-CDR and SGW-CDR the value servingNodeChange is used for partial record
        // -- generation due to Serving Node Address list Overflow
        // -- In SGSN servingNodeChange indicates the SGSN change
        // --
        // -- sWGChange value is used in both the S-GW, TWAG and ePDG for inter serving node change
        // --
        // {
        //	normalRelease					(0),
        //	abnormalRelease					(4),
        //	cAMELInitCallRelease			(5),
        //	volumeLimit						(16),
        //	timeLimit						(17),
        //	servingNodeChange				(18),
        //	maxChangeCond					(19),
        //	managementIntervention			(20),
        //	intraSGSNIntersystemChange		(21),
        //	rATChange						(22),
        //	mSTimeZoneChange				(23),
        //	sGSNPLMNIDChange 				(24),
        //	sGWChange						(25),
        //	aPNAMBRChange					(26),
        //	mOExceptionDataCounterReceipt	(27),
        //	unauthorizedRequestingNetwork	(52),
        //	unauthorizedLCSClient			(53),
        //	positionMethodFailure			(54),
        //	unknownOrUnreachableLCSClient	(58),
        //	listofDownstreamNodeChange		(59)
        // }
        ASNOneBean causeForRecClosing = new ASNOneBean();
        causeForRecClosing.setTagNo(9);
        causeForRecClosing.setLeaf(true);
        causeForRecClosing.setName("causeForRecClosing");
        EnumeratedRule enumeratedRuleCauseForRecClosing = new EnumeratedRule();
        enumeratedRuleCauseForRecClosing.addEnum("0", "normalRelease");
        enumeratedRuleCauseForRecClosing.addEnum("4", "abnormalRelease");
        enumeratedRuleCauseForRecClosing.addEnum("5", "cAMELInitCallRelease");
        enumeratedRuleCauseForRecClosing.addEnum("16", "volumeLimit");
        enumeratedRuleCauseForRecClosing.addEnum("17", "timeLimit");
        enumeratedRuleCauseForRecClosing.addEnum("18", "servingNodeChange");
        enumeratedRuleCauseForRecClosing.addEnum("19", "maxChangeCond");
        enumeratedRuleCauseForRecClosing.addEnum("20", "managementIntervention");
        enumeratedRuleCauseForRecClosing.addEnum("21", "intraSGSNIntersystemChange");
        enumeratedRuleCauseForRecClosing.addEnum("22", "rATChange");
        enumeratedRuleCauseForRecClosing.addEnum("23", "mSTimeZoneChange");
        enumeratedRuleCauseForRecClosing.addEnum("24", "sGSNPLMNIDChange");
        enumeratedRuleCauseForRecClosing.addEnum("25", "sGWChange");
        enumeratedRuleCauseForRecClosing.addEnum("26", "aPNAMBRChange");
        enumeratedRuleCauseForRecClosing.addEnum("27", "mOExceptionDataCounterReceipt");
        enumeratedRuleCauseForRecClosing.addEnum("52", "unauthorizedRequestingNetwork");
        enumeratedRuleCauseForRecClosing.addEnum("53", "unauthorizedLCSClient");
        enumeratedRuleCauseForRecClosing.addEnum("54", "positionMethodFailure");
        enumeratedRuleCauseForRecClosing.addEnum("58", "unknownOrUnreachableLCSClient");
        enumeratedRuleCauseForRecClosing.addEnum("59", "listofDownstreamNodeChange");
        causeForRecClosing.setAsnOneRule(enumeratedRuleCauseForRecClosing);
        ChargingRecord.addChild(causeForRecClosing);

        //===================================
        // cdr.asn
        // localRecordSequenceNumber		[11] LocalSequenceNumber OPTIONAL,
        //===================================
        ASNOneBean localRecordSequenceNumber = new ASNOneBean();
        localRecordSequenceNumber.setTagNo(11);
        localRecordSequenceNumber.setLeaf(true);
        localRecordSequenceNumber.setName("localRecordSequenceNumber");
        localRecordSequenceNumber.setAsnOneRule(new IntegerRule());
        ChargingRecord.addChild(localRecordSequenceNumber);

        //===================================
        // cdr.asn
        // pDUSessionChargingInformation	[13] PDUSessionChargingInformation OPTIONAL,
        //===================================
        // [cdr]
        // PDUSessionChargingInformation 	::= SET
        // {
        //	pDUSessionChargingID			[0] ChargingID,
        //	userIdentifier					[1] InvolvedParty OPTIONAL,
        //	userEquipmentInfo				[2] SubscriberEquipmentNumber OPTIONAL,
        //	userLocationInformation			[3] OCTET STRING (SIZE(4..17)) OPTIONAL,
        //	userRoamerInOut					[4] RoamerInOut OPTIONAL,
        //	presenceReportingAreaInfo		[5]	PresenceReportingAreaInfo OPTIONAL,
        //	pDUSessionId					[6] PDUSessionId,
        //	networkSliceInstanceID			[7] NetworkSliceInstanceID OPTIONAL,
        //	pDUType							[8] PDUSessionType OPTIONAL,
        //	sSCMode							[9] SSCMode OPTIONAL,
        //	sUPIPLMNIdentifier				[10] PLMN-Id OPTIONAL,
        //	servingNetworkFunctionID		[11] SEQUENCE OF ServingNetworkFunctionID OPTIONAL,
        //	rATType							[12] RATType OPTIONAL,
        //	dataNetworkNameIdentifier		[13] DataNetworkNameIdentifier OPTIONAL,
        //	pDUAddress						[14] PDUAddress OPTIONAL,
        //	authorizedQoSInformation		[15] AuthorizedQoSInformation OPTIONAL,
        //	uETimeZone 						[16] MSTimeZone OPTIONAL,
        //	pDUSessionstartTime				[17] TimeStamp OPTIONAL,
        //	pDUSessionstopTime				[18] TimeStamp OPTIONAL,
        //	diagnostics						[19] Diagnostics OPTIONAL,
        //	chargingCharacteristics			[20] ChargingCharacteristics,
        //	chChSelectionMode				[21] ChChSelectionMode OPTIONAL,
        //	threeGPPPSDataOffStatus			[22] ThreeGPPPSDataOffStatus OPTIONAL,
        //	rANSecondaryRATUsageReport 		[23] SEQUENCE OF NGRANSecondaryRATUsageReport OPTIONAL,
        //	subscribedQoSInformation 		[24] SubscribedQoSInformation OPTIONAL,
        //	authorizedSessionAMBR 			[25] SessionAMBR OPTIONAL,
        //	subscribedSessionAMBR 			[26] SessionAMBR OPTIONAL,
        //	servingCNPLMNID					[27] PLMN-Id OPTIONAL,
        //	sUPIunauthenticatedFlag 		[28] NULL OPTIONAL,
        //	dNNSelectionMode				[29] DNNSelectionMode OPTIONAL
        // }
        //
        // [GenericChargingDataTypes]
        // ChargingID	::= INTEGER (0..4294967295)
        //--
        //-- Generated in P-GW, part of IP-CAN bearer
        //-- 0..4294967295 is equivalent to 0..2**32-1
        //--
        //
        // [GenericChargingDataTypes]
        // InvolvedParty ::= CHOICE
        //{
        //	sIP-URI		[0] GraphicString, -- refer to rfc3261 [401]
        //	tEL-URI		[1] GraphicString,	-- refer to rfc3966 [402]
        //	uRN			[2] GraphicString,	-- refer to rfc5031 [407]
        //	iSDN-E164 	[3] GraphicString	-- refer to ITU-T Recommendation E.164[308]
        //}
        //
        // [cdr.asn]
        // PDUSessionId 		::= INTEGER (0..255)
        // --
        // -- See 3GPP TS 29.571 [249] for details
        // --
        //
        // [cdr.asn]
        // NetworkSliceInstanceID	::= SEQUENCE
        // -- See S-NSSAI subclause 28.4.2 of TS 23.003 [200] for encoding.
        // {
        //	sST			[0] SliceServiceType,
        //	sD 			[1] SliceDifferentiator OPTIONAL
        // }
        //
        ASNOneBean pDUSessionChargingInformation = new ASNOneBean();
        pDUSessionChargingInformation.setTagNo(13);
        pDUSessionChargingInformation.setLeaf(false);
        pDUSessionChargingInformation.setName("pDUSessionChargingInformation");
        ChargingRecord.addChild(pDUSessionChargingInformation);

        //	pDUSessionChargingID			[0] ChargingID,
        ASNOneBean pDUSessionChargingID = new ASNOneBean();
        pDUSessionChargingID.setTagNo(0);
        pDUSessionChargingID.setLeaf(true);
        pDUSessionChargingID.setName("pDUSessionChargingID");
        pDUSessionChargingID.setAsnOneRule(new IntegerRule());
        pDUSessionChargingInformation.addChild(pDUSessionChargingID);

        //	userIdentifier					[1] InvolvedParty OPTIONAL,
        ASNOneBean userIdentifier = new ASNOneBean();
        userIdentifier.setTagNo(1);
        userIdentifier.setLeaf(false);
        userIdentifier.setName("InvolvedParty");
        pDUSessionChargingInformation.addChild(userIdentifier);

        ASNOneBean sIP_URI = new ASNOneBean();
        sIP_URI.setTagNo(0);
        sIP_URI.setLeaf(true);
        sIP_URI.setName("sIP-URI");
        sIP_URI.setAsnOneRule(new IA5StringRule());
        userIdentifier.addChild(sIP_URI);

        ASNOneBean tEL_URI = new ASNOneBean();
        tEL_URI.setTagNo(1);
        tEL_URI.setLeaf(true);
        tEL_URI.setName("tEL-URI");
        tEL_URI.setAsnOneRule(new IA5StringRule());
        userIdentifier.addChild(tEL_URI);

        ASNOneBean uRN = new ASNOneBean();
        uRN.setTagNo(2);
        uRN.setLeaf(true);
        uRN.setName("uRN");
        uRN.setAsnOneRule(new IA5StringRule());
        userIdentifier.addChild(uRN);

        ASNOneBean iSDN_E164 = new ASNOneBean();
        iSDN_E164.setTagNo(3);
        iSDN_E164.setLeaf(true);
        iSDN_E164.setName("iSDN-E164");
        iSDN_E164.setAsnOneRule(new IA5StringRule());
        userIdentifier.addChild(iSDN_E164);

        //	userEquipmentInfo				[2] SubscriberEquipmentNumber OPTIONAL,

        //	userLocationInformation			[3] OCTET STRING (SIZE(4..17)) OPTIONAL,
        ASNOneBean userLocationInformation = new ASNOneBean();
        userLocationInformation.setTagNo(3);
        userLocationInformation.setLeaf(true);
        userLocationInformation.setName("userLocationInformation");
        userLocationInformation.setAsnOneRule(new UserLocationInformationRule());
        pDUSessionChargingInformation.addChild(userLocationInformation);

        //	userRoamerInOut					[4] RoamerInOut OPTIONAL,

        //	presenceReportingAreaInfo		[5]	PresenceReportingAreaInfo OPTIONAL,

        //	pDUSessionId					[6] PDUSessionId,
        ASNOneBean pDUSessionId = new ASNOneBean();
        pDUSessionId.setTagNo(6);
        pDUSessionId.setLeaf(true);
        pDUSessionId.setName("pDUSessionId");
        pDUSessionId.setAsnOneRule(new IntegerRule());
        pDUSessionChargingInformation.addChild(pDUSessionId);

        //	networkSliceInstanceID			[7] NetworkSliceInstanceID OPTIONAL,
        // 9.4.4	Network Slice Instance ID
        // 这是一个SEQUENCE结构，对应ASN.1类型是：NetworkSliceInstanceID，ASN.1协议中包含以下子字段（参见：TS 23.003 S-NSSAI）：
        // 1）sST：INTEGER类型，表示切片类型。
        // 2）sD：8位数组类型（OCTET STRING），长度范围1~3，表示切片标识。
        ASNOneBean networkSliceInstanceID = new ASNOneBean();
        networkSliceInstanceID.setTagNo(7);
        networkSliceInstanceID.setLeaf(false);
        networkSliceInstanceID.setName("networkSliceInstanceID");
        pDUSessionChargingInformation.addChild(networkSliceInstanceID);

        ASNOneBean sST = new ASNOneBean();
        sST.setTagNo(0);
        sST.setLeaf(true);
        sST.setName("sST");
        sST.setAsnOneRule(new IntegerRule());
        networkSliceInstanceID.addChild(sST);

        ASNOneBean sD = new ASNOneBean();
        sD.setTagNo(1);
        sD.setLeaf(true);
        sD.setName("sD");
        sD.setAsnOneRule(new HexRule());
        networkSliceInstanceID.addChild(sD);

        //	pDUType							[8] PDUSessionType OPTIONAL,
        // PDUSessionType		::= ENUMERATED
        // {
        //	iPv4v6			(0),
        //	iPv4			(1),
        //	iPv6			(2),
        //	unstructured	(3),
        //	ethernet		(4)
        // }
        // -- See 3GPP TS 29.571 [249] for details.
        ASNOneBean pDUType = new ASNOneBean();
        pDUType.setTagNo(8);
        pDUType.setLeaf(true);
        pDUType.setName("pDUType");
        EnumeratedRule enumeratedRulePDUSessionType = new EnumeratedRule();
        enumeratedRulePDUSessionType.addEnum("0", "iPv4v6");
        enumeratedRulePDUSessionType.addEnum("1", "iPv4");
        enumeratedRulePDUSessionType.addEnum("2", "iPv6");
        enumeratedRulePDUSessionType.addEnum("3", "unstructured");
        enumeratedRulePDUSessionType.addEnum("4", "ethernet");
        pDUType.setAsnOneRule(enumeratedRulePDUSessionType);
        pDUSessionChargingInformation.addChild(pDUType);

        //	sSCMode							[9] SSCMode OPTIONAL,
        // SSCMode	::= INTEGER
        // {
        //	sSCMode1				(1),
        //	sSCMode2				(2),
        //	sSCMode3				(3)
        // }
        // -- See 3GPP TS 29.501 [248] for details.
        ASNOneBean sSCMode = new ASNOneBean();
        sSCMode.setTagNo(9);
        sSCMode.setLeaf(true);
        sSCMode.setName("sSCMode");
        EnumeratedRule enumeratedRuleSSCMode = new EnumeratedRule();
        enumeratedRuleSSCMode.addEnum("1", "sSCMode1");
        enumeratedRuleSSCMode.addEnum("2", "sSCMode2");
        enumeratedRuleSSCMode.addEnum("3", "sSCMode3");
        sSCMode.setAsnOneRule(enumeratedRuleSSCMode);
        pDUSessionChargingInformation.addChild(sSCMode);

        //	sUPIPLMNIdentifier				[10] PLMN-Id OPTIONAL,
        ASNOneBean sUPIPLMNIdentifier = new ASNOneBean();
        sUPIPLMNIdentifier.setTagNo(10);
        sUPIPLMNIdentifier.setLeaf(true);
        sUPIPLMNIdentifier.setName("sUPIPLMNIdentifier");
        sUPIPLMNIdentifier.setAsnOneRule(new MCCMNCRule());
        pDUSessionChargingInformation.addChild(sUPIPLMNIdentifier);

        //	servingNetworkFunctionID		[11] SEQUENCE OF ServingNetworkFunctionID OPTIONAL,
        //
        // ServingNetworkFunctionID	::= SEQUENCE
        // {
        //	servingNetworkFunctionInformation	[0] NetworkFunctionInformation,
        //	aMFIdentifier						[1] AMFID OPTIONAL
        //
        // }
        //
        // NetworkFunctionInformation	::= SEQUENCE
        // {
        //	networkFunctionality				[0] NetworkFunctionality,
        //	networkFunctionName					[1] NetworkFunctionName OPTIONAL,
        //	networkFunctionIPv4Address			[2] IPAddress OPTIONAL,
        //	networkFunctionPLMNIdentifier	 	[3] PLMN-Id OPTIONAL,
        //	networkFunctionIPv6Address			[4] IPAddress,
        // -- if networkFunctionIPv6Address is not available a CHF configured value shall be used.
        //	networkFunctionFQDN					[5] NodeAddress OPTIONAL
        // }
        //
        // AMFID	::= OCTET STRING (SIZE(6))
        // -- See subclause 2.10.1 of 3GPP TS 23.003 [7] for encoding.
        // -- AMFID is defined as an OCTET STRING with 3 bytes length, and is presented in first 3 bytes of this form, the last 3 bytes shall be padded with “FFF”
        ASNOneBean servingNetworkFunctionID = new ASNOneBean();
        servingNetworkFunctionID.setTagNo(11);
        servingNetworkFunctionID.setLeaf(false);
        servingNetworkFunctionID.setName("servingNetworkFunctionID");
        pDUSessionChargingInformation.addChild(servingNetworkFunctionID);

        ASNOneBean servingNetworkFunctionInformation = new ASNOneBean();
        servingNetworkFunctionInformation.setTagNo(0);
        servingNetworkFunctionInformation.setLeaf(false);
        servingNetworkFunctionInformation.setName("servingNetworkFunctionInformation");
        servingNetworkFunctionID.addChild(servingNetworkFunctionInformation);

        ASNOneBean networkFunctionalityCopy = new ASNOneBean(networkFunctionality);
        networkFunctionalityCopy.setTagNo(0);
        servingNetworkFunctionInformation.addChild(networkFunctionalityCopy);

        ASNOneBean networkFunctionNameCopy = new ASNOneBean(networkFunctionName);
        networkFunctionNameCopy.setTagNo(1);
        servingNetworkFunctionInformation.addChild(networkFunctionNameCopy);

        ASNOneBean networkFunctionIPv4AddressCopy = new ASNOneBean(networkFunctionIPv4Address);
        networkFunctionIPv4AddressCopy.setTagNo(2);
        servingNetworkFunctionInformation.addChild(networkFunctionIPv4AddressCopy);

        ASNOneBean networkFunctionPLMNIdentifierCopy = new ASNOneBean(networkFunctionPLMNIdentifier);
        networkFunctionPLMNIdentifierCopy.setTagNo(3);
        servingNetworkFunctionInformation.addChild(networkFunctionPLMNIdentifierCopy);

        ASNOneBean networkFunctionIPv6AddressCopy = new ASNOneBean(networkFunctionIPv6Address);
        networkFunctionIPv6AddressCopy.setTagNo(4);
        servingNetworkFunctionInformation.addChild(networkFunctionIPv6AddressCopy);

        ASNOneBean networkFunctionFQDNCopy = new ASNOneBean(networkFunctionFQDN);
        networkFunctionFQDNCopy.setTagNo(5);
        servingNetworkFunctionInformation.addChild(networkFunctionFQDNCopy);

        //	rATType							[12] RATType OPTIONAL,
        // 9.5.6	RATType
        // RATType ::= INTEGER(0..255)
        // 1	UTRAN
        // 2	GERAN
        // 3	WLAN
        // 4	GAN
        // 5	HSPA Evolution
        // 6	EUTRAN
        // 7	Virtual
        // 8         NB-IoT
        // 9         LTE-M
        // 51	NG-RAN
        ASNOneBean rATType = new ASNOneBean();
        rATType.setTagNo(12);
        rATType.setLeaf(true);
        rATType.setName("rATType");
        EnumeratedRule enumeratedRuleRATType = new EnumeratedRule();
        enumeratedRuleRATType.addEnum("1", "UTRAN");
        enumeratedRuleRATType.addEnum("2", "GERAN");
        enumeratedRuleRATType.addEnum("3", "WLAN");
        enumeratedRuleRATType.addEnum("4", "GAN");
        enumeratedRuleRATType.addEnum("5", "HSPA Evolution");
        enumeratedRuleRATType.addEnum("6", "EUTRAN");
        enumeratedRuleRATType.addEnum("7", "Virtual");
        enumeratedRuleRATType.addEnum("8", "NB-IoT");
        enumeratedRuleRATType.addEnum("9", "LTE-M");
        enumeratedRuleRATType.addEnum("51", "NG-RAN");
        rATType.setAsnOneRule(enumeratedRuleRATType);
        pDUSessionChargingInformation.addChild(rATType);

        //	dataNetworkNameIdentifier		[13] DataNetworkNameIdentifier OPTIONAL,
        // DataNetworkNameIdentifier	::= IA5String (SIZE(1..63))
        // --
        // -- Network Identifier part of DNN in dot representation.
        // -- For example, if the complete DNN is 'apn1a.apn1b.apn1c.mnc022.mcc111.gprs'
        // -- The Identifier is 'apn1a.apn1b.apn1c' and is presented in this form in the CDR.
        // --
        ASNOneBean dataNetworkNameIdentifier = new ASNOneBean();
        dataNetworkNameIdentifier.setTagNo(13);
        dataNetworkNameIdentifier.setLeaf(true);
        dataNetworkNameIdentifier.setName("dataNetworkNameIdentifier");
        dataNetworkNameIdentifier.setAsnOneRule(new IA5StringRule());
        pDUSessionChargingInformation.addChild(dataNetworkNameIdentifier);

        //	pDUAddress						[14] PDUAddress OPTIONAL,
        //
        // PDUAddress 	::= SEQUENCE
        // {
        //	pDUIPv4Address				[0] IPAddress OPTIONAL,
        //	pDUIPv6AddresswithPrefix	[1] IPAddress OPTIONAL,
        //	iPV4dynamicAddressFlag		[2] DynamicAddressFlag OPTIONAL,
        //	iPV6dynamicPrefixFlag		[3] DynamicAddressFlag OPTIONAL
        //
        // }
        //
        // DynamicAddressFlag	::= BOOLEAN
        ASNOneBean pDUAddress = new ASNOneBean();
        pDUAddress.setTagNo(14);
        pDUAddress.setLeaf(false);
        pDUAddress.setName("pDUAddress");
        pDUSessionChargingInformation.addChild(pDUAddress);

        ASNOneBean pDUIPv4Address = new ASNOneBean(networkFunctionIPv4Address);
        pDUIPv4Address.setTagNo(0);
        pDUIPv4Address.setName("pDUIPv4Address");
        pDUAddress.addChild(pDUIPv4Address);

        ASNOneBean pDUIPv6AddresswithPrefix = new ASNOneBean(networkFunctionIPv4Address);
        pDUIPv6AddresswithPrefix.setTagNo(1);
        pDUIPv6AddresswithPrefix.setName("pDUIPv6AddresswithPrefix");
        pDUAddress.addChild(pDUIPv6AddresswithPrefix);

        ASNOneBean iPV4dynamicAddressFlag = new ASNOneBean();
        iPV4dynamicAddressFlag.setTagNo(2);
        iPV4dynamicAddressFlag.setLeaf(true);
        iPV4dynamicAddressFlag.setName("iPV4dynamicAddressFlag");
        iPV4dynamicAddressFlag.setAsnOneRule(new BooleanRule());
        pDUAddress.addChild(iPV4dynamicAddressFlag);

        ASNOneBean iPV6dynamicPrefixFlag = new ASNOneBean();
        iPV6dynamicPrefixFlag.setTagNo(3);
        iPV6dynamicPrefixFlag.setLeaf(true);
        iPV6dynamicPrefixFlag.setName("iPV6dynamicPrefixFlag");
        iPV6dynamicPrefixFlag.setAsnOneRule(new BooleanRule());
        pDUAddress.addChild(iPV6dynamicPrefixFlag);

        //	authorizedQoSInformation		[15] AuthorizedQoSInformation OPTIONAL,
        //
        // AuthorizedQoSInformation	::= SEQUENCE
        // --
        // -- See TS 32.291 [58] for more information
        // --
        // {
        //	fiveQi				[1] INTEGER,
        //	aRP					[2] AllocationRetentionPriority,
        //	priorityLevel 		[3] INTEGER OPTIONAL,
        //	averWindow			[4] INTEGER OPTIONAL,
        //	maxDataBurstVol		[5] INTEGER OPTIONAL
        // }
        //
        // AllocationRetentionPriority	::= SEQUENCE
        // {
        //	priorityLevel 			[1] INTEGER,
        //	preemptionCapability	[2] PreemptionCapability,
        //	preemptionVulnerability	[3] PreemptionVulnerability
        // }
        //
        // PreemptionCapability		::= ENUMERATED
        // {
        //	nOT-PREEMPT			(0),
        //	mAY-PREEMPT			(1)
        // }
        //
        // PreemptionVulnerability		::= ENUMERATED
        // {
        //	nOT-PREEMPTABLE		(0),
        //	pREEMPTABLE			(1)
        // }
        ASNOneBean authorizedQoSInformation = new ASNOneBean();
        authorizedQoSInformation.setTagNo(15);
        authorizedQoSInformation.setLeaf(false);
        authorizedQoSInformation.setName("authorizedQoSInformation");
        pDUSessionChargingInformation.addChild(authorizedQoSInformation);

        ASNOneBean fiveQi = new ASNOneBean();
        fiveQi.setTagNo(1);
        fiveQi.setLeaf(true);
        fiveQi.setName("fiveQi");
        fiveQi.setAsnOneRule(new IntegerRule());
        authorizedQoSInformation.addChild(fiveQi);

        ASNOneBean aRP = new ASNOneBean();
        aRP.setTagNo(2);
        aRP.setLeaf(false);
        aRP.setName("aRP");
        authorizedQoSInformation.addChild(aRP);

        ASNOneBean priorityLevel = new ASNOneBean();
        priorityLevel.setTagNo(1);
        priorityLevel.setLeaf(true);
        priorityLevel.setName("priorityLevel");
        priorityLevel.setAsnOneRule(new IntegerRule());
        aRP.addChild(priorityLevel);

        ASNOneBean preemptionCapability = new ASNOneBean();
        preemptionCapability.setTagNo(2);
        preemptionCapability.setLeaf(true);
        preemptionCapability.setName("preemptionCapability");
        EnumeratedRule enumeratedRulePreemptionCapability = new EnumeratedRule();
        enumeratedRulePreemptionCapability.addEnum("0", "nOT-PREEMPT");
        enumeratedRulePreemptionCapability.addEnum("1", "mAY-PREEMPT");
        preemptionCapability.setAsnOneRule(enumeratedRulePreemptionCapability);
        aRP.addChild(preemptionCapability);

        ASNOneBean preemptionVulnerability = new ASNOneBean();
        preemptionVulnerability.setTagNo(3);
        preemptionVulnerability.setLeaf(true);
        preemptionVulnerability.setName("preemptionVulnerability");
        EnumeratedRule enumeratedRulePreemptionVulnerability = new EnumeratedRule();
        enumeratedRulePreemptionVulnerability.addEnum("0", "nOT-PREEMPTABLE");
        enumeratedRulePreemptionVulnerability.addEnum("1", "pREEMPTABLE");
        preemptionVulnerability.setAsnOneRule(enumeratedRulePreemptionVulnerability);
        aRP.addChild(preemptionVulnerability);

        ASNOneBean priorityLevelCopy = new ASNOneBean(priorityLevel);
        priorityLevelCopy.setTagNo(3);
        authorizedQoSInformation.addChild(priorityLevelCopy);

        ASNOneBean averWindow = new ASNOneBean();
        averWindow.setTagNo(4);
        averWindow.setLeaf(true);
        averWindow.setName("averWindow");
        averWindow.setAsnOneRule(new IntegerRule());
        authorizedQoSInformation.addChild(averWindow);

        ASNOneBean maxDataBurstVol = new ASNOneBean();
        maxDataBurstVol.setTagNo(5);
        maxDataBurstVol.setLeaf(true);
        maxDataBurstVol.setName("maxDataBurstVol");
        maxDataBurstVol.setAsnOneRule(new IntegerRule());
        authorizedQoSInformation.addChild(maxDataBurstVol);

        //	uETimeZone 						[16] MSTimeZone OPTIONAL,

        //	pDUSessionstartTime				[17] TimeStamp OPTIONAL,
        ASNOneBean pDUSessionstartTime = new ASNOneBean(recordOpeningTime);
        pDUSessionstartTime.setTagNo(17);
        pDUSessionstartTime.setName("pDUSessionstartTime");
        pDUSessionChargingInformation.addChild(pDUSessionstartTime);

        //	pDUSessionstopTime				[18] TimeStamp OPTIONAL,

        //	diagnostics						[19] Diagnostics OPTIONAL,

        //	chargingCharacteristics			[20] ChargingCharacteristics,
        //
        // ChargingCharacteristics	::= OCTET STRING (SIZE(2))
        ASNOneBean chargingCharacteristics = new ASNOneBean();
        chargingCharacteristics.setTagNo(20);
        chargingCharacteristics.setLeaf(true);
        chargingCharacteristics.setName("chargingCharacteristics");
        chargingCharacteristics.setAsnOneRule(new HexRule());
        pDUSessionChargingInformation.addChild(chargingCharacteristics);

        //	chChSelectionMode				[21] ChChSelectionMode OPTIONAL,
        //
        // ChChSelectionMode		::= ENUMERATED
        // {
        //	servingNodeSupplied			(0),	-- For S-GW/P-GW
        //	subscriptionSpecific		(1),	-- For SGSN only
        //	aPNSpecific					(2),	-- For SGSN only
        //	homeDefault					(3),	-- For SGSN, S-GW, P-GW, TDF and IP-Edge
        //	roamingDefault				(4),	-- For SGSN, S-GW, P-GW, TDF and IP-Edge
        //	visitingDefault				(5),	-- For SGSN, S-GW, P-GW, TDF and IP-Edge
        //	fixedDefault				(6)		-- For TDF and IP-Edge
        // }
        ASNOneBean chChSelectionMode = new ASNOneBean();
        chChSelectionMode.setTagNo(21);
        chChSelectionMode.setLeaf(true);
        chChSelectionMode.setName("chChSelectionMode");
        EnumeratedRule enumeratedRuleChChSelectionMode = new EnumeratedRule();
        enumeratedRuleChChSelectionMode.addEnum("0", "servingNodeSupplied");
        enumeratedRuleChChSelectionMode.addEnum("1", "subscriptionSpecific");
        enumeratedRuleChChSelectionMode.addEnum("2", "aPNSpecific");
        enumeratedRuleChChSelectionMode.addEnum("3", "homeDefault");
        enumeratedRuleChChSelectionMode.addEnum("4", "roamingDefault");
        enumeratedRuleChChSelectionMode.addEnum("5", "visitingDefault");
        enumeratedRuleChChSelectionMode.addEnum("6", "fixedDefault");
        chChSelectionMode.setAsnOneRule(enumeratedRuleChChSelectionMode);
        pDUSessionChargingInformation.addChild(chChSelectionMode);

        //	threeGPPPSDataOffStatus			[22] ThreeGPPPSDataOffStatus OPTIONAL,

        //	rANSecondaryRATUsageReport 		[23] SEQUENCE OF NGRANSecondaryRATUsageReport OPTIONAL,

        //	subscribedQoSInformation 		[24] SubscribedQoSInformation OPTIONAL,
        //
        // SubscribedQoSInformation	::= SEQUENCE
        // --
        // -- See TS 32.291 [58] for more information
        // --
        // {
        //	fiveQi				[1] INTEGER,
        //	aRP					[2] AllocationRetentionPriority OPTIONAL,
        //	priorityLevel 		[3] INTEGER OPTIONAL
        // }
        ASNOneBean subscribedQoSInformation = new ASNOneBean(authorizedQoSInformation);
        subscribedQoSInformation.setTagNo(24);
        subscribedQoSInformation.setName("subscribedQoSInformation");
        pDUSessionChargingInformation.addChild(subscribedQoSInformation);

        //	authorizedSessionAMBR 			[25] SessionAMBR OPTIONAL,
        //
        // SessionAMBR	::= SEQUENCE
        // {
        //	ambrUL				[1] Bitrate,
        //	ambrDL				[2] Bitrate
        // }
        // 9.4.11	Session AMBR
        // 这是一个SEQUENCE结构，对应ASN.1类型是：SessionAMBR，ASN.1协议中包含以下子字段（参考29571 Ambr）：
        // 1）ambrUL：OCTET STRING类型，实际是字符串，记录了上行最大比特率AMBR（Aggregate Maximum Bit Rate）。
        // 2）ambrDL:OCTET STRING类型，实际是字符串，记录了下行最大比特率AMBR（Aggregate Maximum Bit Rate）。
        ASNOneBean authorizedSessionAMBR = new ASNOneBean();
        authorizedSessionAMBR.setTagNo(25);
        authorizedSessionAMBR.setLeaf(false);
        authorizedSessionAMBR.setName("authorizedSessionAMBR");
        pDUSessionChargingInformation.addChild(authorizedSessionAMBR);

        ASNOneBean ambrUL = new ASNOneBean();
        ambrUL.setTagNo(1);
        ambrUL.setLeaf(true);
        ambrUL.setName("ambrUL");
        ambrUL.setAsnOneRule(new IA5StringRule());
        authorizedSessionAMBR.addChild(ambrUL);

        ASNOneBean ambrDL = new ASNOneBean();
        ambrDL.setTagNo(2);
        ambrDL.setLeaf(true);
        ambrDL.setName("ambrDL");
        ambrDL.setAsnOneRule(new IA5StringRule());
        authorizedSessionAMBR.addChild(ambrDL);

        //	subscribedSessionAMBR 			[26] SessionAMBR OPTIONAL,
        ASNOneBean subscribedSessionAMBR = new ASNOneBean(authorizedSessionAMBR);
        subscribedSessionAMBR.setTagNo(26);
        subscribedSessionAMBR.setName("subscribedSessionAMBR");
        pDUSessionChargingInformation.addChild(subscribedSessionAMBR);

        //	servingCNPLMNID					[27] PLMN-Id OPTIONAL,
        ASNOneBean servingCNPLMNID = new ASNOneBean(networkFunctionPLMNIdentifier);
        servingCNPLMNID.setTagNo(27);
        pDUSessionChargingInformation.addChild(servingCNPLMNID);

        //	sUPIunauthenticatedFlag 		[28] NULL OPTIONAL,
        //	dNNSelectionMode				[29] DNNSelectionMode OPTIONAL


        //===================================
        try (FileInputStream fis = new FileInputStream(f)) {
            ASN1InputStream asn1InputStream = new ASN1InputStream(fis);
            ASN1Primitive read;
            ASNOneBean data = new ASNOneBean(true);
            while ((read = asn1InputStream.readObject()) != null) {
//                logger.info("{}", read);
//                logger.info("{}", org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(read));
                // 第一层是ChargingRecord
                // 第二层是ChargingRecord的内部结构
                read(read, data, 0);
            }
            // 解析
            ASNOneBeanParser.parser(data, ChargingRecord);
            logger.info("{}", ChargingRecord.printTree());
        }
    }
}
