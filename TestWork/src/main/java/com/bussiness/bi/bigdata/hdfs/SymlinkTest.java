package com.bussiness.bi.bigdata.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * 软链接验证
 * api(FileContext.createSymlink)
 */
public class SymlinkTest {

    public static final String PRINCIPAL = "username.client.kerberos.principal";
    public static final String KEYTAB = "username.client.keytab.file";
    public static final String KRB5 = "java.security.krb5.conf";
    public static final String SECURITY = "hadoop.security.authentication";
    public static final String HDFS_IMPL = "org.apache.hadoop.hdfs.DistributedFileSystem";
    public static final String BDOC_ID = "hadoop.security.bdoc.access.id";
    public static final String BDOC_KEY = "hadoop.security.bdoc.access.key";
    public static final String FS_DEFAULTFS = "fs.defaultFS";
    public static final String FS_HDFS_IMPL = "fs.hdfs.impl";
    public static Logger logger = LoggerFactory.getLogger(SymlinkTest.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String xmlpath = System.getProperty("user.dir");
        xmlpath = "D:\\tmp\\etc\\hadoop\\conf206\\";
        conf.addResource(new Path(xmlpath + "core-site.xml"));
        conf.addResource(new Path(xmlpath + "hdfs-site.xml"));
//        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
//        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        //允许在集群中创建symlink，需要在配置文件hdfs-site.xml中增加
//        conf.set("test.SymlinkEnabledForTesting", "true");
        //需要设置hadoop用户,否则没有权限
        System.setProperty("HADOOP_USER_NAME", "edc_base");
        FileSystem fs = null;

        String Principal = "edc_base/bdoc@FJBDKDC";
        String Keytab = "d:\\tmp\\etc\\hadoop\\conf206\\edc_base.keytab";
        String Krb5 = "d:\\tmp\\etc\\hadoop\\conf206\\krb5.conf";

        System.out.println(conf.get(SECURITY));
        // kerberos认证
        if ("kerberos".equalsIgnoreCase(conf.get(SECURITY))) {
            logger.info("kerberos认证");
            // principal，示例：yz_newland/bdoc@FJBDKDC
            conf.set(PRINCIPAL, Principal);
            // keytab file，示例：/home/yz_newland/newland/app/keytab/yz_newland.keytab
            conf.set(KEYTAB, Keytab);
            // kerberos krb5.conf，示例：/etc/krb5.conf
            System.setProperty(KRB5, Krb5);
            // login
            UserGroupInformation.setConfiguration(conf);
            try {
                UserGroupInformation.loginUserFromKeytab(conf.get(PRINCIPAL), conf.get(KEYTAB));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        FileSystem.enableSymlinks();
        System.out.println("FileSystem.areSymlinksEnabled()：" + FileSystem.areSymlinksEnabled());
        FileContext fc = null;
        Path target1 = new Path("/user/edc_base/data/1.data");
        Path link1 = new Path("/user/edc_base/data/2.data");
        boolean createParent = false;
        try {
//            fc = FileContext.getFileContext(new URI("hdfs://fjedcprohd/"), conf);
            fs = FileSystem.newInstance(new URI("hdfs://fjedcprohd/"), conf);
            System.out.println("FileSystem.areSymlinksEnabled()：" + FileSystem.areSymlinksEnabled());
        } catch (UnsupportedFileSystemException e1) {
            System.out.println("UnsupportedFileSystemException");
            e1.printStackTrace();
        }
//		URI uri = null;
        try {
//			uri = new URI("hdfs://10.1.8.1:8020");
//			AbstractFileSystem afs = AbstractFileSystem.createFileSystem(uri, conf);
//			afs.createSymlink(target1, link1, createParent);
//			afs.createSymlink(target2, link2, createParent);
//            fc.createSymlink(target1, link1, createParent);
            fs.createSymlink(target1, link1, createParent);
            System.out.println("success createSymlink.");
        } catch (AccessControlException e) {
            System.out.println("AccessControlException");
            e.printStackTrace();
        } catch (FileAlreadyExistsException e) {
            System.out.println("FileAlreadyExistsException");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            e.printStackTrace();
        } catch (ParentNotDirectoryException e) {
            System.out.println("ParentNotDirectoryException");
            e.printStackTrace();
        } catch (UnsupportedFileSystemException e) {
            System.out.println("UnsupportedFileSystemException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
//		} catch (URISyntaxException e) {
//			System.out.println("URISyntaxException");
//			e.printStackTrace();
        }
    }
}
