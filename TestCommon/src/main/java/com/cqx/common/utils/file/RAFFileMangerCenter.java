package com.cqx.common.utils.file;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.StringSerializationImpl;
import com.cqx.common.utils.system.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

/**
 * RAFFileMangerCenter
 * <pre>
 *     结构：头部+具体内容+8位校验码
 *     头部：具体内容的起始位置+具体内容的长度
 *     8位校验码由具体内容进行CRC32处理生成
 * </pre>
 *
 * @author chenqixu
 */
public class RAFFileMangerCenter<T> implements Closeable {
    public static final String END_TAG = "#RAFFileMangerCenter#END#";
    private static final byte[] END_TAG_BYTES = END_TAG.getBytes();
    private static final Logger logger = LoggerFactory.getLogger(RAFFileMangerCenter.class);
    private int header_len = 20;
    private int header_half_len = header_len / 2;
    private int header_pos_next = 0;
    private byte[] null_byte = new byte[header_len];
    private byte[] single_null_byte = new byte[1];
    private String NULL_VALUE = new String(null_byte);
    private String SINGLE_NULL_VALUE = new String(single_null_byte);
    private char NULL_CHAR = SINGLE_NULL_VALUE.charAt(0);
    private BaseRandomAccessFile baseRandomAccessFile;
    private String file_name;
    private long max_length;
    private long length = 0L;
    private boolean split_flag = false;
    private AtomicBoolean end_write_flag = new AtomicBoolean(true);
    private ISerialization<T> iSerialization;

    public RAFFileMangerCenter(String file_name) throws FileNotFoundException {
        this(null, file_name);
    }

    public RAFFileMangerCenter(String file_name, long max_length) throws FileNotFoundException {
        this(null, file_name, max_length);
    }

    public RAFFileMangerCenter(ISerialization<T> iSerialization, String file_name) throws FileNotFoundException {
        this(iSerialization, file_name, 0L);
    }

    public RAFFileMangerCenter(ISerialization<T> iSerialization, String file_name, long max_length) throws FileNotFoundException {
        this.iSerialization = iSerialization;
        this.file_name = file_name;
        this.max_length = max_length;
        this.baseRandomAccessFile = new BaseRandomAccessFile(file_name);
        this.baseRandomAccessFile.setLock(true);
    }

    public int write(byte[] bytes) {
        if (split_flag) {
            return 2;
        }
        int header_pos = header_pos_next;
        String header = fillZero(header_pos + header_len, header_half_len)
                + fillZero(bytes.length, header_half_len);
        try {
            // header + data
            byte[] _content = ArrayUtil.arrayCopy(header.getBytes(), bytes);
            // 末尾写个8位CRC32校验码
            byte[] content = ArrayUtil.arrayCopy(_content, getCrc32Bytes(bytes));
            if (baseRandomAccessFile.write(header_pos, content)) {
                header_pos_next = header_pos + content.length;
                // 长度等于header+body
                length += content.length;
                logger.debug("【write】msg：{}，header：{}，header_pos：{}，header_pos_next：{}，length：{}",
                        bytes, header, header_pos, header_pos_next, length);
            } else {
                logger.warn("【write】写入失败，没抢到写锁");
                return 0;
            }
        } catch (IOException e) {
            logger.error("【write】写入失败：" + e.getMessage(), e);
            return 0;
        }
        // 需要文件切割
        if (max_length > 0 && length >= max_length) {
            // 写入结束符
            if (end_write_flag.getAndSet(false)) {
                writeEndTag();
            }
            split_flag = true;
        }
        return 1;
    }

    /**
     * 写入数据
     *
     * @param msg <br>0：失败<br>1：成功<br>2：需要切割文件
     * @return
     */
    public int write(String msg) {
        return write(msg.getBytes());
    }

