package com.cqx.common.utils.hdfs;

import com.cqx.common.utils.file.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * HDFS工具类
 */
public class HdfsTool {

    private static final String KRB5 = "java.security.krb5.conf";
    private final static String HDFS_BDOC_ID = "hadoop.security.bdoc.access.id";
    private final static String HDFS_BDOC_KEY = "hadoop.security.bdoc.access.key";
    private final static String HDFS_KEYTAB_STR = "username.client.keytab.file";
    private final static String HDFS_PRINCIPAL_STR = "username.client.kerberos.principal";
    private final static String HDFS_AUTH_TYPE = "hadoop.security.authentication";
    private final static String HDFS_AUTH_TYPE_CHECK = "hadoop.security.authorization";
    private static final Logger logger = LoggerFactory.getLogger(HdfsTool.class);

    private Configuration configuration;
    private FileSystem fs;

    public HdfsTool() {
    }

    public HdfsTool(String conf_path, HdfsBean hdfsBean) throws IOException {
        configuration = getConf(conf_path, hdfsBean);
        fs = getFileSystem(configuration);
    }

    /**
     * 设置hadoop用户
     *
     * @param user_name
     */
    public static void setHadoopUser(String user_name) {
        System.setProperty("HADOOP_USER_NAME", user_name);
    }

    public long getFileSize(Path path) {
        return getFileSize(fs, path);
    }

