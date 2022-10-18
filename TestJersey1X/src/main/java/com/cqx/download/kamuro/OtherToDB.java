package com.cqx.download.kamuro;

import com.cqx.common.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * YaoqiToDB
 *
 * @author chenqixu
 */
public class OtherToDB {
    private static final Logger logger = LoggerFactory.getLogger(OtherToDB.class);
    private FileUtil fileUtil = new FileUtil();

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        if (args.length == 0) {
            logger.warn("需要传入目录名参数！如：akisora 或者 yaoqi");
            System.exit(-1);
        } else if (args.length == 1) {
            String tag = args[0];
            logger.info("tag：{}", tag);
            new OtherToDB().createReadMe(String.format("Z:\\Reader\\web\\res\\comic\\kamuro\\%s\\", tag));
        }
    }

    /**
     * 创建readme.txt文件
     *
     * @param path
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private void createReadMe(String path) throws FileNotFoundException, UnsupportedEncodingException {
        for (String name : FileUtil.listFile(path)) {
            logger.info("{}", name);
            fileUtil.createFile(path + name + "\\readme.txt");
            fileUtil.write(name);
            fileUtil.closeWrite();
        }
    }

    /**
     * 文件夹重命名
     *
     * @param path
     */
    private void rename(String path) {
        for (String name : FileUtil.listFile(path)) {
            if (!name.contains("【")) {
                logger.info("{}", name);
                String key = "其他";
                if (name.startsWith(key)) {
                    logger.info("=={}", name.replace(key, "【" + key + "】"));
//                    new File(path + name).renameTo(
//                            new File(path + name.replace(key, "【" + key + "】")));
                } else {
                    logger.info("---{}", "【" + key + "】" + name);
//                    new File(path + name).renameTo(
//                            new File(path + "【" + key + "】" + name));
                }
            }
        }
    }
}