    public int write(T t) {
        try {
            // 序列化
            byte[] bytes = iSerialization.serialize(t);
            return write(bytes);
        } catch (IOException e) {
            logger.error("【write】写入失败，序列化异常：" + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 写入文件结束符
     */
    public void writeEndTag() {
        write(END_TAG);
    }

    public byte[] readByte() {
        int header_pos = header_pos_next;
        byte[] msg = null;
        String pos = null;
        String len = null;
        //先读一个块
        try {
            String header = baseRandomAccessFile.read(header_pos, header_len);
            // 校验header是否为空
            if (NULL_VALUE.equals(header)) {
                return null;
            }
            // 校验header是否读取完整
            if (checkEndNull(header, header_pos, header_len)) {
                return null;
            }
            // 把header分为2部分
            pos = header.substring(0, header_half_len);
            len = header.substring(header_half_len, header_len);
            // 读取data
            msg = baseRandomAccessFile.readByte(Long.valueOf(pos), Integer.valueOf(len));
            // 读取CRC32
            long crc32StartPos = Long.valueOf(pos) + Integer.valueOf(len);
            byte[] crc32CheckBytes = baseRandomAccessFile.readByte(crc32StartPos, 8);
            // 校验CRC32
            long crc32CheckVal = Utils.bytesToLong(crc32CheckBytes);
            long msgCrc32 = getCrc32Long(msg);
            if (crc32CheckVal != msgCrc32) {// 校验失败
                logger.warn("CRC32校验失败！");
                return null;
            }
            // 下一个要读的起始位置，需要加上8位的校验
            header_pos_next = header_pos + header_len + Integer.valueOf(len) + 8;
            logger.debug("【read】header：{}，pos：{}，len：{}，msg：{}，header_pos：{}，header_pos_next：{}", header, pos, len, msg, header_pos, header_pos_next);
        } catch (IOException e) {
            logger.debug(String.format("【read】读取%s失败，header_pos：%s，pos：%s，len：%s，错误信息：%s", file_name, header_pos, pos, len, e.getMessage()), e);
        }
        return msg;
    }

    public String read() {
        if (iSerialization instanceof StringSerializationImpl) {
            RAFBean<T> rafBean = readDeserialize();
            return rafBean != null ? (String) rafBean.getT() : null;
        } else {
            byte[] msg = readByte();
            return msg != null ? new String(msg) : null;
        }
    }

    public RAFBean<T> readDeserialize() {
        RAFBean<T> rafBean = null;
        byte[] msg = readByte();
        if (msg != null) {// 有读到数据
            // 判断是否是结束符
            if (Arrays.equals(END_TAG_BYTES, msg)) {
                rafBean = new RAFBean<>(null);
                rafBean.setEnd();
            } else {
                // 反序列化
                try {
                    rafBean = new RAFBean<>(iSerialization.deserialize(msg));
                } catch (Exception e) {
                    logger.warn("反序列化异常：{}", e.getMessage());
                    return null;
                }
            }
        }
        return rafBean;
    }

    public void seekToBegin() {
        header_pos_next = 0;
    }

    /**
     * 回到结束标识<br>
     * 本身长度是25，header是20，还要加上末尾的8位校验位<br>
     * 所以是45+8
     */
    public void seekToEndTag() {
        header_pos_next = header_pos_next - 45 - 8;
    }

    /**
     * 校验最后一位是否为NULL，用以来判断信息是否完整
     *
     * @param msg
     * @return
     */
    private boolean checkEndNull(String msg, long pos, long len) {
        // 获取最后一位
        char endChar = msg.charAt(msg.length() - 1);
        // 判断是否为空
        if (endChar == NULL_CHAR) {
            logger.warn("【read】读取不完整，最后一位读取到了空值，pos：{}，len：{}", pos, len);
            return true;
        }
        return false;
    }

    private boolean checkEndNull(String msg, String pos, String len) {
        return checkEndNull(msg, Long.valueOf(pos), Integer.valueOf(len));
    }

    private boolean checkEndNull(byte[] msg, long pos, long len) {
        // 获取最后一位
        byte end = msg[msg.length - 1];
        // 判断是否为空
        if (end == single_null_byte[0]) {
            logger.warn("【read】读取不完整，最后一位读取到了{}，pos：{}，len：{}", end, pos, len);
            return true;
        }
        return false;
    }

    private boolean checkEndNull(byte[] msg, String pos, String len) {
        return checkEndNull(msg, Long.valueOf(pos), Integer.valueOf(len));
    }

    /**
     * 释放
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (baseRandomAccessFile != null) baseRandomAccessFile.close();
    }

    /**
     * 删除
     *
     * @return
     */
    public boolean del() {
        return FileUtil.del(file_name);
    }

    /**
     * 补0
     *
     * @param value 需要补0的字符串
     * @param len   总位数
     * @return
     */
    private String fillZero(int value, int len) {
        String result = String.valueOf(value);
        if (result.length() < len) {
            int surplus = len - result.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < surplus; i++) {
                sb.append("0");
            }
            sb.append(result);
            return sb.toString();
        }
        return result;
    }

    /**
     * 生成CRC32校验码，以byte[]输出
     *
     * @param values
     * @return
     */
    private byte[] getCrc32Bytes(byte[] values) {
        return Utils.longToBytes(getCrc32Long(values));
    }

    /**
     * 生成CRC32校验码，以long输出
     *
     * @param values
     * @return
     */
    private long getCrc32Long(byte[] values) {
        CRC32 crc32 = new CRC32();
        crc32.update(values);
        return crc32.getValue();
    }

    public int getHeader_pos_next() {
        return header_pos_next;
    }

    public void setHeader_pos(int header_pos) {
        this.header_pos_next = header_pos;
    }

    public String getFile_name() {
        return file_name;
    }

    public boolean isSplit_flag() {
        return split_flag;
    }
}
