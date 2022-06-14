package com.bussiness.bi.bigdata.memory;

import com.cqx.common.utils.file.BaseRandomAccessFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * RafMap
 *
 * @author chenqixu
 */
public class RafMap {
    // 索引文件引用缓存，不占资源
    private Map<String, BaseRandomAccessFile> idfileMap;

    public RafMap() {
        idfileMap = new HashMap<>();
    }

    public BaseRandomAccessFile get(String key) throws FileNotFoundException {
        BaseRandomAccessFile myRandomAccessFile = idfileMap.get(key);
        if (myRandomAccessFile == null) {
            myRandomAccessFile = new BaseRandomAccessFile(key);
            put(key, myRandomAccessFile);
        }
        return myRandomAccessFile;
    }

    public BaseRandomAccessFile get(String key, boolean isGetData) throws IOException {
        BaseRandomAccessFile myRandomAccessFile = idfileMap.get(key);
        if (myRandomAccessFile == null) {
            myRandomAccessFile = new BaseRandomAccessFile(key, isGetData);
            put(key, myRandomAccessFile);
        }
        return myRandomAccessFile;
    }

    public void put(String key, BaseRandomAccessFile myRandomAccessFile) {
        idfileMap.put(key, myRandomAccessFile);
    }

    public void close() throws IOException {
        for (BaseRandomAccessFile myRandomAccessFile : idfileMap.values()) {
            myRandomAccessFile.close();
        }
    }
}
