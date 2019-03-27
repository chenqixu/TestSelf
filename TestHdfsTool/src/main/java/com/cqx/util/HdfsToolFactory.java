package com.cqx.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HdfsToolFactory
 *
 * @author chenqixu
 */
public class HdfsToolFactory {

    private static final Logger logger = LoggerFactory.getLogger(HdfsToolFactory.class);
    private FileSystem fs;
    private Configuration configuration;
    private long cachefilelen = 0l;
    private FSDataOutputStream fsDataOutputStream;
    private String writePath;

    private HdfsToolFactory() throws IOException {
        init();
    }

    public static HdfsToolFactory builder() throws IOException {
        return new HdfsToolFactory();
    }

    /**
     * hdfs上获得文件大小
     *
     * @param path
     * @return
     */
    public long getFileSize(String path) {
        check();
        logger.info("getFileSize：{}", path);
        try {
            return getFileInfo(path).getLen();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        }
    }

    private void init() throws IOException {
        configuration = new Configuration();
        String path = "D:\\tmp\\etc\\hadoop\\conf\\";
        configuration.addResource(new Path(path + "core-site.xml"));
        configuration.addResource(new Path(path + "hdfs-site.xml"));
        configuration.addResource(new Path(path + "mapred-site.xml"));
        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        logger.info("hadoopConfig：{}", configuration);
        fs = FileSystem.newInstance(configuration);
        logger.info("FileSystem：{}", fs);
    }

