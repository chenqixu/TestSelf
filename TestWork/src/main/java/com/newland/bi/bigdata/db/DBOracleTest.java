package com.newland.bi.bigdata.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.sql.BLOB;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.newland.bi.bigdata.utils.OtherUtils;

import org.apache.oozie.util.IOUtils;

import com.newland.bi.bigdata.db.DataSourceUtils;

public class DBOracleTest {	
	private Connection conn = null;
	private Statement st = null;
	private PreparedStatement pst = null;
	private ResultSet rs = null;
	
	/**
	 * 构造函数
	 * */
	public DBOracleTest(){
		init();
	}
	
	/**
	 * 构造函数，传入oracle版本
	 * */
	public DBOracleTest(int version){
		switch (version){
		case 11:
			DataSourceUtils.setDbURL("jdbc:oracle:thin:@10.1.0.242:1521:ywxx");
			DataSourceUtils.setDbUserName("bassweb");
			DataSourceUtils.setDbPassword("bassweb");
			DataSourceUtils.setLocal(false); //不使用同一密码
			DataSourceUtils.setPool(false); //不使用连接池
			break;
		case 12:
			DataSourceUtils.setDbURL("jdbc:oracle:thin:@10.1.8.79:1521/edc_etl_pri");
			DataSourceUtils.setDbUserName("edc_etl_col");
			DataSourceUtils.setDbPassword("edc_etl_col");
			DataSourceUtils.setLocal(false); //不使用同一密码
			DataSourceUtils.setPool(false); //不使用连接池
			break;
		default:
			break;
		}
		init();
	}
	