    /**
     * hdfs上获得文件大小
     *
     * @param fs
     * @param path
     * @return
     */
    public long getFileSize(FileSystem fs, Path path) {
        logger.info("getFileSize：{}", path);
        try {
            if (fs.exists(path) && fs.isFile(path)) {
                return fs.getFileStatus(path).getLen();
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0L;
        }
        return 0L;
    }

    public boolean isExist(String path) throws Exception {
        return isExist(fs, path);
    }

    /**
     * 文件或路径是否存在
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public boolean isExist(FileSystem fs, String path) throws Exception {
        logger.info("isExist：{}", path);
        if (fs instanceof LocalFileSystem) {
            File file = new File(new URI(path));
            return file.exists();
        } else {
            return fs.exists(new Path(path));
        }
    }

    /**
     * 创建一个空文件
     *
     * @param path
     * @throws Exception
     */
    public void touch(String path) throws Exception {
        try (OutputStream os = createFile(path)) {
            logger.info("touch：{}", path);
        }
    }

    public OutputStream createFile(String path) throws Exception {
        return createFile(fs, path);
    }

    /**
     * 创建文件
     *
     * @param fs
     * @param path
     * @return
     * @throws Exception
     */
    public OutputStream createFile(FileSystem fs, String path) throws Exception {
        if (isExist(fs, path))
            return appendFile(fs, path);
        logger.info("createFile：{}", path);
        if (fs instanceof LocalFileSystem) {
            return new FileOutputStream(new File(new URI(path)));
        } else {
            return fs.create(new Path(path));
        }
    }

    public OutputStream appendFile(String path) throws IOException, URISyntaxException {
        return appendFile(fs, path);
    }

    /**
     * 续写文件
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public OutputStream appendFile(FileSystem fs, String path) throws IOException, URISyntaxException {
        logger.info("appendFile：{}", path);
        if (fs instanceof LocalFileSystem) {
            return new FileOutputStream(new File(new URI(path)), true);
        } else {
            return fs.append(new Path(path));
        }
    }

    public void closeFileSystem() throws IOException {
        closeFileSystem(fs);
    }

    /**
     * 关闭文件系统
     *
     * @param fs
     * @throws IOException
     */
    public void closeFileSystem(FileSystem fs) throws IOException {
        logger.info("closeFileSystem：{}", fs);
        if (fs != null)
            fs.close();
    }

    /**
     * 通过配置获取分布式文件对象
     *
     * @param hadoopConfig
     * @return
     * @throws IOException
     */
    public FileSystem getFileSystem(Configuration hadoopConfig) throws IOException {
        return FileSystem.newInstance(hadoopConfig);
    }

    /**
     * 获取路径
     *
     * @param local
     * @param remote
     * @return
     */
    public String getPath(String local, String remote) {
        if (!isWindow()) {
            return remote;
        }
        return local;
    }

    /**
     * 获取配置文件
     *
     * @return
     */
    public Configuration getConf(String conf_path, HdfsBean hdfsBean) throws IOException {
        Configuration hadoopConfig = new Configuration();
        String _conf_path = FileUtil.endWith(conf_path);
        hadoopConfig.addResource(new Path(_conf_path + "core-site.xml"));
        hadoopConfig.addResource(new Path(_conf_path + "hdfs-site.xml"));
        hadoopConfig.addResource(new Path(_conf_path + "mapred-site.xml"));
        hadoopConfig.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        //认证
        if (hdfsBean.getAuth_type().equals("default")) {//普通无认证
        } else if (hdfsBean.getAuth_type().equals("bdoc")) {//bdoc认证
            String bdoc_id = hdfsBean.getBdoc_id();
            String bdoc_key = hdfsBean.getBdoc_key();
            if (bdoc_id != null && bdoc_id.length() > 0 && bdoc_key != null && bdoc_key.length() > 0) {
                hadoopConfig.set(HDFS_BDOC_ID, bdoc_id);
                hadoopConfig.set(HDFS_BDOC_KEY, bdoc_key);
            } else {
                throw new NullPointerException(String.format(
                        "当前认证类型为bdoc，bdoc_id或bdoc_key不能为空，[bdoc_id]：%s，[bdoc_key]：%s，请检查！",
                        bdoc_id, bdoc_key));
            }
        } else if (hdfsBean.getAuth_type().equals("kerberos")) {//kerberos认证
            String principal = hdfsBean.getPrincipal();
            String keytab = hdfsBean.getKeytab();
            String krb5 = hdfsBean.getKrb5();
            if (principal != null && principal.length() > 0
                    && keytab != null && keytab.length() > 0
                    && krb5 != null && krb5.length() > 0) {
                hadoopConfig.set(HDFS_PRINCIPAL_STR, principal);
                hadoopConfig.set(HDFS_KEYTAB_STR, keytab);
                hadoopConfig.set(KRB5, krb5);
                hadoopConfig.set(HDFS_AUTH_TYPE, "kerberos");
                hadoopConfig.set(HDFS_AUTH_TYPE_CHECK, "true");

                // kerberos krb5.conf
                System.setProperty(KRB5, krb5);
                UserGroupInformation.setConfiguration(hadoopConfig);
                //login
                UserGroupInformation.loginUserFromKeytab(hadoopConfig.get(HDFS_PRINCIPAL_STR), hadoopConfig.get(HDFS_KEYTAB_STR));
            } else {
                throw new NullPointerException(String.format(
                        "当前认证类型为kerberos，principal或keytab或krb5不能为空，[principal]：%s，[keytab]：%s，[krb5]：%s，请检查！",
                        principal, keytab, krb5));
            }
        } else {
            throw new UnsupportedOperationException(String.format(
                    "不支持的hdfs认证类型，[auth_type]：%s，请检查！", hdfsBean.getAuth_type()));
        }
        logger.info("hadoopConfig：{}", hadoopConfig);
        return hadoopConfig;
    }

    /**
     * 是否是本地测试
     *
     * @return
     */
    public boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }

    public FileStatus getFileInfo(String path) throws IOException {
        return getFileInfo(fs, path);
    }

