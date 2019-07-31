package com.newland.bi.bigdata.changecode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private BufferedWriter writer;
    private BufferedReader reader;

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

    public void write(String msg) {
        try {
            if (writer != null) {
                writer.write(msg);
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

    public static String[] listFile(String path, final String keyword) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            if (keyword != null && keyword.length() > 0) {
                logger.debug("listFile use keyword：{}.", keyword);
                return file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.contains(keyword);
                    }
                });
            } else {
                logger.debug("listFile not use keyword.");
                return file.list();
            }
        } else {
            logger.warn("path：{}，file not exists：{} or file is not Directory：{}", path, file.exists(), file.isDirectory());
        }
        return new String[0];
    }
}
