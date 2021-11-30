package com.newland.bi.bigdata.ftp;

import com.enterprisedt.net.ftp.*;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.enterprisedt.net.ftp.pro.ProFTPClientInterface;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.ftp.ssh.SSHFTPInputStream;
import com.enterprisedt.net.ftp.ssh.SSHFTPOutputStream;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;
import com.enterprisedt.util.license.License;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FtpUtil {
	private String host = "";
	private String user = "";
	private String pwd = "";
	private int ftpPort = 21;// ftp默认端口
	private int sftpPort = 22;// sftp默认端口
	private int timeout = 0;
	private long start = System.currentTimeMillis();
	private long end = 0;
	private String contorlCharset = null;

	private org.slf4j.Logger logger = LoggerFactory.getLogger(FtpUtil.class);
	private ProFTPClientInterface client = null;

	public FtpUtil(String host, String user, String pwd) {
		this(host, user, pwd, 21,120);
	}

	public FtpUtil(String host, String user, String pwd, int port, int timeout) {
		this(host, user, pwd, port, timeout, Level.ERROR);
	}

	public FtpUtil(String host, String user, String pwd, int port, int timeout, Level level) {
		this.host = host;
		this.user = user;
		this.pwd = pwd;
		this.ftpPort = port;
		this.sftpPort = port;
		this.timeout = timeout * 1000;
		initLicense(level);
	}

//	public void setLog(Logger log) {
//		this.log = log;
//	}

	/**
	 * 初始化证书信息以及设置日志级别
	 */
	private void initLicense(Level level) {
		License.setLicenseDetails("hello", "371-2454-4908-7510");
		Logger.setLevel(level);
	}

	/**
	 * 连接远程服务器
	 * 
	 * @throws Exception
	 *             ftp连接异常
	 */
	public void connectServer() throws Exception {
		logger.info("ftp开始连接:" + host + ":" + ftpPort);

		ProFTPClient proFTPClient = new ProFTPClient();
		client = proFTPClient;
		proFTPClient.setRemoteHost(host);
		proFTPClient.setRemotePort(ftpPort);
		proFTPClient.setTimeout(timeout);
		if (contorlCharset != null) {
			proFTPClient.setControlEncoding(contorlCharset);
		}
		proFTPClient.connect();
		proFTPClient.login(user, pwd);
		setBinaryMode();
	

		logger.info("ftp连接成功");
	}

	/**
	 * 关闭FTP客户端连接
	 * 
	 * @return 关闭成功标识
	 */
	public void disconnect() {
		logger.info("ftp 关闭连接中");

		try {
			client.quit();
			logger.info("ftp 连接关闭成功");
		} catch (Exception e) {
		}
	}

	/**
	 * 用SFTP方式连接FTP服务器
	 * 
	 * @throws Exception
	 *             ftp连接异常
	 */
	public void connectServerBySFTP() throws Exception {
		logger.info("开始连接sftp " + host + ":" + sftpPort);
		SSHFTPClient sshftpClient = new SSHFTPClient();
		sshftpClient.setRemoteHost(host);
		sshftpClient.setRemotePort(sftpPort);
		sshftpClient.getValidator().setHostValidationEnabled(false);
		sshftpClient.setAuthentication(user, pwd);
		sshftpClient.setTimeout(timeout);
		sshftpClient.connect();
		if (contorlCharset != null) {
			sshftpClient.setControlEncoding(contorlCharset);
		}
		client = sshftpClient;
		logger.info("成功连接" + host);
	}

	/**
	 * 用主动方式，数据端口随机
	 *
	 * @throws Exception
	 *             ftp设置主被动异常
	 */
	public void setActiveMode() throws Exception {
		if (client instanceof ProFTPClient) {
			logger.info("设置ftp为主动模式");
			((ProFTPClient) client).setConnectMode(FTPConnectMode.ACTIVE);
		}

	}

	/**
	 * 用主动方式，数据端口指定
	 * 
	 * @param startPort
	 *            起始端口 必须大于1024
	 * @param endPort
	 *            结束端口 必须小于65535
	 * @throws Exception
	 *             ftp异常
	 */
	public void setActiveModeWithPortRange(int startPort, int endPort) throws Exception {
		if (client instanceof ProFTPClient) {
			logger.info("设置ftp为主动模式，数据端口范围:" + startPort + "-" + endPort);
			((ProFTPClient) client).setActivePortRange(startPort, endPort);
			((ProFTPClient) client).setConnectMode(FTPConnectMode.ACTIVE);
		}
	}

	/**
	 * 设置ftp为被动模式
	 * 
	 * @throws Exception
	 *             ftp异常
	 */
	public void setPssiveMode() throws Exception {
		if (client instanceof ProFTPClient) {
			logger.info("设置ftp为被动模式");
			((ProFTPClient) client).setConnectMode(FTPConnectMode.PASV);
		}
	}

	/**
	 * 用二进制方式传输
	 * 
	 * @throws Exception
	 *             ftp异常
	 */
	public void setBinaryMode() throws Exception {
		if (client instanceof ProFTPClient) {
			logger.info("设置ftp为二进制模式");
			((ProFTPClient) client).setType(FTPTransferType.BINARY);
		}
	}

	/**
	 * 设置字符集(此方法必须在连接ftp之前调用才生效)
	 * 
	 * @param charset
	 *            字符集
	 */
	public void setContorlCharset(String charset) {
		logger.info("设置字符集");
		contorlCharset = charset;
	}

	/**
	 * 用ASCII方式传输
	 * 
	 * @throws Exception
	 *             ftp异常
	 */
	public void setASCIIMode() throws Exception {
		if (client instanceof ProFTPClient) {
			logger.info("设置ftp为被动模式");
			((ProFTPClient) client).setType(FTPTransferType.ASCII);
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @param localFileName
	 *            本地文件名
	 * @param remoteFileName
	 *            远程文件加密
	 * @throws Exception
	 *             ftp异常
	 */
	public void fileUpload(String localPath, String remotePath, String localFileName, String remoteFileName) throws Exception {
		chkConnection();
		localPath = covertPath(localPath);
		remotePath = covertPath(remotePath);
		logger.info("开始上传文件:" + localPath + localFileName);
		// 上传文件
		client.put(localPath + localFileName, remotePath + remoteFileName);
		logger.info("文件上传成功:" + localFileName);
	}

	/**
	 * 下载文件
	 * 
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @param localFileName
	 *            本地文件名
	 * @param remoteFileName
	 *            远程文件名
	 * @throws Exception
	 *             ftp异常
	 */
	public void fileDownload(String localPath, String remotePath, String localFileName, String remoteFileName) throws Exception {
		chkConnection();
		localPath = covertPath(localPath);
		remotePath = covertPath(remotePath);
		logger.info("开始下载文件：" + remotePath + remoteFileName);

		// 下载文件
		client.get(localPath + localFileName, remotePath + remoteFileName);
		logger.info("下载文件成功:" + localFileName);
	}

	/**
	 * 根据文件正则表达式批量上传
	 * 
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @param fileName
	 *            文件名规则
	 * @return ftpp返回状态
	 * @throws FTPException
	 *             ftp异常
	 * @throws IOException
	 *             io异常
	 */
	public FtpUtilRespInfo fileUploadByReg(String localPath, String remotePath, String fileName) throws FTPException, IOException {
		chkConnection();
		FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
		localPath = covertPath(localPath);
		remotePath = covertPath(remotePath);
		try {
			logger.info("开始批量上传文件：" + fileName);
			File dir = new File(localPath);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (matchFile(file.getName(), fileName)) {
					info.setTotalCount();
					try {
						client.put(file.getAbsolutePath(), remotePath + file.getName());
						info.setSuccessCount();
						logger.info("成功上传文件:" + file.getAbsolutePath());
					} catch (Exception e) {
						info.setFailCount();
						logger.error("上传文件异常:" + file.getAbsolutePath(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error("批量上传文件异常", e);
		}
		return info;
	}

	/**
	 * 根据文件正则表达式批量下载
	 * 
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @param fileName
	 *            文件名规则
	 * @return ftpp返回状态
	 * @throws FTPException
	 *             ftp异常
	 * @throws IOException
	 *             io异常
	 */
	public FtpUtilRespInfo fileDownloadByReg(String localPath, String remotePath, String fileName) throws FTPException, IOException {
		chkConnection();
		FtpUtilRespInfo info = new FtpUtilRespInfo();// 记录上传信息
		localPath = covertPath(localPath);
		remotePath = covertPath(remotePath);
		logger.info("开始批量下载文件:" + fileName);
		try {
			FTPFile[] files = client.dirDetails(remotePath);
			for (FTPFile ftpFile : files) {
				if (matchFile(ftpFile.getName(), fileName)) {
					try {
						info.setTotalCount();
						client.get(localPath + ftpFile.getName(), remotePath + ftpFile.getName());
						info.setSuccessCount();
						logger.info("成功下载文件：" + localPath + ftpFile.getName());
					} catch (Exception e) {
						info.setFailCount();
						logger.error("下载文件失败：" + remotePath + ftpFile.getName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("下载文件异常:" + host + "---" + remotePath + fileName, e);
		}

		return info;
	}

	/**
	 * 根据路径，找出所有符合规则的文件，支持通配符* 和？
	 * 
	 * @param remoteDir
	 *            远程目录
	 * @param fileName
	 *            文件名规则 如：aa_*.txt
	 * @return 文件名列表
	 * @throws Exception
	 *             ftp异常
	 */
	public List<String> getFileNames(String remoteDir, String fileName) throws Exception {
		chkConnection();
		logger.info("扫描目标目录参数:" + remoteDir);
		ArrayList<String> fileNames = new ArrayList<String>();
		List<String> pathList = getPaths(null, remoteDir.split("/"));
		logger.info("解析后目标目录:" + pathList);
		for (String path : pathList) {

			logger.info("开始扫描目录" + path);
			FTPFile[] files = client.dirDetails(path);// 获取目录下的所有文件和文件夹
			logger.info("扫描到文件和子目录数" + files.length);
			int num = 0;
			// 遍历所有文件名，找出与fileName匹配的文件
			for (FTPFile ftpFile : files) {
				String name = ftpFile.getName();
				if (!ftpFile.isDir() && matchFile(name, fileName)) {
					fileNames.add(path + name);
					num++;
				}
			}
			logger.info("筛选后符合条件的文件数:" + num);
		}
		return fileNames;
	}

	/**
	 * 根据通配符获取所有路径
	 * 
	 * @param allPath
	 *            要遍历的路径
	 * @return 路径列表
	 * @throws Exception
	 *             ftp异常
	 */
	public List<String> getPaths(List<String> allPath, String[] pathArr) throws Exception {
		chkConnection();
		if (allPath == null) {
			allPath = new ArrayList<String>();
			allPath.add("");
		}
		List<String> paths = new ArrayList<String>();
		for (String path : allPath) {

			String str = pathArr[0];
			if (str.equals("")) {
				path += "/";
				paths.add(path);
			} else if (str.contains("*") || str.contains("?")) {
				FTPFile[] p = client.dirDetails(path);
				for (FTPFile ftpFile : p) {
					if (ftpFile.isDir() && matchFile(ftpFile.getName(), str)) {
						String pa = path + ftpFile.getName() + "/";
						paths.add(pa);
					}
				}
			} else {
				path += str + "/";
				paths.add(path);
			}
		}
		if (pathArr.length > 1) {
			paths = getPaths(paths, Arrays.copyOfRange(pathArr, 1, pathArr.length));
		}
		return paths;
	}

	/**
	 * 重命名文件，移动文件
	 * 
	 * @param srcPath
	 *            源路径
	 * @param srcFile
	 *            源文件名称
	 * @param tgtPath
	 *            目标路径
	 * @param tgtName
	 *            目标路径名称
	 * @throws Exception
	 *             ftp异常
	 */
	public void rename(String srcPath, String srcFile, String tgtPath, String tgtName) throws Exception {
		chkConnection();
		srcPath = covertPath(srcPath);
		tgtPath = covertPath(tgtPath);
		try {
			client.mkdir(tgtPath);
		} catch (Exception e) {
			logger.info("目录已经存在，不再创建 dir:" + tgtPath);
		}

		client.rename(srcPath + srcFile, tgtPath + tgtName);
	}

	/**
	 * 删除文件或目录，当文件名为空的时候删除目录
	 * 
	 * @param path
	 *            文件路径
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 *             ftp异常
	 */
	public void delete(String path, String fileName) throws Exception {
		chkConnection();
		path = covertPath(path);
		if (fileName != null && !fileName.equals("")) {
			client.delete(path + fileName);
		} else {
			client.rmdir(path, true);
		}
	}

	/**
	 * 删除文件或者目录
	 * 
	 * @param filePathName
	 *            文件绝对路径
	 * @throws Exception
	 *             ftp异常
	 */
	public void delete(String filePathName) throws Exception {
		chkConnection();
		if (!StringUtils.isEmpty(filePathName)) {
			client.delete(filePathName);
		}
	}

	/**
	 * 看path最后一个字符是否为/，若不是则补上
	 * 
	 * @param path
	 *            路径名
	 * @return 转换后的路径
	 */
	private String covertPath(String path) {
		if (path == null) {
			return "";
		}
		path = path.trim();
		char lastStr = path.charAt(path.length() - 1);
		if (lastStr != '/') {
			path = path + "/";
		}
		return path;
	}

	/**
	 * 根据通配符来匹配对应的文件 通配符 * ？
	 * 
	 * @param realFileName
	 *            真是文件名 如 123.txt
	 * @param fileName
	 *            要匹配的文件名 如 *.txt
	 * @return true表示匹配，false表示不匹配
	 */
	public boolean matchFile(String realFileName, String fileName) {
		fileName = fileName.replace('.', '#');
		fileName = fileName.replaceAll("#", "\\\\.");
		fileName = fileName.replace('*', '#');
		fileName = fileName.replaceAll("#", ".*");
		fileName = fileName.replace('?', '#');
		fileName = fileName.replaceAll("#", ".?");
		fileName = "^" + fileName + "$";

		Pattern p = Pattern.compile(fileName);
		Matcher fMatcher = p.matcher(realFileName);
		return fMatcher.matches();
	}

	/**
	 * 下载文件，返回文件输入流
	 *
	 * @param filePath
	 *            远程文件路径
	 * @param fileName
	 *            远程文件名
	 * @return 远程文件输入流
	 * @throws Exception
	 *             ftp异常
	 */
	public InputStream downloadStream(String filePath, String fileName) throws Exception {
		String path = covertPath(filePath);
		return downloadStream(path + fileName);
	}

	/**
	 * 下载文件，返回文件输入流
	 * 
	 * @param filePath
	 *            文件的全路径
	 * @return 文件输入流
	 * @throws IOException
	 *             io异常
	 * @throws FTPException
	 *             ftp异常
	 */
	public InputStream downloadStream(String filePath) throws FTPException, IOException {
		return downloadStream(filePath, 1);
	}

	/**
	 * 下载文件，返回输入流
	 * 
	 * @param filePath
	 * @param count
	 *            重试次数，最大三次
	 * @return 输入流
	 * @throws FTPException
	 *             ftp异常
	 * @throws IOException
	 *             io异常
	 */
	private InputStream downloadStream(String filePath, int count) throws FTPException, IOException {
		chkConnection();
		try {
			if (client instanceof ProFTPClient) {
				return new BufferedInputStream(new FTPInputStream((ProFTPClient) client, filePath));
			} else {
				return new BufferedInputStream(new SSHFTPInputStream((SSHFTPClient) client, filePath));
			}
		} catch (IOException e) {
			logger.warn("下载异常", e);
			if (count < 4) {
				logger.info("ftp采集文件IO异常，重试" + count + "次");
				start = 0;// 让ftp重新链接
				return downloadStream(filePath, count + 1);
			} else {
				logger.error("重试3次失败，文件：" + filePath, e);
				throw e;
			}
		}
	}

	/**
	 * 用io流的方式上传文件
	 * 
	 * @param remotePath
	 *            远程地址
	 * @param fileName
	 *            远程文件名
	 * @return 文件输出流
	 * @throws Exception
	 *             ftp异常
	 */
	public OutputStream uploadStream(String remotePath, String fileName) throws Exception {
		chkConnection();
		String path = covertPath(remotePath);
		createDirctorys(path);
		if (client instanceof ProFTPClient) {
			return new BufferedOutputStream(new FTPOutputStream((ProFTPClient) client, path + fileName));
		} else {
			return new BufferedOutputStream(new SSHFTPOutputStream((SSHFTPClient) client, path + fileName));
		}
	}

	/**
	 * 创建目录，支持多级目录创建 ftp 和sftp判断目录是否存在的方式不一样
	 * 
	 * @param path
	 *            待创建的路径
	 * @throws FTPException
	 *             ftp异常
	 * @throws IOException
	 *             io异常
	 */
	public void createDirctorys(String path) throws FTPException, IOException {

		if (client instanceof ProFTPClient) {
			createFtpDir(path);
		} else {
			createSftpDir(path);
		}
	}

	/**
	 * 创建sftp目录
	 * 
	 * @param path
	 *            待创建路径
	 * @throws IOException
	 *             io异常
	 * @throws FTPException
	 *             ftp异常
	 */
	private void createSftpDir(String path) throws IOException, FTPException {

		try {
			if (exists(path)) {
				return;
			}
		} catch (FTPException e) {// 多层目录不存在也会抛异常
			logger.warn("目录不存在", e);
		}
		String[] paths = path.split("/");
		String tmpPath = "";
		// 遍历创建目录
		for (int i = 1; i < paths.length; i++) {
			tmpPath += "/" + paths[i];
			if (!client.exists(tmpPath)) {
				client.mkdir(tmpPath);
			}

		}

	}

	/**
	 * 创建ftp目录,ftp api暂时没有判断目录是否存在的方法
	 * 
	 * @param path
	 *            文件路径
	 * @throws IOException
	 *             io异常
	 */
	private void createFtpDir(String path) throws IOException, FTPException {

		if(!exists(path)){
			String[] paths = path.split("/");
			String tmpPath = "";
			// 遍历创建目录
			for (int i = 1; i < paths.length; i++) {
				tmpPath += "/" + paths[i];
				if(!exists(tmpPath)){
					client.mkdir(tmpPath);
				}
			}
		}
	}

    /**
     * 判断文件是否存在
     * @param filePath 路径
     * @return 存在返回true 失败返回false
     * @throws IOException
     * @throws FTPException
     */
    public boolean exists(String filePath) throws IOException, FTPException {
		if (client instanceof ProFTPClient) {//ftp api单独判断文件或者目录是否存在
			ProFTPClient cli = (ProFTPClient)client;
			return cli.existsFile(filePath)||cli.existsDirectory(filePath);
		} else {//sftp客户端可直接判断
			return client.exists(filePath);
		}
    }
	/**
	 * 检查ftp连接是否过期，若过期重新创建，防止sockt连接超时
	 *
	 * @throws IOException
	 *             io异常
	 * @throws FTPException
	 *             ftp异常
	 */
	public void chkConnection() throws FTPException, IOException {
		end = System.currentTimeMillis();
		long comTime = 0;
		if (timeout == 0) // 除以9表示在时间达到90%的时候，重新连接，以防再后续环境连接超时。
		{// vsftp服务端 操作默认超时600秒
			comTime = 600000 / 9;
		} else {
			comTime = timeout / 9;
		}
		// 超时，重新获取ftp连接
		if ((end - start) >= comTime) {
			disconnect();
			try {
			    if(client instanceof  ProFTPClient){
                    connectServer();
                }else {
			        connectServerBySFTP();
                }
			} catch (Exception e) {
				logger.info(e.getMessage());
				for (int i = 0; i < 10; i++)// 重连10次
				{
					try {
                        if(client instanceof  ProFTPClient){
                            connectServer();
                        }else {
                            connectServerBySFTP();
                        }
						break;
					} catch (Exception e2) {
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e1) {
							logger.error("sleep 异常：", e1);
						}
					}
				}
			}

			start = System.currentTimeMillis();
		}
	}

	public class FtpUtilRespInfo {
		private int totalCount = 0;
		private int successCount = 0;
		private int failCount = 0;

		public int getTotalCount() {
			return totalCount;
		}

		public void setTotalCount() {
			this.totalCount++;
		}

		public int getSuccessCount() {
			return successCount;
		}

		public void setSuccessCount() {
			this.successCount++;
		}

		public int getFailCount() {
			return failCount;
		}

		public void setFailCount() {
			this.failCount++;
		}

		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}

		public void setSuccessCount(int successCount) {
			this.successCount = successCount;
		}

		public void setFailCount(int failCount) {
			this.failCount = failCount;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public int getSftpPort() {
		return sftpPort;
	}

	public void setSftpPort(int sftpPort) {
		this.sftpPort = sftpPort;
	}

	public ProFTPClientInterface getClient() {
		return client;
	}

	public void setClient(ProFTPClientInterface client) {
		this.client = client;
	}
}
