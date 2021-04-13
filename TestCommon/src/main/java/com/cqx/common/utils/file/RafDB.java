package com.cqx.common.utils.file;

import com.cqx.common.exception.rafdb.RafDBException;
import com.cqx.common.utils.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * RAFDB<br>
 * <pre>
 *         数据格式
 *         header + key + value + next link
 *         header = 具体位置，长度等于num_len
 *         key = 数据对应的key，长度等于key_len
 *         value = 具体数据，长度等于value_len
 *         next link = 下一个链表位置，长度等于num_len
 *         单个结构的数据总长为：2 * num_len + key_len + value_len
 *
 *         数据存储
 *         比如输入的数据是10个，这里就要做好20个槽的打算
 *         max_cnt等于槽位
 *         实际需要2 * max_cnt的槽位，所以num_len由2 * max_cnt决定
 *
 *         写入算法如下：
 *         步骤1：先mod
 *         步骤2：然后到对应槽位查询是否有人占用
 *         步骤3-1：如果有人占用，就读取占用者的next link
 *         步骤3-1-1：占用者的next link如果是0，就从全局读取link槽位，写入link槽位，并更新next link
 *         步骤3-1-2：如果占用者的next link不是0，就执行步骤2
 *         步骤3-2：如果没有人占用，就直接写入
 *
 *         查询算法如下：
 *         步骤1：先mod
 *         步骤2：然后到对应槽位查询
 *         步骤3-1：没有值，返回nil
 *         步骤3-2：查询到的和当前值比较
 *         步骤3-2-1：如果是，返回
 *         步骤3-2-2：如果不是，则读取next_link
 *         步骤3-2-3：如果next_link为0则返回
 *         步骤3-2-4：否则执行步骤2
 *
 *     </pre>
 *
 * @author chenqixu
 */
