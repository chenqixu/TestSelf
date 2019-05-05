package com.newland.bi.bigdata.utils.hadoop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * IOUtil
 *
 * @author chenqixu
 */
public class IOUtil {
    private static final int BUFF_SIZE = 4096;
    private static Logger logger = LoggerFactory.getLogger(IOUtil.class);

    public static void mergeFile(String srcPath, List<String> srcList, String dst) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            // 防止目录没有创建
            new File(new File(dst).getParent()).mkdirs();
            out = new FileOutputStream(dst);
            for (String src : srcList) {
                String absPath = srcPath + "/" + src;
                File file = new File(absPath);
                if (file.exists() && file.isFile()) {
                    in = new FileInputStream(file);
                    logger.debug("mergeFile：{} to：{}", absPath, dst);
                    copyBytes(in, out);
                }
            }
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    public static void copyBytes(InputStream in, OutputStream out) throws IOException {
        copyBytes(in, out, BUFF_SIZE, true);
    }

    public static void copyBytes(InputStream in, OutputStream out, int buffSize, boolean close) throws IOException {
        try {
            copyBytes(in, out, buffSize);
            if (close) {
                closeStream(in);
            }
        } catch (IOException e) {
            closeStream(in);
            closeStream(out);
            throw e;
        } finally {
            if (close) {
                closeStream(in);
            }
        }
    }

    public static void copyBytes(InputStream in, OutputStream out, int buffSize) throws IOException {
        byte[] buf = new byte[buffSize];

        for (int bytesRead = in.read(buf); bytesRead >= 0; bytesRead = in.read(buf)) {
            out.write(buf, 0, bytesRead);
        }
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static boolean del(String src) {
        return new File(src).delete();
    }

    public static long countFileRows(String fileName) {
        BufferedReader br = null;
        long count = 0;
        try {
            File file = new File(fileName);
            if (file.exists() && file.isFile()) {
                br = new BufferedReader(new FileReader(file));
                while (br.readLine() != null) {
                    count++;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return count;
    }
}
