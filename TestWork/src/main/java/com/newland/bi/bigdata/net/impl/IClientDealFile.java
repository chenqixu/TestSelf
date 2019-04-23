package com.newland.bi.bigdata.net.impl;

import com.newland.bi.bigdata.net.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * IClientDealFile
 *
 * @author chenqixu
 */
public class IClientDealFile extends IClientDeal<File> {

    private static Logger logger = LoggerFactory.getLogger(IClientDealFile.class);
    private DataInputStream dis;
    private DataOutputStream dos;
    private String filePath = NetUtils.FILE_CACHE;
    private FileOutputStream fos;
    private FileInputStream fis;

    @Override
    public void newReader(InputStream is) throws Exception {
        dis = new DataInputStream(is);
    }

    @Override
    public void newWriter(OutputStream os) throws Exception {
        dos = new DataOutputStream(os);
    }

    @Override
    protected File read() throws Exception {
        try {
            // 文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            fos = new FileOutputStream(file);
            // 开始接收文件
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = dis.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            logger.info("======== 文件接收成功 [File Name：{}] [Size：{}] ========",
                    fileName, getFormatFileSize(fileLength));
        } finally {
            if (fos != null)
                fos.close();
        }
        return null;
    }

    @Override
    protected void write(File file) throws Exception {
        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                // 文件名和长度
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();
                // 开始传输文件
                logger.info("======== 开始传输文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    logger.info("| {} % |" + (100 * progress / file.length()));
                }
                logger.info("======== 文件传输成功 ========");
            }
        } finally {
            if (fis != null)
                fis.close();
        }
    }

    @Override
    protected void check(File o) throws Exception {
        throwNullException(dis, "Reader is null ! please newReader first !");
        throwNullException(dos, "Writer is null ! please newWriter first !");
        throwNullException(filePath, "filePath is null ! please init filePath first !");
    }

    @Override
    public void closeServer() {
        NetUtils.closeStream(dis);
        NetUtils.closeStream(dos);
    }

    @Override
    public void closeClient() {
        NetUtils.closeStream(dis);
        NetUtils.closeStream(dos);
    }

    /**
     * 格式化文件大小
     *
     * @param length
     * @return
     */
    private String getFormatFileSize(long length) {
        double size = ((double) length) / (1 << 30);
        if (size >= 1) {
            return NetUtils.df.format(size) + "GB";
        }
        size = ((double) length) / (1 << 20);
        if (size >= 1) {
            return NetUtils.df.format(size) + "MB";
        }
        size = ((double) length) / (1 << 10);
        if (size >= 1) {
            return NetUtils.df.format(size) + "KB";
        }
        return length + "B";
    }

}