public class RafDB implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(RafDB.class);
    private MyRandomAccessFile myRandomAccessFile;
    private String NULL_VALUE;//空值判断
    private long max_cnt;//输入数据的总数，用于mod，决定了有2 * max_cnt的槽位进行数据装载
    private int key_len;//key数据的长度
    private int value_len;//value数据的长度
    private int num_len;//根据数据的总数决定数据位置的长度
    private int single_len;//单条完整数据的长度，含（header，key，value，next_link）
    private long idle_link;//全局未使用next_link，从max_cnt + 1的槽位开始

    public RafDB(String db_file) throws FileNotFoundException {
        this(db_file, false);
    }

    public RafDB(String db_file, boolean is_clean) throws FileNotFoundException {
        if (is_clean) FileUtil.del(db_file);
        myRandomAccessFile = new MyRandomAccessFile(db_file);
        myRandomAccessFile.setLock(true);
    }

    public void init(long max_cnt, int key_len, int value_len) {
        this.max_cnt = max_cnt;
        this.key_len = key_len;
        this.value_len = value_len;
        this.num_len = String.valueOf(max_cnt * 2).length();
        this.single_len = 2 * num_len + key_len + value_len;
        this.idle_link = max_cnt + 1;
        byte[] null_byte = new byte[single_len];
        this.NULL_VALUE = new String(null_byte);
        logger.info("槽位：{}，位置长度：{}，key长度：{}，value长度：{}，数据长度：{}，全局未使用next_link：{}",
                max_cnt * 2, num_len, key_len, value_len, single_len, idle_link);
    }

    @Override
    public void close() throws IOException {
        if (myRandomAccessFile != null) myRandomAccessFile.close();
    }

    /**
     * 查询
     *
     * @param mod
     * @return
     * @throws IOException
     */
    private String read(long mod) throws IOException {
        //如果是01，就从0开始
        //如果是02，就要从2*数据长度开始
        long pos = 0;
        if (mod > 1) pos = mod * single_len;
        return myRandomAccessFile.read(pos, single_len);
    }

    /**
     * 真正写入、更新操作
     *
     * @param rafDBData
     * @throws IOException
     */
    private void write(RafDBData rafDBData) throws IOException {
        //如果是01，就从0开始写，写single_len位
        //如果是02，就要从2*数据长度开始写，写single_len位
        long header = Long.valueOf(rafDBData.getHeader());
        long pos = 0;
        if (header > 1) pos = header * single_len;
        myRandomAccessFile.write(pos, rafDBData.getVal());
    }

    /**
     * 写入算法如下
     * <pre>
     * 步骤1：先mod
     * 步骤2：然后到对应槽位查询是否有人占用
     * 步骤3-1：如果有人占用，就读取占用者的next link
     * 步骤4：占用者的next link如果是0，就从全局读取link槽位，写入link槽位，并更新next link
     * 步骤5：如果占用者的next link不是0，就执行步骤2
     * 步骤3-2：如果没有人占用，就直接写入
     * </pre>
     *
     * @param key
     * @param value
     * @throws IOException
     * @throws RafDBException
     */
    public void put(long key, String value) throws IOException, RafDBException {
        //key校验
        checkKey(key);
        //value校验
        checkValue(value);
        String real_key = StringUtil.fillZero(key, key_len);
        RafDBData newData = new RafDBData();
        newData.setKey(real_key);
        newData.setValue(value);
        //先mod
        long mod = key % max_cnt;
        //写入逻辑
        write(mod, newData);
    }

    /**
     * 查询算法如下
     * <pre>
     * 步骤1：先mod
     * 步骤2：然后到对应槽位查询
     * 步骤3-1：没有值，返回nil
     * 步骤3-2：有值，查询到的和当前值比较
     * 步骤3-2-1：如果是，返回
     * 步骤3-2-2：如果不是，则读取next_link
     * 步骤3-2-3：如果next_link为0则返回
     * 步骤3-2-4：否则执行步骤2
     * </pre>
     *
     * @param key
     * @throws RafDBException
     */
    public String get(long key) throws RafDBException, IOException {
        //key校验
        checkKey(key);
        //先mod
        long mod = key % max_cnt;
        String getVal = get(mod, key);
        return getVal != null ? getVal.trim() : null;
    }

    private void checkKey(long key) throws RafDBException {
        if (String.valueOf(key).length() > key_len)
            throw new RafDBException(String.format("key长度不符，key：%s，要求长度不超过：%s", key, key_len));
    }

    private void checkValue(String value) throws RafDBException {
        if (value != null && value.length() > value_len)
            throw new RafDBException(String.format("value长度不符，value：%s，要求长度不超过：%s", value, value_len));
    }

    /**
     * 查询逻辑
     *
     * @param mod
     * @param val
     * @return
     * @throws IOException
     * @throws RafDBException
     */
    private String get(long mod, long val) throws IOException, RafDBException {
        //然后到对应槽位查询
        String read = read(mod);
        logger.debug("到对应槽位查询，mod：{}，read：{}", mod, read);
        //没有值，返回nil
        if (NULL_VALUE.equals(read)) {
            logger.debug("没有值，返回nil，val：{}", val);
            return null;
        } else {//有值，查询到的和当前值比较
            RafDBData queryData = new RafDBData(read);
            logger.debug("有值，查询到的和当前值比较，queryData：{}", queryData);
            //如果是，返回
            if (Long.valueOf(queryData.getKey()) == val) {
                return queryData.getValue();
            } else {//如果不是，则读取next_link
                long next_link = Long.valueOf(queryData.getNextLink());
                logger.debug("如果不是，则读取next_link：{}", next_link);
                if (next_link <= 0) {
                    logger.debug("next_link<=0，没有值，返回nil，val：{}", val);
                    return null;
                } else {//执行步骤2
                    return get(next_link, val);
                }
            }
        }
    }

    /**
     * 写入逻辑
     *
     * @param mod
     * @param newData
     * @throws IOException
     */
    private void write(long mod, RafDBData newData) throws IOException {
        //然后到对应槽位查询是否有人占用
        String read = read(mod);
        logger.debug("查询是否有人占用槽位，mod：{}，read：{}", mod, read);
        //如果有人占用，就读取占用者的next link
        if (!NULL_VALUE.equals(read)) {
            RafDBData oldData = new RafDBData(read);
            long nextlink = Long.valueOf(oldData.getNextLink());
            logger.debug("读取占用者 next_link：{}，oldData：{}", nextlink, oldData);
            //占用者的next link如果是0，就从全局读取link槽位，写入link槽位，并更新next link
            if (nextlink == 0) {
                //从全局读取link槽位，执行后槽位要自增
                String newHeader = StringUtil.fillZero(idle_link++, num_len);
                //oldData设置next_link
                oldData.setNextLink(newHeader);
                //更新oldData
                write(oldData);
                //设置newData.header
                newData.setHeader(newHeader);
                //写入newData
                write(newData);
                logger.debug("更新和写入 oldData：{}，newData：{}", oldData, newData);
            } else {//如果占用者的next link不是0，就执行步骤2
                write(nextlink, newData);
            }
        } else {//如果没有人占用，就直接写入
            //设置newData.header
            newData.setHeader(StringUtil.fillZero(mod, num_len));
            //写入newData
            write(newData);
            logger.debug("写入 newData：{}", newData);
        }
    }

    public void testRD(String val) {
        logger.info("val：{}，RafDBData：{}", val, new RafDBData(val));
    }

    class RafDBData {
        private String header;
        private String key;
        private String value;
        private String nextLink = StringUtil.fillZero(0, num_len);

        RafDBData() {
        }

        RafDBData(String val) {
            if (val != null && val.length() == single_len) {
                setHeader(val.substring(0, num_len));
                setKey(val.substring(num_len, key_len + num_len));
                setValue(val.substring(key_len + num_len, key_len + value_len + num_len));
                setNextLink(val.substring(key_len + value_len + num_len));
            }
        }

        public String getVal() {
            return getHeader() + getKey() + getValue() + getNextLink();
        }

        public String toString() {
            return "header：" + getHeader() + "，key：" + getKey() + "，value：" + getValue() + "，nextLink：" + getNextLink();
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getNextLink() {
            return nextLink;
        }

        public void setNextLink(String nextLink) {
            this.nextLink = nextLink;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            //不足要补空格
            this.value = StringUtil.fillSpace(value, value_len);
        }
    }
}
