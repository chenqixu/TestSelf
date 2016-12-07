package com.newland.bi.bigdata.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.security.AccessControlException;

/**
 * 软链接验证
 * api(FileContext.createSymlink) 
 * */
public class SymlinkTest {
	public static void main(String[] args) {
		Configuration conf = new Configuration();
		String xmlpath = System.getProperty("user.dir");
		conf.addResource(xmlpath+"/core-site.xml");
		conf.addResource(xmlpath+"/hdfs-site.xml");
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        //允许在集群中创建symlink，需要在配置文件hdfs-site.xml中增加
        conf.set("test.SymlinkEnabledForTesting", "true");
        //需要设置hadoop用户,否则没有权限
        System.setProperty("HADOOP_USER_NAME", "hadoop");
//		FileSystem fs = null;
//		try {
//			fs = FileSystem.newInstance(conf);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		};
		FileContext fc = null;
		Path target1 = new Path("/test/input1.txt");
		Path target2 = new Path("/test/input2.txt");
		Path link1 = new Path("/test/inputlink/input1.txt");
		Path link2 = new Path("/test/inputlink/input2.txt");
		boolean createParent = false;
		try {
			fc = FileContext.getFileContext(conf);
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
			fc.createSymlink(target1, link1, createParent);
			fc.createSymlink(target2, link2, createParent);
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
