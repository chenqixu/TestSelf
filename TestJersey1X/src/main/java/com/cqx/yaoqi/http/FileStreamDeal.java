package com.cqx.yaoqi.http;

import com.cqx.yaoqi.AppMain;
import com.cqx.yaoqi.FileUtil;

import java.io.*;

/**
 * FileStreamDeal
 *
 * @author chenqixu
 */
public class FileStreamDeal implements StreamDeal {

    @Override
    public Object deal(InputStream inputStream, FileUtil fileUtil) throws IOException {
        FileOutputStream fos = null;
        try {
            //读取内容
            byte[] getData = readInputStream(inputStream);
            // 文件保存位置
            File saveDir = new File(AppMain.FILE_PATH + fileUtil.getTitle());
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            //输出流
            File file = new File(saveDir + File.separator + fileUtil.getIndexAndIncrease() + ".jpg");
            System.out.println("下载" + file.getPath());
            fos = new FileOutputStream(file);
            fos.write(getData);
        } finally {
            if (fos != null) fos.close();
        }
        return "";
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
}
