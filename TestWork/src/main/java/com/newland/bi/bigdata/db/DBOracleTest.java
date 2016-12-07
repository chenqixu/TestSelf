package com.newland.bi.bigdata.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.sql.BLOB;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
//					File tmpfile = new File("H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\"+id+".zip");
//					FileOutputStream fout = new FileOutputStream(tmpfile);
//					byte b[] = null; // 保存从BLOB读出的字节
//					b = blob_result.getBytes(1, (int)blob_result.getLength()); // 从BLOB取出字节流数据					
//                  fout.write(b, 0, b.length); // 将从BLOB读出的字节写入文件
//                  fout.close(); 
//                  tmpfile = null;
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
//			byte[] tmpb = blob_result.getBytes();
			InputStream is = blob_result.getBinaryStream();
			int b = 0;
			String xmlhead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><configuration>";
			fout.write(xmlhead.getBytes());
			while((b=is.read())>-1) {
				fout.write(b);
			}
			String xmlend = "</configuration>";
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
		String path = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\data.zip";
		String path1 = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456";
		String path2 = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456.zip";
		String xmlPath = "H:\\Work\\WorkSpace\\MyEclipse10\\self\\test\\src\\main\\resources\\data\\123456.xml";
		DBOracleTest dbot = new DBOracleTest();
//		int code = dbot.insertBlob(path);
//		System.out.println("[resultcode]"+code);
//		dbot.done();
//		dbot.readByteStringToFile(path1, path2);
//		dbot.readXmlToFile(xmlPath, path2);
		// 销毁对象
		dbot.finalize();
	}
}
