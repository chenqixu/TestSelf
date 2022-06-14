package com.bussiness.bi.bigdata.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.AbstractFileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

public class HelloWord {
	public static boolean createSymlink(AbstractFileSystem hdfsSystem,
			Path target, Path link, boolean createParent) {		
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
	
	public static void deleteFile(AbstractFileSystem hdfsSystem, Path path) {
		try {
			boolean delete = hdfsSystem.delete(path, true);
			System.out.println(path+" delete["+delete+"]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Configuration conf = new Configuration();
        conf.addResource(new Path("../conf/core-site.xml"));
        conf.addResource(new Path("../conf/hdfs-site.xml"));
        conf.set("test.SymlinkEnabledForTesting", "true");
        AbstractFileSystem fs = null;
		try {
			fs = AbstractFileSystem.get(new URI("hdfs://fj/"), conf);
			Path rootpath = new Path("/yz_newland/bigdata/gn_xdr/joinData/tohbasedata/2016011110cp");
			String final_link = "/yz_newland/bigdata/gn_xdr/joinData/tohbasedata/2016011110-link";
			boolean createParent = true;
			RemoteIterator<FileStatus> rip = fs.listStatusIterator(rootpath);
			while(rip.hasNext()){
				FileStatus _p = rip.next();
				if(_p.isDirectory()){
					RemoteIterator<FileStatus> _rip = fs.listStatusIterator(_p.getPath());
					while(_rip.hasNext()){
						FileStatus _tmp_p = _rip.next();
						System.out.println(_tmp_p.getPath()+" "+_tmp_p.getPath().getName());
						createSymlink(fs, _tmp_p.getPath(),
								new Path(final_link+"/"+_tmp_p.getPath().getName()), createParent);
					}
				}
			}			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		};		
	}
}
