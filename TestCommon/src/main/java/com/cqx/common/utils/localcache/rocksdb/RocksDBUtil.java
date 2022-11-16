package com.cqx.common.utils.localcache.rocksdb;

import com.cqx.common.utils.file.FileUtil;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocksDBUtil
 *
 * @author chenqixu
 */
public class RocksDBUtil implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(RocksDBUtil.class);
    private ConcurrentHashMap<String, ColumnFamilyHandle> columnFamilyHandleMap = new ConcurrentHashMap<>();

//    static {
//        // 不需要这个动作了，这个动作在Options、DBOptions、ColumnFamilyOptions构造的时候做了
//        //a static method that loads the RocksDB C++ library
//        RocksDB.loadLibrary();
//    }

    private String dbFilePath;
    private RocksDB rocksDB;
    private boolean hasColumnFamily;

    public RocksDBUtil(String db_FilePath, boolean isReadOnly) throws RocksDBException {
        this(db_FilePath, isReadOnly, RocksDBUtil.listColumnFamilies(db_FilePath));
        //==============================================
        // 区别在于这个构造支持merge方法，现在作废不用这种方式进行构造
        //==============================================
//        this.dbFilePath = db_FilePath;
//        Options options = new Options();
//        // 如果文件不存在就创建
//        options.setCreateIfMissing(true);
//        // 设置LOG.old保留个数
//        options.setKeepLogFileNum(1);
//        // 设置merge的操作类，多个值由逗号分隔
//        options.setMergeOperator(new StringAppendOperator());
//        if (isReadOnly) rocksDB = RocksDB.openReadOnly(options, dbFilePath);
//        else rocksDB = RocksDB.open(options, dbFilePath);
    }

    public RocksDBUtil(String db_FilePath) throws RocksDBException {
        this(db_FilePath, RocksDBUtil.listColumnFamilies(db_FilePath));
    }

    public RocksDBUtil(String db_FilePath, List<String> columnFamilyDescriptorList) throws RocksDBException {
        this(db_FilePath, false, columnFamilyDescriptorList);
    }

    public RocksDBUtil(String db_FilePath, boolean isReadOnly, List<String> columnFamilyDescriptorList) throws RocksDBException {
        this.dbFilePath = db_FilePath;
        this.hasColumnFamily = true;
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
            // 添加默认列族
            cfDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
            for (String _name : columnFamilyDescriptorList) {
                cfDescriptors.add(new ColumnFamilyDescriptor(_name.getBytes(), cfOpts));
            }

            // a list which will hold the handles for the column families once the db is opened
            final List<ColumnFamilyHandle> columnFamilyHandleList =
                    new ArrayList<>();

            DBOptions dbOptions = new DBOptions();
            // 如果文件不存在就创建
            dbOptions.setCreateIfMissing(true);
            // 如果列族不存在就创建
            dbOptions.setCreateMissingColumnFamilies(true);
            // 设置LOG.old保留个数
            dbOptions.setKeepLogFileNum(1);
            if (isReadOnly) {
                rocksDB = RocksDB.openReadOnly(dbOptions, dbFilePath, cfDescriptors, columnFamilyHandleList);
            } else {
                rocksDB = RocksDB.open(dbOptions, dbFilePath, cfDescriptors, columnFamilyHandleList);
            }
            for (ColumnFamilyHandle _columnFamilyHandle : columnFamilyHandleList) {
                columnFamilyHandleMap.put(new String(_columnFamilyHandle.getName()), _columnFamilyHandle);
            }
        }
    }

    /**
     * 获取所有的列族
     *
     * @param db_FilePath
     * @return
     * @throws RocksDBException
     */
    public static List<String> listColumnFamilies(String db_FilePath) throws RocksDBException {
        List<String> _columnFamilies = new ArrayList<>();
        try (final Options options = new Options()) {
            List<byte[]> columnFamilies = RocksDB.listColumnFamilies(options, db_FilePath);

            for (byte[] bytes : columnFamilies) {
                _columnFamilies.add(new String(bytes));
            }
        }
        return _columnFamilies;
    }

    public void createColumnFamilyHandle(String columnFamilyName) throws RocksDBException {
        if (getColumnFamilyHandleByName(columnFamilyName) == null) {
            try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
                ColumnFamilyHandle _columnFamilyHandle = rocksDB.createColumnFamily(new ColumnFamilyDescriptor(columnFamilyName.getBytes(), cfOpts));
                columnFamilyHandleMap.put(new String(_columnFamilyHandle.getName()), _columnFamilyHandle);
            }
        }
    }

    private ColumnFamilyHandle getColumnFamilyHandleByName(String columnFamilyName) {
        return columnFamilyHandleMap.get(columnFamilyName);
    }

    public void putColumnFamilyValue(String columnFamilyHandleName, String key, String value) throws RocksDBException {
        rocksDB.put(checkColumnFamily(columnFamilyHandleName), key.getBytes(), value.getBytes());
    }

    public String getColumnFamilyValue(String columnFamilyHandleName, String key, String charsetName) throws RocksDBException, UnsupportedEncodingException {
        byte[] values = rocksDB.get(checkColumnFamily(columnFamilyHandleName), key.getBytes());
        if (values != null && values.length > 0) {
            return new String(values, charsetName);
        }
        return null;
    }

    public String getColumnFamilyValue(String columnFamilyHandleName, String key) throws RocksDBException, UnsupportedEncodingException {
        return getColumnFamilyValue(columnFamilyHandleName, key, "UTF-8");
    }

    public void printColumnFamilyAllValue(String columnFamilyHandleName) throws RocksDBException {
        RocksIterator iter = rocksDB.newIterator(checkColumnFamily(columnFamilyHandleName));
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            logger.info("[ColumnFamily : {}] iter key : {}, iter value : {}", columnFamilyHandleName, new String(iter.key()), new String(iter.value()));
        }
    }

    /**
     * 获取对应列族下的所有值，返回一个Map对象<br/>
     * 注意：值太多内存可能会爆
     *
     * @param columnFamilyHandleName
     * @return
     * @throws RocksDBException
     */
    public Map<String, String> getColumnFamilyAllValue(String columnFamilyHandleName) throws RocksDBException {
        Map<String, String> values = new HashMap<>();
        RocksIterator iter = rocksDB.newIterator(checkColumnFamily(columnFamilyHandleName));
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            values.put(new String(iter.key()), new String(iter.value()));
        }
        return values;
    }

    public void printAllColumnFamilyAllValue() throws RocksDBException {
        for (ColumnFamilyHandle _columnFamilyHandle : columnFamilyHandleMap.values()) {
            printColumnFamilyAllValue(new String(_columnFamilyHandle.getName()));
        }
    }

    public void dropColumnFamily(String columnFamilyHandleName) throws RocksDBException {
        ColumnFamilyHandle _columnFamilyHandle = checkColumnFamily(columnFamilyHandleName);
        rocksDB.dropColumnFamily(_columnFamilyHandle);
        _columnFamilyHandle.close();
        columnFamilyHandleMap.remove(columnFamilyHandleName);
    }

    public void dropAllColumnFamily() throws RocksDBException {
        // default column family 不能被drop
        for (Map.Entry<String, ColumnFamilyHandle> entry : columnFamilyHandleMap.entrySet()) {
            if (!entry.getKey().contains(new String(RocksDB.DEFAULT_COLUMN_FAMILY))) {
                dropColumnFamily(entry.getKey());
            }
        }
    }

    /**
     * 打印默认的列族的所有值
     */
    public void printAllValue() {
        RocksIterator iter = rocksDB.newIterator();
        for (iter.seekToFirst(); iter.isValid(); iter.next()) {
            logger.info("iter key : {}, iter value : {}", new String(iter.key()), new String(iter.value()));
        }
    }

    public long getCount() {
        long cnt = 0L;
        RocksIterator iter = rocksDB.newIterator();
        for (iter.seekToFirst(); iter.isValid(); iter.next()) cnt++;
        return cnt;
    }

    public String getLastKey() {
        RocksIterator iter = rocksDB.newIterator();
        iter.seekToLast();
        if (iter.isValid()) return new String(iter.key());
        else return null;
    }

    public String getLastValue() {
        RocksIterator iter = rocksDB.newIterator();
        iter.seekToLast();
        if (iter.isValid()) return new String(iter.value());
        else return null;
    }

    public String getValue(String key, String charsetName) throws RocksDBException, UnsupportedEncodingException {
        byte[] bytes = rocksDB.get(key.getBytes());
        if (bytes != null && bytes.length > 0) {
            return new String(rocksDB.get(key.getBytes()), charsetName);
        }
        return null;
    }

    public String getValue(String key) throws UnsupportedEncodingException, RocksDBException {
        return getValue(key, "UTF-8");
    }

    public void putValue(String key, String value) throws RocksDBException {
        rocksDB.put(key.getBytes(), value.getBytes());
    }

    public boolean keyMayExist(String key) {
        return rocksDB.keyMayExist(key.getBytes(), null);
    }

    public void merge(String key, String value) throws RocksDBException {
        rocksDB.merge(key.getBytes(), value.getBytes());
    }

    public List<byte[]> multiGetAsList(List<byte[]> keys) throws RocksDBException {
        return rocksDB.multiGetAsList(keys);
    }

    public void flush() throws RocksDBException {
        FlushOptions fl = new FlushOptions();
        fl.setWaitForFlush(true);
        rocksDB.flush(fl);
    }

    public void flushColumnFamily(String columnFamilyHandleName) throws RocksDBException {
        FlushOptions fl = new FlushOptions();
        fl.setWaitForFlush(true);
        rocksDB.flush(fl, checkColumnFamily(columnFamilyHandleName));
    }

    public void flushWal() throws RocksDBException {
        rocksDB.flushWal(true);
    }

    public void delete(String key) throws RocksDBException {
        rocksDB.delete(key.getBytes());
    }

    public void deleteColumnFamily(String columnFamilyHandleName, String key) throws RocksDBException {
        rocksDB.delete(checkColumnFamily(columnFamilyHandleName), key.getBytes());
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

    /**
     * 检查列族
     *
     * @param columnFamilyHandleName 列族名称
     * @return
     */
    public ColumnFamilyHandle checkColumnFamily(String columnFamilyHandleName) throws RocksDBException {
        ColumnFamilyHandle columnFamilyHandle = getColumnFamilyHandleByName(columnFamilyHandleName);
        if (columnFamilyHandle != null) {
            return columnFamilyHandle;
        } else {
            throw new RocksDBException(String.format("列族[%s]不存在!", columnFamilyHandleName));
        }
    }

    public boolean isHasColumnFamily() {
        return hasColumnFamily;
    }

    /**
     * 资源释放
     */
    public void release() {
        if (columnFamilyHandleMap != null && columnFamilyHandleMap.size() > 0) {
            for (ColumnFamilyHandle _columnFamilyHandle : columnFamilyHandleMap.values()) {
                _columnFamilyHandle.close();
            }
            columnFamilyHandleMap = null;
        }
        if (rocksDB != null) rocksDB.close();
        rocksDB = null;
    }

    @Override
    public void close() throws Exception {
        release();
    }
}
