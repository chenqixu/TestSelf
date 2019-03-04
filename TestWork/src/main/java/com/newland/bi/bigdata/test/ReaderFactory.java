package com.newland.bi.bigdata.test;

import java.util.HashMap;
import java.util.Map;

/**
 * ReaderFactory
 *
 * @author chenqixu
 */
public class ReaderFactory {
    private static Map<String, IReader> readerMap = new HashMap<>();

    private ReaderFactory() throws ClassNotFoundException {
        init();
    }

    public synchronized static void registerReader(String readerType, Class<? extends IReader> cls) throws IllegalAccessException, InstantiationException {
        readerMap.put(readerType, cls.newInstance());
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ReaderFactory.builder().getReaderMapSize();
    }

    public static ReaderFactory builder() throws ClassNotFoundException {
        return new ReaderFactory();
    }

    private void init() throws ClassNotFoundException {
        Class cls = Class.forName(TextReader.class.getName());
        System.out.println("init：" + cls);
    }

    public void getReaderMapSize() {
        System.out.println(readerMap.size());
    }
}
