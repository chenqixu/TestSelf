package com.cqx.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;

/**
 * file operation on HDFS
 * 
 * @author liuxingjiaofu
 * 
 */
public class HDFS_File {
	// read the file from HDFS
	public void ReadFile(Configuration conf, String FileName) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			FSDataInputStream dis = hdfs.open(new Path(FileName));
			IOUtils.copyBytes(dis, System.out, 4096, false);
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// copy the file from HDFS to local
	public void GetFile(Configuration conf, String srcFile, String dstFile) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path srcPath = new Path(srcFile);
			Path dstPath = new Path(dstFile);
			hdfs.copyToLocalFile(true, srcPath, dstPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// copy the local file to HDFS
	public void PutFile(Configuration conf, String srcFile, String dstFile) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path srcPath = new Path(srcFile);
			Path dstPath = new Path(dstFile);
			hdfs.copyFromLocalFile(srcPath, dstPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// create the new file
	public FSDataOutputStream CreateFile(Configuration conf, String FileName) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path path = new Path(FileName);
			FSDataOutputStream outputStream = hdfs.create(path);
			return outputStream;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// rename the file name
	public boolean ReNameFile(Configuration conf, String srcName, String dstName) {
		try {
			Configuration config = new Configuration();
			FileSystem hdfs = FileSystem.get(config);
			Path fromPath = new Path(srcName);
			Path toPath = new Path(dstName);
			boolean isRenamed = hdfs.rename(fromPath, toPath);
			return isRenamed;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// delete the file
	// tyep = true, delete the directory
	// type = false, delete the file
	public boolean DelFile(Configuration conf, String FileName, boolean type) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path path = new Path(FileName);
			boolean isDeleted = hdfs.delete(path, type);
			return isDeleted;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Get HDFS file last modification time
	public long GetFileModTime(Configuration conf, String FileName) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path path = new Path(FileName);
			FileStatus fileStatus = hdfs.getFileStatus(path);
			long modificationTime = fileStatus.getModificationTime();
			return modificationTime;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// check if a file exists in HDFS
	public boolean CheckFileExist(Configuration conf, String FileName) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path path = new Path(FileName);
			boolean isExists = hdfs.exists(path);
			return isExists;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Get the locations of a file in the HDFS cluster
	public List<String[]> GetFileBolckHost(Configuration conf, String FileName) {
		try {
			List<String[]> list = new ArrayList<String[]>();
			FileSystem hdfs = FileSystem.get(conf);
			Path path = new Path(FileName);
			FileStatus fileStatus = hdfs.getFileStatus(path);

			BlockLocation[] blkLocations = hdfs.getFileBlockLocations(
					fileStatus, 0, fileStatus.getLen());

			int blkCount = blkLocations.length;
			for (int i = 0; i < blkCount; i++) {
				String[] hosts = blkLocations[i].getHosts();
				list.add(hosts);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Get a list of all the nodes host names in the HDFS cluster
	// have no authorization to do this operation
	public String[] GetAllNodeName(Configuration conf) {
		try {
			FileSystem fs = FileSystem.get(conf);
			DistributedFileSystem hdfs = (DistributedFileSystem) fs;
			DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();
			String[] names = new String[dataNodeStats.length];
			for (int i = 0; i < dataNodeStats.length; i++) {
				names[i] = dataNodeStats[i].getHostName();
			}
			return names;
		} catch (IOException e) {
			System.out.println("error!!!!");
			e.printStackTrace();
		}
		return null;
	}
}