    public void close() {
        if (fs != null) {
            try {
                logger.info("closeFileSystem-begin：{}", fs);
                fs.close();
                logger.info("closeFileSystem-end：{}", fs);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void finalize() {
        close();
    }

    /**
     * 文件或路径是否存在
     *
     * @param path
     * @return
     * @throws IOException
     */
    public boolean isExist(String path) throws IOException {
        check();
        logger.info("isExist：{}", path);
        return fs.exists(new Path(path));
    }

    public boolean isFile(String path) throws IOException {
        check();
        logger.info("isFile：{}", path);
        return fs.isFile(new Path(path));
    }

    public FSDataOutputStream createFile(String path) throws IOException {
        check();
        logger.info("createFile：{}", path);
        if (!isExist(path)) {
            return fs.create(new Path(path));
        } else {
            throw new FileAlreadyExistsException(path + " exist!");
        }
    }

    public FSDataOutputStream appendFile(String path) throws IOException {
        check();
        logger.info("appendFile：{}", path);
        if (isExist(path)) {
            return fs.append(new Path(path));
        } else {
            return createFile(path);
        }
    }

    /**
     * 获取文件状态
     *
     * @param path
     * @return
     * @throws IOException
     */
    public FileStatus getFileInfo(String path) throws IOException {
        check();
        logger.info("getFileInfo：{}", path);
        if (isExist(path) && isFile(path)) {
            return fs.getFileStatus(new Path(path));
        } else {
            throw new NullPointerException(path + " not exist or not file!");
        }
    }

    public void ls(String path) throws IOException {
        check();
        logger.info("ls：{}", path);
        for (FileStatus fileStatus : fs.listStatus(new Path(path))) {
            logger.info("{} {} {} {} {}", fileStatus.getPermission(), fileStatus.getOwner(),
                    fileStatus.getGroup(), fileStatus.getLen(), fileStatus.getPath());
        }
    }

    public boolean mkdir(String path) throws IOException {
        check();
        if (!isExist(path)) {
            logger.info("mkdir：{}", path);
            return fs.mkdirs(new Path(path));
        }
        return false;
    }

    public FSDataInputStream openFile(String path) throws IOException {
        check();
        if (isExist(path)) {
            logger.info("openFile：{}", path);
            return fs.open(new Path(path));
        }
        return null;
    }

    public void copyBytes(String inputFile, String outputFile) throws IOException {
        copyBytesSkipBegin(inputFile, outputFile, 0);
    }

    public void copyBytesSkipBegin(String inputFile, String outputFile, long count) throws IOException {
        check();
        if (isExist(inputFile)) {
            logger.info("copyBytes：{} to {}.", inputFile, outputFile);
            delete(outputFile);
            InputStream in = HdfsTool.openFile(fs, inputFile);
            OutputStream out = HdfsTool.createFile(fs, outputFile);
            if (count > 0)
                IOUtils.skipFully(in, count);
            IOUtils.copyBytes(in, out, fs.getConf());
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
            logger.info("copyBytes closeStream.");
        }
    }

    public void copyBytesSkipEnd(String inputFile, String outputFile, long count) throws IOException {
        check();
        if (isExist(inputFile)) {
            logger.info("copyBytesSkipEnd：{} to {}，skipEnd：{}.", inputFile, outputFile, count);
            delete(outputFile);
            InputStream in = HdfsTool.openFile(fs, inputFile);
            OutputStream out = HdfsTool.createFile(fs, outputFile);
            IOUtils.copyBytes(in, out, count, true);
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
            logger.info("copyBytesSkipEnd closeStream.");
        }
    }

    public boolean delete(String path) throws IOException {
        check();
        logger.info("delete：{}", path);
        return fs.delete(new Path(path), true);
//        return fs.deleteOnExit(new Path(path));
    }

    public boolean rename(String source, String dist) throws IOException {
        check();
        boolean flag = false;
        logger.info("rename：{} {}", source, dist);
        if (isExist(source) && !isExist(dist)) {
            flag = fs.rename(new Path(source), new Path(dist));
        } else {
            logger.warn("source is not exist ({}) or dist is exist ({}).", isExist(source), isExist(dist));
        }
        return flag;
    }

    private void check() {
        if (fs == null)
            throw new NullPointerException("fs is null!");
    }

    private void checkWrite() {
        if (fsDataOutputStream == null || writePath == null || writePath.length() == 0)
            throw new NullPointerException("fsDataOutputStream is null!");
    }

    private boolean checkFile(byte[] value, long checklen) {
        return value.length == checklen;
    }

    private boolean checkHdfsFile() {
        return checkHdfsFile(0);
    }

    private boolean checkHdfsFile(long filesize) {
        long cachefilelen = this.cachefilelen + filesize;
        logger.info("cachefilelen：{}", cachefilelen);
        // get file last len
        long filelastlen = getFileSize(writePath);
        // Determine whether the final length subtracted from the cache is not equal to the check length
        boolean flag = ((cachefilelen - filelastlen) != 0);
        if (flag)
            logger.info("rollback，because (cachefilelen - filelastlen) {} != 0 .", cachefilelen - filelastlen);
        return flag;
    }

    public void startWrite(FSDataOutputStream fsDataOutputStream, String writePath) {
        this.fsDataOutputStream = fsDataOutputStream;
        this.writePath = writePath;
        this.cachefilelen = getFileSize(writePath);
    }

    public void write(byte[] value) throws IOException {
        check();
        checkWrite();
        fsDataOutputStream.write(value);
        fsDataOutputStream.flush();
        fsDataOutputStream.hsync();
    }

    public void write(byte[] value, long checklen) throws IOException {
        check();
        checkWrite();
        boolean delete = true;
        boolean rename = true;
        // Determine whether the final length subtracted from the cache is not equal to the check length
        if (checkHdfsFile()) {
            // rollback
            // first : iocopy tmp tmp1 by cachefilelen
            copyBytesSkipEnd(writePath, writePath + "1", cachefilelen);
            // second : rm tmp
            delete = delete(writePath);
            // third : mv tmp1 tmp
            rename = rename(writePath + "1", writePath);
        }
        logger.info("delete：{}，rename：{}", delete, rename);
        if (delete && rename) {
            if (checkFile(value, checklen)) {// 校验
                write(value);// 这里异常
                if (!checkHdfsFile(value.length)) {// 写完再次校验
                    // 成功，修改cachefilelen
                    cachefilelen = cachefilelen + checklen;
                    logger.info("success，update cachefilelen：{}", cachefilelen);
                } else {
                    // 校验不过，抛出异常？
                    logger.error("write hdfs is fail，throw Exception.");
                }
            } else {
                // 校验不过，抛出异常？
                logger.error("check is fail，throw Exception.");
            }
        }
    }

    public void closeStream(Closeable closeable) {
        if (closeable != null) {
            logger.info("closeStream：{}", closeable);
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
