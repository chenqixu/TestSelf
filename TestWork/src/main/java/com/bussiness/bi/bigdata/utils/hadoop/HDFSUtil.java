package com.bussiness.bi.bigdata.utils.hadoop;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * HDFSUtil
 *
 * @author chenqixu
 */
public class HDFSUtil {

    public static boolean isExist(FileSystem fs, String path) throws IOException {
        if (fs == null) throw new NullPointerException("FileSystem is null.");
        return fs.exists(new Path(path));
    }

    public static long getFileSize(FileSystem fs, String path) throws IOException {
        if (fs == null) throw new NullPointerException("FileSystem is null.");
        if (isExist(fs, path))
            return fs.getFileStatus(new Path(path)).getLen();
        return -1;
    }

    public static boolean delete(FileSystem fs, String path) throws IOException {
        if (fs != null) {
            // recursive表示是否递归
            return fs.delete(new Path(path), true);
        }
        return false;
    }
}
