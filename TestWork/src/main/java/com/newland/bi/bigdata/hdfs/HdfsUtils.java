package com.newland.bi.bigdata.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.SecurityUtil;

public class HdfsUtils {
	// key
    public final static String HDFS_KEYTAB_STR = "username.client.keytab.file";
    public final static String HDFS_PRINCIPAL_STR = "username.client.kerberos.principal";
    // code
    public final static String GZIP_CLASSNAME = "org.apache.hadoop.io.compress.GzipCodec";
    public final static String BZIP2_CLASSNAME = "org.apache.hadoop.io.compress.BZip2Codec";
    public final static String LZO_CLASSNAME = "com.hadoop.compression.lzo.LzopCodec";
    public final static String SNAPPY_CLASSNAME = "org.apache.hadoop.io.compress.SnappyCodec";
    // file type
    public final static String LOCAL_FILE = "local";
    public final static String HDFS_FILE = "hdfs";
    // conf
    private static Configuration hdfsCfg = null;
    
	public static Configuration getHdfsCfg() {
		return hdfsCfg;
	}

	/**
	 * 连接hdfs,获得FileSystem
	 * */
	public static FileSystem openFileSystem(String defaultfs,
			String nameservice, String namenodes, String namenodeAddr,
			String failoverProxy, boolean isSecurity,String principal, String keytab,
			String bdoc_id, String bdoc_key) throws IOException {
//		File workaround = new File(".");
//		System.getProperties().put("hadoop.home.dir",
//				workaround.getAbsolutePath());
//		new File("./bin").mkdirs();
//		new File("./bin/winutils.exe").createNewFile();

		FileSystem fs = null;
		hdfsCfg = new Configuration();	
		hdfsCfg.set("fs.defaultFS", defaultfs);
		hdfsCfg.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
		if(nameservice.length()>0){
			hdfsCfg.set("dfs.nameservices", nameservice);
		}
		if(namenodes.length()>0){
			hdfsCfg.set("dfs.ha.namenodes." + nameservice, namenodes);
		}

		if(namenodes.length()>0 && namenodeAddr.length()>0){
			List<String> nameNodeList = split(namenodes, ",");
			List<String> nameNodeAddrList = split(namenodeAddr, ",");
			if (nameNodeAddrList.size()>1) {
				for (int i = 0; i < nameNodeAddrList.size(); i++) {
					hdfsCfg.set("dfs.namenode.rpc-address." + nameservice + "."
							+ nameNodeList.get(i), nameNodeAddrList.get(i));
				}
			}
		}
		if (failoverProxy.length()>0) {
			hdfsCfg.set("dfs.client.failover.proxy.provider." + nameservice,
					failoverProxy);
		}
		if(isSecurity){
			hdfsCfg.set(HdfsUtils.HDFS_PRINCIPAL_STR, principal);
	        hdfsCfg.set(HdfsUtils.HDFS_KEYTAB_STR, keytab);
	        hdfsCfg.set("hadoop.security.authentication", "kerberos");
	        hdfsCfg.set("hadoop.security.authorization", "true");

			SecurityUtil.login(hdfsCfg, HdfsUtils.HDFS_KEYTAB_STR, HdfsUtils.HDFS_PRINCIPAL_STR, 
					InetAddress.getLocalHost().getCanonicalHostName());
		}
		if(bdoc_id.length()>0 && bdoc_key.length()>0){
			hdfsCfg.set("hadoop.security.bdoc.access.id", bdoc_id);
			hdfsCfg.set("hadoop.security.bdoc.access.key", bdoc_key);			
		}
		fs = FileSystem.get(hdfsCfg);
		System.out.println("[openFileSystem]"+fs);
		return fs;
	}
	
