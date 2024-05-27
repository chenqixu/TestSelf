package com.cqx.algorithm.bitmap;

import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.io.*;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * RoaringBitmapUv
 *
 * @author chenqixu
 */
public class RoaringBitmapUv implements BitmapUv {

    private Roaring64NavigableMap all = new Roaring64NavigableMap();

    private ReadWriteLock rwlock = new ReentrantReadWriteLock();

    private Lock rlock = rwlock.readLock();
    private Lock wlock = rwlock.writeLock();

    RoaringBitmapUv() {
    }

    @Override
    public BitmapUv add(Long data) {
        try {
            wlock.lock();
            all.add(data);
            return this;
        } finally {
            wlock.unlock();
        }
    }


    public boolean contains(long d) {
        try {
            rlock.lock();
            return all.contains(d);
        } finally {
            rlock.unlock();
        }
    }

    @Override
    public long cardinality() {
        try {
            rlock.lock();
            return all.getLongCardinality();
        } finally {
            rlock.unlock();
        }
    }


    @Override
    public BitmapUv or(BitmapUv bitmapUv) {
        try {
            wlock.lock();
            if (bitmapUv instanceof RoaringBitmapUv) {
                all.or(((RoaringBitmapUv) bitmapUv).all);

            } else {
                throw new RuntimeException();
            }
            return this;
        } finally {
            wlock.unlock();
        }
    }

    public boolean runOptimize() {
        try {
            wlock.lock();
            return all.runOptimize();
        } finally {
            wlock.unlock();
        }
    }

    /**
     * 将bitmap对象转成bitmap文件
     */
    @Override
    public void serToFile(File file) throws IOException {
        try {
            wlock.lock();
            try (FileOutputStream fos = new FileOutputStream(file);) {
                byte[] bs = serToBytes();
                fos.write(bs);
            }
        } finally {
            wlock.unlock();
        }

    }

    /**
     * 将bitmap文件转换成bitmap对象
     */

    @Override
    public void deserFromFile(File file) throws IOException {
        try {
            wlock.lock();
            try (FileInputStream fis = new FileInputStream(file);) {
                byte[] bs = new byte[fis.available()];
                fis.read(bs);
                deserFromBytes(bs);
            }

        } finally {
            wlock.unlock();
        }
    }


    @Override
    public void deserFromBytes(byte[] bs) throws IOException {
        try {
            wlock.lock();
            all.deserialize(new DataInputStream(new ByteArrayInputStream(bs)));
        } finally {
            wlock.unlock();
        }
    }


    @Override
    public byte[] serToBytes() throws IOException {
        try {
            wlock.lock();
            all.runOptimize();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            all.serialize(new DataOutputStream(baos));
            return baos.toByteArray();
        } finally {
            wlock.unlock();
        }
    }


    @Override
    public Iterator<Long> iterator() {
        return all.iterator();
    }


    @Override
    public BitmapUv and(BitmapUv bitmapUv) {
        try {
            wlock.lock();
            if (bitmapUv instanceof RoaringBitmapUv) {
                all.and(((RoaringBitmapUv) bitmapUv).all);
            } else {
                throw new RuntimeException();
            }
            return this;
        } finally {
            wlock.unlock();
        }
    }


    @Override
    public BitmapUv clear() {
        all.clear();
        return this;
    }


    @Override
    public BitmapUv andNot(BitmapUv bitmapUv) {
        try {
            wlock.lock();
            if (bitmapUv instanceof RoaringBitmapUv) {
                all.andNot(((RoaringBitmapUv) bitmapUv).all);
            } else {
                throw new RuntimeException();
            }
            return this;
        } finally {
            wlock.unlock();
        }
    }

    @Override
    public boolean contains(Long a) {
        return all.contains(a);
    }
}
