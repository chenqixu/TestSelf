package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.MyRandomAccessFile;

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
    private Map<String, MyRandomAccessFile> idfileMap;

    public RafMap() {
        idfileMap = new HashMap<>();
    }

    public MyRandomAccessFile get(String key) throws FileNotFoundException {
        MyRandomAccessFile myRandomAccessFile = idfileMap.get(key);
        if (myRandomAccessFile == null) {
            myRandomAccessFile = new MyRandomAccessFile(key);
            put(key, myRandomAccessFile);
        }
        return myRandomAccessFile;
    }

    public MyRandomAccessFile get(String key, boolean isGetData) throws IOException {
        MyRandomAccessFile myRandomAccessFile = idfileMap.get(key);
        if (myRandomAccessFile == null) {
            myRandomAccessFile = new MyRandomAccessFile(key, isGetData);
            put(key, myRandomAccessFile);
        }
        return myRandomAccessFile;
    }

    public void put(String key, MyRandomAccessFile myRandomAccessFile) {
        idfileMap.put(key, myRandomAccessFile);
    }

    public void close() throws IOException {
        for (MyRandomAccessFile myRandomAccessFile : idfileMap.values()) {
            myRandomAccessFile.close();
        }
    }
}
