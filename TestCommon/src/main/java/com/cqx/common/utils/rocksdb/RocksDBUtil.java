package com.cqx.common.utils.rocksdb;

import com.cqx.common.utils.file.FileUtil;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * RocksDBUtil
 *
 * @author chenqixu
 */
public class RocksDBUtil {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBUtil.class);

    static {
        //a static method that loads the RocksDB C++ library
        RocksDB.loadLibrary();
    }

    private String dbFilePath;
    private RocksDB rocksDB;

    public RocksDBUtil(String db_FilePath, String dbName) throws RocksDBException {
        this(db_FilePath, dbName, false);
    }

    public RocksDBUtil(String db_FilePath, String dbName, boolean isReadOnly) throws RocksDBException {
        this.dbFilePath = FileUtil.endWith(db_FilePath) + dbName;
        Options options = new Options();
        options.setCreateIfMissing(true);//如果不存在就创建
        options.setKeepLogFileNum(1);//设置LOG.old保留个数
        if (isReadOnly) rocksDB = RocksDB.openReadOnly(options, dbFilePath);
        else rocksDB = RocksDB.open(options, dbFilePath);
    }

    public void printAllValue() {
        RocksIterator iter = rocksDB.newIterator();
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            logger.info("iter key : {}, iter value : {}", new String(iter.key()), new String(iter.value()));
        }
    }

    public long getCount() {
        long cnt = 0L;
        RocksIterator iter = rocksDB.newIterator();
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            cnt++;
        }
        return cnt;
    }

    public String getValue(String key, String charsetName) throws RocksDBException, UnsupportedEncodingException {
        return new String(rocksDB.get(key.getBytes()), charsetName);
    }

    public String getValue(String key) throws UnsupportedEncodingException, RocksDBException {
        return getValue(key, "UTF-8");
    }

    public void putValue(String key, String value) throws RocksDBException {
        rocksDB.put(key.getBytes(), value.getBytes());
    }

    public void flush() throws RocksDBException {
        FlushOptions fl = new FlushOptions();
        fl.setWaitForFlush(true);
        rocksDB.flush(fl);
    }

    public void delete(String key) throws RocksDBException {
        rocksDB.delete(key.getBytes());
    }

    /**
     * 扫描*.sst，并删除
     *
     * @throws RocksDBException
     */
    public void deleteFile() throws RocksDBException {
        for (String s : FileUtil.listFileEndWith(dbFilePath, ".sst")) {
            logger.info("deleteFile {}", s);
            rocksDB.deleteFile(s);
        }
    }

    public void release() {
        if (rocksDB != null) rocksDB.close();
        rocksDB = null;
    }
}
