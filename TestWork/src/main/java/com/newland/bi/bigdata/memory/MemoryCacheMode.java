package com.newland.bi.bigdata.memory;

import java.nio.channels.FileChannel;

/**
 * MemoryCacheMode
 *
 * @author chenqixu
 */
public enum MemoryCacheMode {
    READ_ONLY("r", FileChannel.MapMode.READ_ONLY),
    READ_WRITE("rw", FileChannel.MapMode.READ_WRITE);

    private final String code;
    private final FileChannel.MapMode mapMode;

    private MemoryCacheMode(String code, FileChannel.MapMode mapMode) {
        this.code = code;
        this.mapMode = mapMode;
    }

    public String getCode() {
        return this.code;
    }

    public FileChannel.MapMode getMapMode() {
        return this.mapMode;
    }
}
