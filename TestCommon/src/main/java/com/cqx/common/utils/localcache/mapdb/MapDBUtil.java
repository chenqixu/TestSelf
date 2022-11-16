package com.cqx.common.utils.localcache.mapdb;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * MapDBUtil
 *
 * @author chenqixu
 */
public class MapDBUtil implements Closeable {
    private DB db;
    private ConcurrentMap map;

    public MapDBUtil(String fileName) {
        db = DBMaker.fileDB(fileName).make();
        map = db.hashMap("map").createOrOpen();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key).toString();
    }

    public Map<String, String> getMap(String mapName) {
        return db.get(mapName);
    }

    @Override
    public void close() throws IOException {
        if (db != null) db.close();
    }
}
