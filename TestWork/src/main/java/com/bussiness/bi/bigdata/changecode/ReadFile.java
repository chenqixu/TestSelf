package com.bussiness.bi.bigdata.changecode;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadFile {

    private static MyLogger logger = MyLoggerFactory.getLogger(ReadFile.class);
    private ChangeCode cc = null;

    public ReadFile() {
        cc = new ChangeCode();
    }

    public static void main(String[] args) throws Exception {
        ReadFile readFile = new ReadFile();
        //===============================
        //获取文件编码
//        String[] charsetpath_arr = {
//                "D:/Document/Workspaces/Git/TestSelf/TestSpring/src/main/java/com/spring/printSystemProperties/servlet/GetDBConnServlet.java",
//                "D:/Document/Workspaces/Git/TestSelf/TestWork/src/main/java/com/newland/bi/bigdata/changecode/ReadFile.java",
//                "D:/Document/Workspaces/Git/TestSelf/TestFrameForm/src/main/java/mainForm.java",
//                "D:\\Document\\Workspaces\\Git\\TestSelf\\TestMR\\src\\main\\java\\com\\main\\MRSearchMain.java"
//        };
//        for (String charsetpath : charsetpath_arr) {
//            readFile.getCharset(charsetpath);
//        }
        //===============================
        //获取扫描的文件，进行编码转换
//        String scanPath = "D:\\Document\\Workspaces\\Git\\ProjectAcc\\ProjectAccSvc\\src\\main\\java";
//        scanPath = "D:\\Document\\Workspaces\\Git\\TestGame\\Tetris\\src\\main\\java";
//        String scanRule = ".*\\.java";
//        String readCode = "GBK";
//        String writeCode = "UTF-8";
//        readFile.getAllFileAndChangeCode(scanPath, scanRule, readCode, writeCode);
        //===============================
        //强制修改文件编码，从GBK改成UTF-8
//        String[] path_arr = {
//                "D:\\Document\\Workspaces\\Git\\TestGame\\Tetris\\src\\main\\java\\com\\cqx\\tetris\\block\\OBlock.java",
//                "D:\\Document\\Workspaces\\Git\\TestGame\\Tetris\\src\\main\\java\\com\\cqx\\tetris\\block\\TBlock.java",
//                "D:\\Document\\Workspaces\\Git\\TestGame\\Tetris\\src\\main\\java\\com\\cqx\\tetris\\block\\Z1Block.java",
//                "D:\\Document\\Workspaces\\Git\\TestGame\\Tetris\\src\\main\\java\\com\\cqx\\tetris\\block\\ZBlock.java"
//        };
//        for (String path : path_arr) {
//            readFile.changeFileCodeFormGBKToUTF8Force(path);
//        }
    }

    /**
     * 获取文件编码
     *
     * @param path
     * @param defaultCode
     * @return
     */
    public String getCharset(String path, String defaultCode) {
        InputStream is = null;
        UniversalDetector detector = new UniversalDetector(null);
        try {
            is = new FileInputStream(path);
            byte[] bytes = new byte[1024];
            int nread;
            if ((nread = is.read(bytes)) > 0 && !detector.isDone()) {
                detector.handleData(bytes, 0, nread);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                is = null;
            }
        }
        detector.dataEnd();
        String encode = detector.getDetectedCharset();
        /** default UNKNOW */
        if (StringUtils.isEmpty(encode)) {
            if (StringUtils.isEmpty(defaultCode)) {
                encode = "UNKNOW";
            } else {
                encode = defaultCode;
            }
        }
        detector.reset();
        logger.debug("path：{}，encode：{} ", path, encode);
        return encode;
    }

    /**
     * 获取文件编码，默认GBK
     *
     * @param path
     * @return
     */
    public String getCharset(String path) {
        return getCharset(path, "GBK");
    }

    /**
     * 修改文件编码，从sourceCode改成dstCode
     *
     * @param path
     * @param sourceCode
     * @param dstCode
     */
    public void changeFileCode(String path, String sourceCode, String dstCode) {
        String encode = getCharset(path);
        if (encode.equals(sourceCode))
            cc.change(path, sourceCode, dstCode);
    }

    /**
     * 强制修改文件编码，从sourceCode改成dstCode
     *
     * @param path
     * @param sourceCode
     * @param dstCode
     */
    public void changeFileCodeForce(String path, String sourceCode, String dstCode) {
        cc.change(path, sourceCode, dstCode);
    }

    /**
     * 修改文件编码，从GBK改成UTF-8
     *
     * @param path
     */
    public void changeFileCodeFormGBKToUTF8(String path) {
        changeFileCode(path, "GBK", "UTF-8");
    }

    /**
     * 强制修改文件编码，从GBK改成UTF-8
     *
     * @param path
     */
    public void changeFileCodeFormGBKToUTF8Force(String path) {
        changeFileCodeForce(path, "GBK", "UTF-8");
    }

    /**
     * 获取扫描的文件，进行编码转换
     *
     * @param scanPath
     * @param scanRule
     * @param readCode
     * @param writeCode
     */
    public void getAllFileAndChangeCode(String scanPath, String scanRule, String readCode, String writeCode) {
        cc.setScan_path(scanPath);
        cc.setScan_rule(scanRule);
        cc.setRead_code(readCode);
        cc.setWrite_code(writeCode);
        for (String path : cc.scan()) {
            String encode = getCharset(path);
            if (encode.startsWith("GB")) {
                logger.info("path：{}，encode：{} ", path, encode);
                cc.change(path, encode, "UTF-8");
            } else if (encode.startsWith("UTF-8")) {
                logger.info("path：{}，encode：{} ", path, encode);
            } else if (encode.startsWith("WINDOWS-1252")) {
                logger.info("path：{}，encode：{} ", path, encode);
                cc.change(path, "GBK", "UTF-8");
            } else {
                logger.info("path：{}，encode：{} ", path, encode);
            }
        }
    }
}