	/**
	 * 类释放
	 * */
	@Override
	protected void finalize() {
		try {
			System.out.println("finalize...");
			// 关闭连接池
			DataSourceUtils.shutdownDataSource(DataSourceUtils.getConfSource());
			// 调用超类中的finalize方法
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 初始化
	 * */
	protected void init(){
		// 初始连接池
		DataSourceUtils.initConfigure();		
	}
	
	/**
	 * 通过SQL进行查询
	 * */
	public void querySQL(String sql){
		try {
			// 获得连接
			conn = DataSourceUtils.getConnection();
			st = conn.createStatement();
			// 执行查询语句
			rs = st.executeQuery(sql);
			// 打印行数
			System.out.println(rs.getRow());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			DataSourceUtils.release(conn, st, rs);
		}
	}
	
	/**
	 * 设置参数
	 * */
    private void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }
	
    /**
     * 查询
     * */
	public List<Map<String, Object>> executeQuery(String sql, List<Object> parameters)
            throws SQLException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// 获得连接
			conn = DataSourceUtils.getConnection();
			stmt = conn.prepareStatement(sql);
			setParameters(stmt, parameters);
			rs = stmt.executeQuery();
			ResultSetMetaData rsMeta = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
					String columName = rsMeta.getColumnLabel(i + 1);
					Object value = rs.getObject(i + 1);
					row.put(columName, value);
				}
				rows.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			DataSourceUtils.release(null, st, rs);
		}
		return rows;
	}
	
	/**
	 * 查询blob，进行操作
	 * */
	public void done(){
		try {
			// 获得连接
			conn = DataSourceUtils.getConnection();
			String sql = "select id,fstream from extern_flowtask_cfg";
			st = conn.createStatement();
			// 执行查询语句
			rs = st.executeQuery(sql);
			// 获得查询结果
			while (rs.next() != false) {
				BigDecimal id = rs.getBigDecimal(1);
				BLOB blob_result = (BLOB) rs.getBlob(2);
				if(blob_result!=null && (int)blob_result.getLength()>0 &&
						blob_result.getBytes(1, (int)blob_result.getLength())!=null) {
					System.out.println(id);
//					System.out.println(new String(blob_result.getBytes(1, (int)blob_result.getLength())));
					saveBlobToByteString(blob_result, "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\"+id+".xml");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			DataSourceUtils.release(conn, st, rs);
		}
	}	
	
	/**
	 * 查询blob，进行操作
	 * */
	public void query_wf_files(String filePath, String savefilepath){
		try {
			// 获得连接
			String sql = "select content from wf_files where path=?";
			List<Object> list = new ArrayList<Object>();
			list.add(filePath);
			List<Map<String, Object>> listResult = executeQuery(sql, list);
			if (listResult != null && !listResult.isEmpty()) {
	            saveBlobToByteString(((BLOB) listResult.get(0).get("CONTENT")),
	            		savefilepath+OtherUtils.getCurrentPid()+".xml");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			DataSourceUtils.release(conn, null, null);
		}
	}
	
	public void query_wf_files(String filePath){
		String workflowXml = "";
		try {
//			Reader reader = new InputStreamReader(getFileStreamFromDb(filePath, "oracle"));
//			StringWriter writer = new StringWriter();
//			IOUtils.copyCharStream(reader, writer);
//			workflowXml = writer.toString();
			workflowXml = getStreamBytes(getFileStreamFromDb(filePath, "oracle"), "UTF-8");
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("##workflowXml##"+workflowXml);
	}
	
	/**
	 * 从流中按byte[]读取
	 * */
	public String getStreamBytes(InputStream is, String charset) {
		byte[] buffer = new byte[1024];
		StringBuffer sb = new StringBuffer();
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, len, charset));
//				for(int i = 0;i<len; i++){
//					System.out.println(buffer[i]);
//				}
//				break;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 从数据库从获取文件流
	 * */
	public InputStream getFileStreamFromDb(String filePath, String dbtype) throws SQLException {
		// 编写SQL：
		String sql = "select CONTENT from wf_files t where t.path =?";
		List<Object> list = new ArrayList<Object>();
		list.add(filePath);
		List<Map<String, Object>> listResult = executeQuery(sql, list);
		if (listResult != null && !listResult.isEmpty()) {
            Blob blob;
            if (dbtype.equalsIgnoreCase("oracle")){
				InputStream is = ((BLOB) listResult.get(0).get("CONTENT")).binaryStreamValue();
				return is;
            }else{
                blob = new SerialBlob((byte[]) listResult.get(0).get("CONTENT"));
            }
			if (blob != null) {
				return blob.getBinaryStream();
			}
		}
		return null;
	}
	
	/**
	 * 插入blob<br>
	 * SQL: insert into extern_flowtask_cfg values(123456,'10.1.8.3',5553,'',EMPTY_BLOB(),sysdate,'');
	 * */
	public int insertBlob(String path){
		int resultcode = -1;
		File tmpfile = null;
		FileInputStream finput = null;
		try {
			// 获得连接
			conn = DataSourceUtils.getConnection();
			String sql = "insert into wf_files values(sys_guid(),?,?,?,sysdate)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, "102607117304@2018080101000000");
			pst.setString(2, "/home/edc_base/edc-app/xml/102607117304@2018080101000000/workflow.xml");
			tmpfile = new File(path);
			finput = new FileInputStream(tmpfile);
			pst.setBlob(3, finput);
			resultcode = pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件输入流
			if(finput!=null){
				try {
					finput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 释放文件对象
			tmpfile = null;
			// 释放连接
			DataSourceUtils.release(conn, pst, null);
		}
		return resultcode;
	}
	
	/**
	 * 更新blob<br>
	 * SQL: insert into extern_flowtask_cfg values(123456,'10.1.8.3',5553,'',EMPTY_BLOB(),sysdate,'');
	 * */
	public int updateBlob(String path){
		int resultcode = -1;
		File tmpfile = null;
		FileInputStream finput = null;
		try {
			// 获得连接
			conn = DataSourceUtils.getConnection();
			String sql = "update extern_flowtask_cfg set fstream=? where id=? ";
			pst = conn.prepareStatement(sql);
			tmpfile = new File(path);
			finput = new FileInputStream(tmpfile);
			pst.setBlob(1, finput);
			pst.setInt(2, 123456);
			resultcode = pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件输入流
			if(finput!=null){
				try {
					finput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 释放文件对象
			tmpfile = null;
			// 释放连接
			DataSourceUtils.release(conn, pst, null);
		}
		return resultcode;
	}
	
	/**
	 * 把文件保存成byte字符串
	 * */
	public void saveFileToByteString(String path){
		
	}

	/**
	 * 把blob保存成byte字符串
	 * */
	public void saveBlobToByteString(BLOB blob_result, String path){
		File tmpfile = null;
		FileOutputStream fout = null;
		try {
			tmpfile = new File(path);
			fout = new FileOutputStream(tmpfile);
			InputStream is = blob_result.getBinaryStream();
			int b = 0;
			String xmlhead = "";
//			String xmlhead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><configuration>";
			fout.write(xmlhead.getBytes());
			while((b=is.read())>-1) {
				fout.write(b);
			}
			String xmlend = "";
//			String xmlend = "</configuration>";
			fout.write(xmlend.getBytes());
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件输出流
			if(fout!=null){
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 释放文件对象
			tmpfile = null;
		}
	}
	
	/**
	 * 读取byte字符串还原成文件
	 * */
	public void readByteStringToFile(String path1, String path2){
		File tmpfile1 = null;
		File tmpfile2 = null;
		FileInputStream finput = null;
		FileOutputStream fout = null;
		try {
			tmpfile1 = new File(path1);
			tmpfile2 = new File(path2);
			finput = new FileInputStream(tmpfile1);
			fout = new FileOutputStream(tmpfile2);
			int b = 0;
			while((b = finput.read())!=-1) {
				fout.write(b);
			}					
			fout.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件输出流
			if(finput!=null){
				try {
					finput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 关闭文件输出流
			if(fout!=null){
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// 释放文件对象
			tmpfile1 = null;
			tmpfile2 = null;
		}		
	}
	
	/**
	 * 读取xml文件中的byte部分，保存成为文件
	 * */
	public void readXmlToFile(String xmlPath, String outPath){
		try {
			File f = new File(xmlPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
			DocumentBuilder builder = factory.newDocumentBuilder();   
			Document doc = builder.parse(f);   
			NodeList nl = doc.getElementsByTagName("configuration");
			System.out.println(nl.item(1).getNodeValue());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		String path = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\data.zip";
//		String path1 = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456";
//		String path2 = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456.zip";
//		String xmlPath = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456.xml";
		String path = "D:\\tmp\\workflow.xml";
		DBOracleTest dbot = new DBOracleTest(12);
//		dbot.query_wf_files("/home/edc_base/edc-app/xml/103663752343@2018080201000000/workflow.xml", "d:\\");
//		dbot.query_wf_files("/home/edc_base/edc-app/xml/103663752343@2018080201000000/workflow.xml");
		dbot.query_wf_files("/home/edc_base/edc-app/xml/102607117304@2018080101000000/workflow.xml");
//		int code = dbot.insertBlob(path);
//		System.out.println("[resultcode]"+code);
//		dbot.done();
//		dbot.readByteStringToFile(path1, path2);
//		dbot.readXmlToFile(xmlPath, path2);
//		// 销毁对象
//		dbot.finalize();
//		// 测试12c和11g能否共用1个jdbc包
//		DBOracleTest dbot12 = new DBOracleTest(12);
//		dbot12.querySQL("select * from sm2_user");
//		dbot12.finalize();
//		DBOracleTest dbot11 = new DBOracleTest(11);
//		dbot11.querySQL("select * from sm_user");
//		dbot11.finalize();
	}
}
