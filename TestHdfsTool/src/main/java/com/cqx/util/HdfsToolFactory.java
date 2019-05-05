package com.cqx.util;

import com.cqx.common.utils.file.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HdfsToolFactory
 *
 * @author chenqixu
 */
public class HdfsToolFactory {

    private static final Logger logger = LoggerFactory.getLogger(HdfsToolFactory.class);
    private FileSystem fs;
    private FileSystem localFileSystem;
    private Configuration configuration;
    private long cachefilelen = 0l;
    private FSDataOutputStream fsDataOutputStream;
    private String writePath;
    private com.cqx.common.utils.file.FileUtil fileUtil;

    private HdfsToolFactory() throws IOException {
        init("D:\\tmp\\etc\\hadoop\\conf\\");
    }

    private HdfsToolFactory(String conf) throws IOException {
        init(conf);
    }

    public static HdfsToolFactory builder() throws IOException {
        return new HdfsToolFactory();
    }

    public static HdfsToolFactory builder(String conf) throws IOException {
        return new HdfsToolFactory(conf);
    }

    /**
     * hdfs上获得文件大小
     *
     * @param path
     * @return
     */
    public long getFileSize(String path) throws Exception {
        check();
        logger.info("getFileSize：{}", path);
        try {
            return getFileInfo(path).getLen();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        }
    }

    private void init(String conf_path) throws IOException {
        configuration = new Configuration();
        if (!conf_path.endsWith("/")) conf_path = conf_path + "/";
        configuration.addResource(new Path(conf_path + "core-site.xml"));
        configuration.addResource(new Path(conf_path + "hdfs-site.xml"));
        configuration.addResource(new Path(conf_path + "mapred-site.xml"));
        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        logger.info("hadoopConfig：{}", configuration);
        fs = FileSystem.newInstance(configuration);
        logger.info("FileSystem：{}", fs);
        localFileSystem = FileSystem.newInstanceLocal(new Configuration());
        logger.info("localFileSystem：{}", localFileSystem);
    }

