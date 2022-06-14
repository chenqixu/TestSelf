package com.bussiness.bi.bigdata.searchcrawler.main;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class SNCraweler4 {
//	public void baidu(){	
//		// TODO Auto-generated method stub
//		WebDriver driver = new HtmlUnitDriver();
//		// 打开百度首页
//		driver.get("http://www.baidu.com/");
//		// 打印页面标题
//		System.out.println("页面标题：" + driver.getTitle());
//		// 根据id获取页面元素输入框
//		WebElement search = driver.findElement(By.id("kw"));
//		// 在id=“kw”的输入框输入“selenium”
//		search.sendKeys("selenium");
//		// 根据id获取提交按钮
//		WebElement submit = driver.findElement(By.id("su"));
//		// 点击按钮查询
//		submit.click();
//		// 打印当前页面标题
//		System.out.println("页面标题：" + driver.getTitle());
//		// 返回当前页面的url
//		System.out.println("页面url：" + driver.getCurrentUrl());
//		// 返回当前的浏览器的窗口句柄
//		System.out.println("窗口句柄：" + driver.getWindowHandle());
//	}
//	
//	public void suning(){
//		WebDriver driver = new HtmlUnitDriver();
//		driver.get("http://product.suning.com/123129015.html");
//		// 打印页面标题
//		System.out.println("页面标题：" + driver.getTitle());
////		System.out.println(driver.getPageSource());
//		File saveFile = new File("d:/Work/ETL/网络爬虫/suning_phone1.txt");
//		FileWriter fw = null;
//		BufferedWriter bw = null;
//		try{
//			if (saveFile.exists()) {
//				saveFile.delete();
//			}
//			fw = new FileWriter(saveFile);
//			bw = new BufferedWriter(fw);
//			bw.write(driver.getPageSource());
//			bw.flush();
//			bw.close();
//			fw.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		driver.close();
//		// 根据id获取页面元素
////		WebElement search = driver.findElement(By.id("promotionPrice"));
////		System.out.println("内容:"+search.getText());
//	}
//	
//	public static void main(String[] args) {
//		SNCraweler4 sc4 = new SNCraweler4();
//		sc4.suning();
//	}
}
