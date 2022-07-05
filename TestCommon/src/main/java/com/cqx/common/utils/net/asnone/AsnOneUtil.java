package com.cqx.common.utils.net.asnone;

import com.cqx.common.utils.system.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * asn.1工具
 *
 * @author chenqixu
 */
public class AsnOneUtil {
    private static final Logger logger = LoggerFactory.getLogger(AsnOneUtil.class);

    public static TagBean parseTag(FileInputStream fis) throws IOException {
        // 读取标签
        // 先读取1个字节
        int tagLength = 0;
        String bitV = AsnOneUtil.readByte(fis);
        tagLength++;
        TagBean tagBean = AsnOneUtil.parseTag30(bitV);
        logger.info("parseTag30: {}", tagBean);
        if (tagBean.isTagValueToBig()) {
            StringBuilder _tmp_tag_number = new StringBuilder();
            // 如果tag number为11111，则继续读取
            while (tagBean.isTagValueToBig()) {
                bitV = AsnOneUtil.readByte(fis);
                tagLength++;
                TagBean _tagBean = AsnOneUtil.parseTag31(bitV);
                tagBean.setTagValueToBig(_tagBean.isTagValueToBig());
                logger.info("parseTag31: {}", _tagBean);
                _tmp_tag_number.append(_tagBean.getTagNumber());
            }
            tagBean.setTagNumber(_tmp_tag_number.toString());
        }
        tagBean.setTagLength(tagLength);
        logger.info("parseTag: {}", tagBean);
        return tagBean;
    }

    public static TagBean parseTag30(String tag) {
        //         8   7   6   5   4   3   2   1
        //   +-------+---+------------------+
        // 1 ¦ CLASS ¦P/C¦  TAG NUMBER    ¦
        //   +-------------------------------+
        //    Bits 8-7: 数据的类型标记:
        //   +------------------------------+
        //   ¦                          Bit: 8   7       ¦
        //   +-------------------------------¦
        //   ¦ Universal                 0   0       ¦
        //   ¦ Application              0   1      ¦
        //   ¦ Context-specific       1   0      ¦
        //   ¦ Private                     1   1      ¦
        //   +------------------------------+
        //    Bit  6  :原子式 (0) 或  结构类型 (1)
        //    Bits 5-1: 5个比特的二进制数
        TagBean tagBean = new TagBean();
        String tagClass = tag.substring(0, 2);
        tagBean.setTagClass(EnumTagClass.valueOfByValue(tagClass));
        String tagType = tag.substring(2, 3);
        tagBean.setTagType(EnumTagType.valueOfByValue(tagType));
        String tagNumber = tag.substring(3, 8);
        tagBean.setTagNumber(tagNumber);
        return tagBean;
    }

    public static TagBean parseTag31(String tag) {
        //      8   7   6   5   4   3   2   1
        //   +---+---------------------------¦ first
        // 2 ¦ 1 ¦  NUMBER of TAG (msb)    ¦ subsequent
        //   +-------------------------------¦
        //   .                               .
        //   .                               .
        //   +-------------------------------¦ last
        //   ¦ 0 ¦  NUMBER of TAG (lsb)       ¦ subsequent
        //   +-------------------------------+
        //
        //    Bits 8 :  最后一个字节时，设置为0，其他均设置为1
        //    Bits 7-1: 所有字节的比特7－1都加起来，就是实际的Tag值。
        TagBean tagBean = new TagBean();
        String subsequentTag = tag.substring(0, 1);
        tagBean.setSubsequentTag(subsequentTag);
        String tagNumber = tag.substring(1, 8);
        tagBean.setTagNumber(tagNumber);
        return tagBean;
    }

    public static LengthOctetsBean parseLengthOctets(FileInputStream fis) throws IOException {
        int lengthOctetsLength = 0;
        LengthOctetsBean lengthOctetsBean = new LengthOctetsBean();
        // 先读1个字节
        String bitV = readByte(fis);
        lengthOctetsLength++;
        // 判断是短编码还是长编码
        String flag = bitV.substring(0, 1);
        lengthOctetsBean.setFlag(flag);
        if (flag.equals("0")) {
            // 当长度<=127时，用一个字节表示，即为短编码方式：
            // 	第8位为0。
            // 	第7到第1位表示长度。
            // 如L=26H，则编码为 0010,0110。
            //     8   7   6   5   4   3   2   1
            //   +----------------------------¦
            // 1 ¦ 0   L   L   L   L   L   L   L      ¦
            //   +----------------------------+
            //	   LLLLLLL 表示实际长度的值
            lengthOctetsBean.setLength(Integer.valueOf(bitV.substring(1, 8), 2));
        } else if (flag.equals("1")) {
            // 当长度>127时，用多个字节表示，即为长编码方式：
            //         8   7   6   5   4   3   2   1
            //      +---+-------------------------¦
            //    1 ¦ 1 ¦      0 < n < 127              ¦
            //      +-----------------------------+
            //      +-----------------------------¦
            //    2 ¦ L   L   L   L   L   L   L   L  ¦
            //      +-----------------------------+
            //                 ...
            //      +-----------------------------¦
            // n+1¦ L   L   L   L   L   L   L   L       ¦
            //      +-----------------------------+
            //	       LLLLLLLL表示实际长度的值
            //
            // 	第1个字节的位8固定填写为1，BIT1~BIT7表示长度所占的字节数。
            // 	第2到n+1字节代表长度的值。
            // 读取长度所占的字节数
            int n = Integer.valueOf(bitV.substring(1, 8), 2);
            StringBuilder lenStr = new StringBuilder();
            for (int i = 0; i < n; i++) {
                String tmp = readByte(fis);
                lengthOctetsLength++;
                lenStr.append(tmp);
            }
            lengthOctetsBean.setLength(Integer.valueOf(lenStr.toString(), 2));
        } else {
            throw new NullPointerException(String.format("不认识的编码类型！[%s]", flag));
        }
        lengthOctetsBean.setLengthOctetsLength(lengthOctetsLength);
        return lengthOctetsBean;
    }

    public static String readByte(FileInputStream fis) throws IOException {
        return readByte(fis, 1);
    }

    public static String readByte(FileInputStream fis, int readLen) throws IOException {
        byte[] tmp = new byte[readLen];
        fis.read(tmp);
        StringBuilder bitVBuf = new StringBuilder();
        for (byte b : tmp) {
            bitVBuf.append(ByteUtil.byteToBit(b));
        }
        logger.info("读取{}个字节: {}, Hex: {}", readLen, bitVBuf.toString(), ByteUtil.bytesToHexStringH(tmp));
        return bitVBuf.toString();
    }
}
