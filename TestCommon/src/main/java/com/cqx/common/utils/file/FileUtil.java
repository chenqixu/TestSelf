package com.cqx.common.utils.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 文件工具
 *
 * @author chenqixu
 */
public class FileUtil {

    private static final String valueSplit = "\\|";
    private static final int BUFF_SIZE = 4096;
    private BufferedWriter writer;
    private BufferedReader reader;

    public static File[] listFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            return file.listFiles();
        }
        return null;
    }

    public static void mergeFile(List<String> srcList, String dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);
        for (String src : srcList) {
            File file = new File(src);
            if (file.exists() && file.isFile()) {
                InputStream in = new FileInputStream(file);
                copyBytes(in, out);
            }
        }
        closeStream(out);
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
                e.printStackTrace();
            }
        }
    }

    public static boolean del(String src) {
        return new File(src).delete();
    }

    public void getFile(String filename, String read_code)
            throws FileNotFoundException, UnsupportedEncodingException {
        File readFile = new File(filename);
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), read_code));
    }

    public List<String> read(String sql) {
        List<String> resultlist = new ArrayList<>();
        try {
            String _tmp;
            while ((_tmp = reader.readLine()) != null) {
                String[] values = _tmp.split(valueSplit);
                StringBuffer sb = new StringBuffer();
                sb.append(sql).append(" values(");
                for (int i = 0; i < values.length; i++) {
                    sb.append("'").append(values[i]).append("'");
                    if (i < values.length - 1) sb.append(",");
                }
                sb.append(")");
                resultlist.add(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultlist;
    }

    /**
     * 通过路径读取文件内容到List
     *
     * @param path
     * @param read_code
     * @return
     */
    public List<String> read(String path, String read_code) {
        File file = null;
        BufferedReader reader = null;
        List<String> sublist = new Vector<String>();
        try {
            file = new File(path);
            if (file.isFile()) {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), read_code));
                String _tmp = null;
                while ((_tmp = reader.readLine()) != null) {
                    sublist.add(_tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sublist;
    }

    public void createFile(String filename, String write_code)
            throws FileNotFoundException, UnsupportedEncodingException {
        File writeFile = new File(filename);
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeFile), write_code));
    }

    public void write(String str) {
        try {
            if (writer != null) {
                writer.write(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String s, int off, int len) {
        try {
            if (writer != null) {
                writer.write(s, off, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeRead() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeWrite() {
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