    /**
     * 获取文件状态
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public FileStatus getFileInfo(FileSystem fs, String path) throws IOException {
        logger.info("getFileInfo：{}", path);
        return fs.getFileStatus(new Path(path));
    }

    public List<String> lsPath(String path) throws IOException {
        List<FileStatus> fileStatusList = ls(fs, path);
        List<String> result = new ArrayList<>();
        for (FileStatus fileStatus : fileStatusList) {
            result.add(fileStatus.getPath().toString());
        }
        return result;
    }

    public List<FileStatus> lsFileStatus(String path) throws IOException {
        return ls(fs, path);
    }

    /**
     * ls遍历文件路径，默认不走通配符模式
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public List<FileStatus> ls(FileSystem fs, String path) throws IOException {
        return ls(fs, path, false);
    }

    /**
     * ls遍历文件路径
     *
     * @param fs
     * @param path
     * @param wildcard 是否支持通配符
     * @throws IOException
     */
    public List<FileStatus> ls(FileSystem fs, String path, boolean wildcard) throws IOException {
        List<FileStatus> fileList = new ArrayList<>();
        if (fs != null) {
            logger.info("ls：{}", path);
            if (wildcard) {
                //globStatus 支持通配符
                for (FileStatus fileStatus : fs.globStatus(new Path(path))) {
                    fileList.add(fileStatus);
                    logger.info("{} {} {} {} {} {}", fileStatus.getPermission(), fileStatus.getOwner(),
                            fileStatus.getGroup(), fileStatus.isDirectory(), fileStatus.getLen(), fileStatus.getPath());
                }
            } else {
                //listStatus 不支持通配符
                for (FileStatus fileStatus : fs.listStatus(new Path(path))) {
                    fileList.add(fileStatus);
                    logger.info("{} {} {} {} {} {}", fileStatus.getPermission(), fileStatus.getOwner(),
                            fileStatus.getGroup(), fileStatus.isDirectory(), fileStatus.getLen(), fileStatus.getPath());
                }
            }
        }
        return fileList;
    }

    public boolean mkdir(String path) throws IOException {
        return mkdir(fs, path);
    }

    /**
     * 创建文件夹
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public boolean mkdir(FileSystem fs, String path) throws IOException {
        if (fs != null && !fs.exists(new Path(path))) {
            logger.info("mkdir：{}", path);
            return fs.mkdirs(new Path(path));
        }
        return false;
    }

    public InputStream openFile(String path) throws Exception {
        return openFile(fs, path);
    }

    /**
     * 打开文件
     *
     * @param fs
     * @param path
     * @return
     * @throws Exception
     */
    public InputStream openFile(FileSystem fs, String path) throws Exception {
        if (fs != null) {
            if (fs instanceof LocalFileSystem) {
                File file = new File(new URI(path));
                if (file.exists()) {
                    logger.info("openFile：{}", path);
                    return new FileInputStream(file);
                }
            } else {
                if (fs.exists(new Path(path))) {
                    logger.info("openFile：{}", path);
                    return fs.open(new Path(path));
                }
            }
        }
        return null;
    }

    public void copyBytes(String inputFile, String outputFile) throws Exception {
        copyBytes(fs, inputFile, outputFile);
    }

    /**
     * 拷贝文件
     *
     * @param fs
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    public void copyBytes(FileSystem fs, String inputFile, String outputFile) throws Exception {
        if (fs != null && fs.exists(new Path(inputFile))) {
            logger.info("copyBytes：{} to {}.", inputFile, outputFile);
            delete(fs, outputFile);
            InputStream in = openFile(fs, inputFile);
            OutputStream out = createFile(fs, outputFile);
            IOUtils.copyBytes(in, out, fs.getConf());
            IOUtils.closeStream(in);
            IOUtils.closeStream(out);
//            IOUtils.writeFully(FileChannel, ByteBuffer, 10);
            logger.info("copyBytes closeStream.");
        }
    }

    public boolean delete(String path) throws IOException {
        return delete(fs, path);
    }

    /**
     * 删除文件
     *
     * @param fs
     * @param path
     * @return
     * @throws IOException
     */
    public boolean delete(FileSystem fs, String path) throws IOException {
        if (fs != null) {
            logger.info("delete：{}", path);
            return fs.delete(new Path(path), true);
//            return fs.deleteOnExit(new Path(path));
        }
        return false;
    }

    /**
     * 重命名文件
     *
     * @param fs
     * @param old_path
     * @param new_path
     * @return
     * @throws IOException
     */
    public boolean rename(FileSystem fs, String old_path, String new_path) throws IOException {
        if (fs != null) {
            logger.info("rename，old_path：{}，new_path：{}", old_path, new_path);
            Path src = new Path(old_path);
            Path dest = new Path(new_path);
            return fs.rename(src, dest);
        }
        return false;
    }

    public boolean rename(String old_path, String new_path) throws IOException {
        return rename(fs, old_path, new_path);
    }

}
