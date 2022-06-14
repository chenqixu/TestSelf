package com.bussiness.bi.bigdata.hdfs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

public class HdfsTest {
	private FileSystem hdfsSystem = null;
    private Configuration hdfsCfg = null;
    
	public Configuration getHdfsCfg() {
		return hdfsCfg;
	}

	public HdfsTest(){
//		init();
	}
	
	public void init(String confPath) {
		try {
			hdfsSystem = HdfsUtils.openFileSystem(confPath);
			hdfsCfg = HdfsUtils.getHdfsCfg();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void init(String defaultfs, String nameservice, String namenodes,
			String namenodeAddr, String bdoc_id, String bdoc_key) {
        //需要设置hadoop用户,否则没有权限
//        System.setProperty("HADOOP_USER_NAME", "hadoop");
		try {
			// other
			String failoverProxy = "";
			boolean isSecurity = false;
			String principal = "";
			String keytab = "";
			hdfsSystem = HdfsUtils.openFileSystem(defaultfs, nameservice, namenodes, namenodeAddr, 
					failoverProxy, isSecurity, principal, keytab, bdoc_id, bdoc_key);
//			hdfsSystem = HdfsUtils.openFileSystem(confPath);
			hdfsCfg = HdfsUtils.getHdfsCfg();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public InputStream readfile(String filepath, String filetype){
		InputStream ins = null;
		try {
			if(filetype.equals(HdfsUtils.LOCAL_FILE)){
				ins = new FileInputStream(filepath);
			}else if(filetype.equals(HdfsUtils.HDFS_FILE)){
				ins = new FSDataInputStream(hdfsSystem.open(new Path(filepath)));
			}
			System.out.println("[readfile]"+filepath+" "+ins.available());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return ins;
	}
	
	public void compress(String codecClassName, InputStream in, String outputPath) throws Exception {
		Class<?> codecClass = Class.forName(codecClassName);
		CompressionCodec codec = (CompressionCodec)ReflectionUtils.newInstance(codecClass, hdfsCfg);
		// 压缩前先判断文件是否已经存在，如果存在则删除
		long file_long = HdfsUtils.getFileSize(hdfsSystem, new Path(outputPath));
		// 存在文件
		if(file_long>0) {
			// 删除文件
			HdfsUtils.deleteFile(hdfsSystem, new Path(outputPath));
		}
		// 指定压缩文件路径
		FSDataOutputStream outputStream = hdfsSystem.create(new Path(outputPath));
		// 创建压缩输出流
		CompressionOutputStream out = codec.createOutputStream(outputStream);
		IOUtils.copyBytes(in, out, hdfsCfg);
		IOUtils.closeStream(in);
		IOUtils.closeStream(out);
	}
	
	public void hdfsLS(String outputPath){
		HdfsUtils.getFileList(hdfsSystem, outputPath);
	}
	
	public static void main(String[] args) {
		// 从本地读取文件，以GZIP方式压缩并入到hdfs文件系统
		HdfsTest ht = new HdfsTest(); 
		String filepath = "";
		String filetype = "";
		String outputPath = "";
		String codecClassName = "";
		String defaultfs = "";
		String nameservice = "";
		String namenodes = "";
		String namenodeAddr = "";
		String bdoc_id = "";
		String bdoc_key = "";
		String confPath = "";		
		if(args.length==1){ // 配置文件版本
			confPath = args[0];
			System.out.println("[confPath]"+confPath);
			try {
				ht.init(confPath);
				filepath =  ht.getHdfsCfg().get("filepath");
				filetype =  ht.getHdfsCfg().get("filetype");
				outputPath =  ht.getHdfsCfg().get("outputPath");
				codecClassName =  ht.getHdfsCfg().get("codecClassName");
				ht.compress(codecClassName, ht.readfile(filepath, filetype), outputPath);
				// 查看是否压缩成功
				ht.hdfsLS(outputPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(args.length>=5){ // 配置参数版本
			filepath = args[0];
			filetype = args[1];
			outputPath = args[2];
			codecClassName = args[3];
			defaultfs = args[4];
			System.out.println("[filepath]"+filepath);
			System.out.println("[filetype]"+filetype);
			System.out.println("[outputPath]"+outputPath);
			System.out.println("[codecClassName]"+codecClassName);
			System.out.println("[defaultfs]"+defaultfs);
			if(args.length>5){
				nameservice = args[5];
				System.out.println("[nameservice]"+nameservice);
			}
			if(args.length>6){
				namenodes = args[6];
				System.out.println("[namenodes]"+namenodes);
			}
			if(args.length>7){
				namenodeAddr = args[7];
				System.out.println("[namenodeAddr]"+namenodeAddr);
			}
			if(args.length>8){
				bdoc_id = args[8];
				System.out.println("[bdoc_id]"+bdoc_id);
			}
			if(args.length>9){
				bdoc_key = args[9];
				System.out.println("[bdoc_key]"+bdoc_key);
			}
			try {
				ht.init(defaultfs, nameservice, namenodes, namenodeAddr, bdoc_id, bdoc_key);
				ht.compress(codecClassName, ht.readfile(filepath, filetype), outputPath);
				// 查看是否压缩成功
				ht.hdfsLS(outputPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("参数个数不对，请输入1个或5个或5个以上参数。");
			System.exit(1);
		}		
//		filepath = "D:\\home\\dict\\URL_CHK";
//		filepath = "/home/hadoop/files1463716229824/input/N5001_2G.AVL";
//		outputPath = "/cqx/testdata/gzip/URL_CHK.gz";
//		codecClassName = HdfsUtils.GZIP_CLASSNAME;
//		outputPath = "/cqx/testdata/gzip/URL_CHK.bz2";
//		codecClassName = HdfsUtils.BZIP2_CLASSNAME;
//		outputPath = "/cqx/testdata/gzip/URL_CHK.lzo";
//		outputPath = "/cqx/testdata/gzip/N5001_2G.lzo";
//		codecClassName = HdfsUtils.LZO_CLASSNAME;
//		outputPath = "/cqx/testdata/gzip/URL_CHK.snappy";
//		codecClassName = HdfsUtils.SNAPPY_CLASSNAME;
	}
}
