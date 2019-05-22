package com.newland.bi.bigdata.file;

import com.newland.bi.bigdata.bean.CheckFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * FileLocalBack
 *
 * @author chenqixu
 */
public class FileLocalBack {
    public static final String newLine = System.getProperty("line.separator");
    public static final String fileSparator = File.separator;
    public static final String writeCode = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(FileLocalBack.class);
    private String finalFileName;
    private File localBack;
    private BufferedWriter bw;
    private int rowNumber = 0;

    public FileLocalBack(String fileName, String localBackPath) {
        if (localBackPath.endsWith(fileSparator)) finalFileName = localBackPath + fileName;
        else finalFileName = localBackPath + fileSparator + fileName;
    }

    public void start() throws FileNotFoundException, UnsupportedEncodingException {
        localBack = new File(finalFileName);
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localBack), writeCode));
    }

    public void write(String content) {
        if (bw != null) {
            try {
                bw.write(content + newLine);
                rowNumber++;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                //todo Exception Deal ???
//                throw e;
            }
        }
    }

    public boolean check(int tuplesSize, CheckFile checkFile) {
        if (tuplesSize != checkFile.getRowNumber()) {
            logger.warn("check is not ok！tuplesSize：{}，checkFile：{}", tuplesSize, checkFile.toJson());
            return false;
        }
        return true;
    }

    public boolean check(CheckFile checkFile) {
        return check(rowNumber, checkFile);
    }

    public long getSize() throws IOException {
        if (bw != null) {
            try {
                bw.flush();
            } catch (IOException e) {
                if (!e.getMessage().equals("Stream closed")) throw e;
            }
        }
        return localBack.length();
    }

    public void close() {
        if (bw != null) {
            try {
                bw.close();
                bw = null;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