	public static FileSystem openFileSystem(String confPath){
		FileSystem fs = null;
		hdfsCfg = new Configuration();
		List<String> confList = split(confPath, ",");
		for (int i=0;i<confList.size();i++) {
			hdfsCfg.addResource(new Path(confList.get(i)));
		}
		System.out.println("hdfsCfg:"+hdfsCfg);
		System.out.println("[fs.defaultFS]"+hdfsCfg.get("fs.defaultFS"));
		System.out.println("[fs.hdfs.impl]"+hdfsCfg.get("fs.hdfs.impl"));
		try {
			fs = FileSystem.get(hdfsCfg);
			System.out.println("[openFileSystem]"+fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fs;
	}
	
	/**
	 * 连接hdfs,获得AbstractFileSystem
	 * */
	public static AbstractFileSystem openAbstractFileSystem(String defaultfs,
			String nameservice, String namenodes, String namenodeAddr,
			String failoverProxy, boolean isSecurity,String principal, String keytab) throws IOException {
		AbstractFileSystem afs = null;
		hdfsCfg = new Configuration();
//		String xmlpath = System.getProperty("user.dir");
//		hdfsCfg.addResource(xmlpath+"/src/main/resources/conf/core-site.xml");
//		hdfsCfg.addResource(xmlpath+"/src/main/resources/conf/hdfs-site.xml");		
		hdfsCfg.set("fs.defaultFS", defaultfs);
		hdfsCfg.set("dfs.nameservices", nameservice);
		hdfsCfg.set("dfs.ha.namenodes." + nameservice, namenodes);

		List<String> nameNodeList = split(namenodes, ",");
		List<String> nameNodeAddrList = split(namenodeAddr, ",");

		for (int i = 0; i < nameNodeAddrList.size(); i++) {
			hdfsCfg.set("dfs.namenode.rpc-address." + nameservice + "."
					+ nameNodeList.get(i), nameNodeAddrList.get(i));
		}
		hdfsCfg.set("dfs.client.failover.proxy.provider." + nameservice,
				failoverProxy);
		if(isSecurity){
			hdfsCfg.set(HdfsUtils.HDFS_PRINCIPAL_STR, principal);
	        hdfsCfg.set(HdfsUtils.HDFS_KEYTAB_STR, keytab);
	        hdfsCfg.set("hadoop.security.authentication", "kerberos");
	        hdfsCfg.set("hadoop.security.authorization", "true");

			SecurityUtil.login(hdfsCfg, HdfsUtils.HDFS_KEYTAB_STR, HdfsUtils.HDFS_PRINCIPAL_STR, 
					InetAddress.getLocalHost().getCanonicalHostName());
		}
		System.out.println("hdfsCfg:"+hdfsCfg);		
		try {
			afs = AbstractFileSystem.get(new URI(defaultfs), hdfsCfg);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return afs;
	}
	
	/**
	 * 切割字符串变成list
	 * */
	public static List<String> split(String str, String delim){
		List<String> splitList = null;
		StringTokenizer st;
		
		if(str == null)
			return null;
		if(delim != null)
			st = new StringTokenizer(str, delim);
		else
			st = new StringTokenizer(str);
		if(st.hasMoreTokens()){
			splitList = new ArrayList<String>();
			
			while(st.hasMoreTokens()){
				splitList.add(st.nextToken());
			}
		}		
		return splitList;
	}
	
	/**
	 * hdfs上建立软链接
	 * */
	public static boolean createSymlink(AbstractFileSystem hdfsSystem, Path target, Path link, boolean createParent) {		
		try {
			deleteFile(hdfsSystem, link);
			hdfsSystem.createSymlink(target, link, createParent);
			System.out.println(target+" createSymlink "+link+" success.");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * hdfs上新建文件
	 * */
	public static boolean createNewFile(FileSystem hdfsSystem, Path path) {		
		FSDataOutputStream fos = null;
		try {
			deleteFile(hdfsSystem, path);
			System.out.println("[createNewFile]"+path.toString());
			fos = hdfsSystem.create(path, true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * hdfs上修改文件名称
	 * */
	public static boolean reNameFile(FileSystem hdfsSystem, Path path0,
			Path path1) {
		try {
			deleteFile(hdfsSystem, path1);
			System.out.println("[reNameFile]"+path0.toString()+" to "+path1.toString());
			return hdfsSystem.rename(path0, path1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * hdfs上删除文件
	 * */
	public static void deleteFile(AbstractFileSystem hdfsSystem, Path path) {
		try {
			boolean delete = hdfsSystem.delete(path, true);
			System.out.println(path+" delete["+delete+"]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * hdfs上删除文件
	 * */
	public static boolean deleteFile(FileSystem hdfsSystem, Path path) {
		boolean flag = false;
		try {
			if(hdfsSystem.exists(path)){
				System.out.println("[deleteFile]"+path.toString());
				flag = hdfsSystem.delete(path, true);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * hdfs上获得文件大小
	 * */
	public static long getFileSize(FileSystem fs, Path path){
		long resultLong = 0L;
		try {
			if (fs.exists(path)) {
				resultLong = fs.listStatus(path)[0].getLen();
				System.out.println("[getFileSize]"+path.toString()+" "+resultLong);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultLong;
	}
	
	/**
	 * hdfs dfs -ls /path
	 * */
	public static void getFileList(FileSystem fs, String path){
		FileStatus[] alist = null;
		try {
			alist = fs.listStatus(new Path(path));
			for(int i=0;i<alist.length;i++) {
				System.out.println("[getFileList]"+alist[i].getPath()+" "+alist[i].getLen());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void hadoopedc01(){        
//		FileSystem hdfsSystem;
		AbstractFileSystem ahdfsSystem;
		String defaultfs = "hdfs://10.1.8.1:8020";//"hdfs://streamslab.localdomain:8020";//
		String nameservice = "bch";
		String namenodes = "nn1,nn2";//"nn1";//
		String namenodeAddr = "10.1.8.1:8020,10.1.8.2:8020";//"192.168.230.128:8020";//
		String failoverProxy = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
		boolean isSecurity = false;
		String principal = "";
		String keytab = "";
		try{
//			hdfsSystem = HdfsUtils.openFileSystem(defaultfs, nameservice, namenodes, namenodeAddr, 
//					failoverProxy, isSecurity, principal, keytab);
//			System.out.println(hdfsSystem);
//			Path path = new Path("/test/a1.txt");
//			Path path1 = new Path("/test/aa1.txt");
//			boolean createresult = HdfsUtils.createNewFile(hdfsSystem, path);
//			System.out.println("createresult:"+createresult);
//			boolean createresult1 = HdfsUtils.createNewFile(hdfsSystem, path1);
//			System.out.println("createresult1:"+createresult1);
//			HdfsUtils.deleteFile(hdfsSystem, path);
//			System.out.println("delete "+path);
//			boolean renameresult = HdfsUtils.reNameFile(hdfsSystem, path1, path);
//			System.out.println("renameresult:"+renameresult);
//			long file_long = HdfsUtils.getFileSize(hdfsSystem, path);
//			System.out.println(file_long);
//			long file_long = HdfsUtils.getFileSize(hdfsSystem, path);
//			System.out.println(file_long);
//			boolean createresult = HdfsUtils.createNewFile(hdfsSystem, path1);
//			System.out.println(createresult);
			//软链接
			ahdfsSystem = HdfsUtils.openAbstractFileSystem(defaultfs, nameservice, namenodes,
					namenodeAddr, failoverProxy, isSecurity, principal, keytab);
//			Path target1 = new Path("/test/input1.txt");
//			Path target2 = new Path("/test/input2.txt");
//			Path link1 = new Path("/test/inputlink/input1.txt");
//			Path link2 = new Path("/test/inputlink/input2.txt");
			Path rootpath = new Path("/yz_newland/bigdata/gn_xdr/joinData/data/2016011110");
			String final_link = "/yz_newland/bigdata/gn_xdr/joinData/data/2016011110-link";
			boolean createParent = true;
			RemoteIterator<FileStatus> rip = ahdfsSystem.listStatusIterator(rootpath);
			while(rip.hasNext()){
				FileStatus _p = rip.next();
				if(_p.isDirectory()){
					RemoteIterator<FileStatus> _rip = ahdfsSystem.listStatusIterator(_p.getPath());
					while(_rip.hasNext()){
						FileStatus _tmp_p = _rip.next();
						System.out.println(_tmp_p.getPath()+" "+_tmp_p.getPath().getName());
						HdfsUtils.createSymlink(ahdfsSystem, _tmp_p.getPath(),
								new Path(final_link+"/"+_tmp_p.getPath().getName()), createParent);
					}
				}
			}			
//			HdfsUtils.createSymlink(ahdfsSystem, target1, link1, createParent);
//			HdfsUtils.createSymlink(ahdfsSystem, target2, link2, createParent);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void hadoopmaster75(){
		FileSystem hdfsSystem;
		String defaultfs = "hdfs://10.1.8.75:8020";
		String nameservice = "master75";
		String namenodes = "nn";
		String namenodeAddr = "10.1.8.75:8020";
		String failoverProxy = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
		boolean isSecurity = false;
		String principal = "";
		String keytab = "";
		try{
			hdfsSystem = HdfsUtils.openFileSystem(defaultfs, nameservice, namenodes, namenodeAddr, 
					failoverProxy, isSecurity, principal, keytab, "", "");
			System.out.println(hdfsSystem);
			String path = "hdfs://master75:8020/zyh";
			getFileList(hdfsSystem, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        //需要设置hadoop用户,否则没有权限
        System.setProperty("HADOOP_USER_NAME", "hadoop");
        hadoopmaster75();
	}
	
}