    public void close() {
        if (fs != null) {
            try {
//                logger.info("closeFileSystem-begin：{}", fs);
                fs.close();
                logger.info("closeFileSystem-end：{}", fs);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (localFileSystem != null) {
            try {
//                logger.info("closelocalFileSystem-begin：{}", localFileSystem);
                localFileSystem.close();
                logger.info("closelocalFileSystem-end：{}", localFileSystem);
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
    public boolean isExist(String path) throws Exception {
        return isExist(fs, path);
    }

    public boolean isExist(FileSystem fs, String path) throws Exception {
        check(fs);
        logger.info("isExist：{}", path);
        return HdfsTool.isExist(fs, path);
    }

    public boolean isFile(String path) throws IOException {
        check();
        logger.info("isFile：{}", path);
        return fs.isFile(new Path(path));
    }

    public FSDataOutputStream createFile(String path) throws Exception {
        check();
        logger.info("createFile：{}", path);
        if (!isExist(path)) {
            return fs.create(new Path(path));
        } else {
            throw new FileAlreadyExistsException(path + " exist!");
        }
    }

    public FSDataOutputStream appendFile(String path) throws Exception {
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
    public FileStatus getFileInfo(String path) throws Exception {
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

    public boolean mkdir(String path) throws Exception {
        check();
        if (!isExist(path)) {
            logger.info("mkdir：{}", path);
            return fs.mkdirs(new Path(path));
        }
        return false;
    }

    public FSDataInputStream openFile(String path) throws Exception {
        check();
        if (isExist(path)) {
            logger.info("openFile：{}", path);
            return fs.open(new Path(path));
        }
        return null;
    }

    public void copyBytes(String inputFile, String outputFile) throws Exception {
        copyBytesSkipBegin(inputFile, outputFile, 0);
    }

    public void copyBytesFromLocal(String inputFile, String outputFile) throws Exception {
        InputStream in = localFileSystem.open(new Path(inputFile));
        OutputStream out = HdfsTool.createFile(fs, outputFile);
        IOUtils.copyBytes(in, out, fs.getConf());
        IOUtils.closeStream(in);
        IOUtils.closeStream(out);
        logger.info("copyBytesFromLocal closeStream.");
    }

    public void copyBytesFromLocalToLocal(String inputFile, String outputFile) throws Exception {
        InputStream in = HdfsTool.openFile(localFileSystem, inputFile);
        OutputStream out = HdfsTool.createFile(localFileSystem, outputFile);
        IOUtils.copyBytes(in, out, localFileSystem.getConf());
        IOUtils.closeStream(in);
        IOUtils.closeStream(out);
        logger.info("copyBytesFromLocalToLocal closeStream.");
    }

    public void copyBytesSkipBegin(String inputFile, String outputFile, long count) throws Exception {
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

    public void copyBytesSkipEnd(String inputFile, String outputFile, long count) throws Exception {
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

    public long countFileLine(String fileName) throws Exception {
        check();
        long count = 0;
        if (isExist(fileName)) {
            InputStream in = null;
            BufferedReader br = null;
            try {
                in = HdfsTool.openFile(fs, fileName);
                br = new BufferedReader(new InputStreamReader(in));
//                ExecutorsFactory executorsFactory = new ExecutorsFactory(10);
//                final BufferedReader finalBr = br;
//                executorsFactory.setiExecutorsRun(new IExecutorsRun() {
//                    @Override
//                    public long run() throws Exception {
//                        int count = 0;
//                        while (finalBr.readLine() != null) {
//                            count++;
//                        }
//                        System.out.println(this + "，" + count);
//                        return count;
//                    }
//                });
//                count = executorsFactory.startCallable();
                while (br.readLine() != null) {
                    count++;
                }
            } finally {
                IOUtils.closeStream(in);
                IOUtils.closeStream(br);
            }
        }
        return count;
    }

    public boolean delete(String path) throws Exception {
        return delete(fs, path);
    }

    public boolean delete(FileSystem fs, String path) throws Exception {
        check(fs);
        if (isExist(fs, path)) {
            logger.info("delete：{}", path);
            return HdfsTool.delete(fs, path);
//        return fs.deleteOnExit(new Path(path));
        }
        return false;
    }

    public boolean rename(String source, String dist) throws Exception {
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
        check(fs);
    }

    private void check(FileSystem fs) {
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

    private boolean checkHdfsFile() throws Exception {
        return checkHdfsFile(0);
    }

    private boolean checkHdfsFile(long filesize) throws Exception {
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

    public void startWrite(FSDataOutputStream fsDataOutputStream, String writePath) throws Exception {
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

    public void write(byte[] value, long checklen) throws Exception {
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

    /**
     * 从本地拷贝文件到HDFS集群
     *
     * @param src 本地文件
     * @param dst HDFS目标文件
     * @throws IOException
     */
    public void copyFromLocalFile(String src, String dst) throws IOException {
        check();
        fs.copyFromLocalFile(new Path(src), new Path(dst));
    }

    /**
     * 从本地拷贝文件到HDFS集群
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public void copyFromLocalFile(String[] src, String dst) throws Exception {
        check();
        delete(dst);
        for (String path : src) {
            copyBytesFromLocal(path, dst);
        }
    }

    /**
     * 从本地合并文件到本地
     *
     * @param src
     * @param localDst
     * @param hdfsDst
     * @throws IOException
     */
    public void copyFromLocalFileToLocal(String[] src, String localDst, String hdfsDst) throws Exception {
        check(localFileSystem);
        delete(localFileSystem, localDst);
        for (String path : src) {
            copyBytesFromLocalToLocal(path, localDst);
        }
        delete(hdfsDst);
        copyFromLocalFile(localDst, hdfsDst);
    }

    public void mergeFile(List<String> srcList, String dst) throws IOException {
        FileUtil.del(dst);
        FileUtil.mergeFile(srcList, dst);
    }

    public void mergeFileByPath(String srcPath, String dst) throws IOException {
        FileUtil.del(dst);
        File[] files = FileUtil.listFiles(srcPath);
        List<String> fileList = new ArrayList<>();
        for (File file : files) {
            logger.info("name：{}", file.getAbsolutePath());
            fileList.add(file.getAbsolutePath());
        }
        FileUtil.mergeFile(fileList, dst);
    }
}
