package com.newland.bi.bigdata.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Jdom2Paser {
	
	/**
     * 解析xml文件
     * @throws Exception
     */
    public static void readXmlFile(Document doc) throws Exception {    
        Element root = doc.getRootElement(); //获取根元素
        
        System.out.println("---获取第一个子节点和子节点下面的节点信息------");
        Element e = root.getChild("person"); //获取第一个子元素
        System.out.println("person的属性id的值："+e.getAttributeValue("id")); //获取person的属性值
//        for(Element el: e.getChildren()){
//            System.out.println(el.getText());//第一次输入张三  第二次输出123123
//            System.out.println(el.getChildText("username"));//这里这么写会是null
//            System.out.println(el.getChildText("password"));//这里这么写会是null
//        }
        
        System.out.println("---直接在根节点下就遍历所有的子节点---");
        for(Element el: root.getChildren()){
//            System.out.println(el.getText());//这里输出4行空格
            System.out.println(el.getChildText("username"));//输出张三   & 1111111112
            System.out.println(el.getChildText("password"));//输出123123 &  password2
        }
    }
    
    /**
     * 新增节点
     * @throws Exception
     */
    public static void addXmlElement(Document doc) throws Exception {
        Element root = doc.getRootElement(); //获取根元素
        
        Element newEle = new Element("person");//设置新增的person的信息
        newEle.setAttribute("id","88888");
        
        Element chiledEle = new Element("username"); //设置username的信息
        chiledEle.setText("addUser");
        newEle.addContent(chiledEle);
        
        Element chiledEle2 = new Element("password"); //设置password的信息
        chiledEle2.setText("addPassword");
        newEle.addContent(chiledEle2);
        
        root.addContent(newEle);
        
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getCompactFormat().setEncoding("GBK"));//设置UTF-8编码,理论上来说应该不会有乱码，但是出现了乱码,故设置为GBK
        out.output(doc, new FileWriter("src/test.xml")); //写文件
    }
    
    /**
     * 更新节点
     * @param doc
     * @throws Exception
     */
    public static void updateXmlElement(Document doc) throws Exception {
        Element root = doc.getRootElement(); //获取根元素
        
        //循环person元素并修改其id属性的值
        for(Element el : root.getChildren("person")){
            el.setAttribute("id","haha");
        }
        //循环设置username和password的文本值和添加属性
        for(Element el: root.getChildren()){
            el.getChild("username").setAttribute("nameVal", "add_val").setText("update_text"); 
            el.getChild("password").setAttribute("passVal", "add_val").setText("update_text"); 
        }
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getCompactFormat().setEncoding("GBK"));//设置UTF-8编码,理论上来说应该不会有乱码，但是出现了乱码,故设置为GBK
        out.output(doc, new FileWriter("src/test.xml")); //写文件
    }
    
    /**
     * 删除节点和属性
     * @param doc
     * @throws Exception
     */
    public static void deleteXmlElement(Document doc) throws Exception {
        Element root = doc.getRootElement(); //获取根元素
        
        List<Element> personList = root.getChildren("person");
        
        //循环person元素,删除person的id为1的id属性以及username子节点
        for(Element el : personList){
            if(null!=el.getAttribute("id") && "1".equals(el.getAttribute("id").getValue())){
                el.removeAttribute("id");
                el.removeChild("username");
            }
        }
        
        //循环person元素,删除person的id为2的节点
        for(int i=0; i<personList.size(); i++){
            Element el = personList.get(i);
            if(null!=el.getAttribute("id") &&  "2".equals(el.getAttribute("id").getValue())){
                root.removeContent(el);//从root节点上删除该节点
                //警告：此处删除了节点可能会使personList的长度发生变化而发生越界错误,故不能写成for(int i=0,len=personList.size(); i<len; i++)
            }
        }

        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getCompactFormat().setEncoding("GBK"));//设置UTF-8编码,理论上来说应该不会有乱码，但是出现了乱码,故设置为GBK
        out.output(doc, new FileWriter("src/test.xml")); //写文件
    }
	
	public static void main(String[] args) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File("src/main/resources/conf/jdom2_test1.xml"));
//		Document doc = builder.build(new FileInputStream("src/jdom2_test1.xml"));
//		Document doc = builder.build(new FileReader("src/jdom2_test1.xml"));
//		Document doc = builder.build(new URL("http://localhost:8080/jdomTest/jdom2_test1.xml"));
//		Document doc = builder.build("src/jdom2_test1.xml");
		
		readXmlFile(doc);
//		addXmlElement(doc);
//		updateXmlElement(doc);
//		deleteXmlElement(doc);
	}
}
