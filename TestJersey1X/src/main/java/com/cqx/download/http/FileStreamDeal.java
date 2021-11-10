package com.cqx.download.http;

import com.cqx.download.yaoqi.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * FileStreamDeal
 *
 * @author chenqixu
 */
public class FileStreamDeal implements StreamDeal {
    private static final Logger logger = LoggerFactory.getLogger(FileStreamDeal.class);
    private static String file_path;

    @Override
    public Object deal(InputStream inputStream, FileUtil fileUtil) throws IOException {
        FileOutputStream fos = null;
        try {
            //读取内容
            byte[] getData = readInputStream(inputStream);
            // 文件保存位置
            File saveDir = new File(getFile_path() + fileUtil.getTitle());
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            //输出流
            File file = new File(saveDir + File.separator + fileUtil.getIndexAndIncrease() + ".jpg");
            logger.debug("下载" + file.getPath());
            fos = new FileOutputStream(file);
            fos.write(getData);
        } finally {
            if (fos != null) fos.close();
        }
        return "ok";
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] b = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(b)) != -1) {
            bos.write(b, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public String getFile_path() {
        return file_path;
    }

    public static void setFile_path(String file_path) {
        FileStreamDeal.file_path = file_path;
    }
}
