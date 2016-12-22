package com.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;

public class JFrameTest extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JFrameTest() {
		
		MyComboBox jComboBox1 = new MyComboBox();
		for (int i = 0; i < 10; i++) {
			MyCheckBox cValue = new MyCheckBox();
            cValue.value = "测试_" + i;
            if (i % 3 == 0) {
                cValue.bolValue = true;
            }
            jComboBox1.addItem(cValue);
        }
        jComboBox1.setRenderer(new CheckListCellRenderer());
        jComboBox1.setFont(new Font("Dialog", Font.PLAIN, 12));
		
		//整体容器
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(jComboBox1, BorderLayout.NORTH);
		
		this.setTitle("JFrameTest");
		this.setBounds(200,100,500,500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭退出jvm
		this.setVisible(true);
	}

	/**
	 * 把String转换成String[][]
	 * 其中字符串用^分行 用@分列
	 * */
	public String[][] changeTwoArray(String arg0){
		String[][] result = null;
		if(arg0!=null && arg0.length()>0){
			String[] a1 = arg0.split("\\^");
			result = new String[a1.length][];
			for(int i=0;i<a1.length;i++){
				String[] a2 = a1[i].split("@");
				result[i] = a2;
			}
		}
		return result;
	}
	/**
	 * 把String转换成String[]
	 * 其中字符串用^分割
	 * */
	public String[] changeOneArray(String arg0){
		String[] result = null;
		if(arg0!=null && arg0.length()>0){
			result = arg0.split("\\^");
		}
		return result;
	}
	/**
	 * 把String转换成ArrayList<String[][]>
	 * 其中字符串用/^分割
	 * 里面是个2维数组 字符串用^分行 用@分列
	 * */
	public ArrayList<String[][]> changeOtherOneArray(String arg0){
		ArrayList<String[][]> result = null;
		String[] resultArray = null;
		if(arg0!=null && arg0.length()>0){
			resultArray = arg0.split("/\\^");
			result = new ArrayList<String[][]>();
			for(int i=0;i<resultArray.length;i++){
				String tmp = resultArray[i];
				String tmparray[][] = changeTwoArray(tmp);
				result.add(tmparray);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		JFrameTest jt = new JFrameTest();
//		String a = "1@2";//"1@2^3@4^";
//		String[][] b = jt.changeTwoArray(a);
//		System.out.println("b.length:"+b.length);
//		for(int i=0;i<b.length;i++){
//			for(int j=0;j<b[i].length;j++)
//				System.out.print(b[i][j]+" ");
//			System.out.println();
//		}
		
		String a1 = "1@2/^3@4/^5@6";
		ArrayList<String[][]> b1 = jt.changeOtherOneArray(a1);
		for(int ix=0;ix<b1.size();ix++){
			String[][] btmp = b1.get(ix);
			for(int i=0;i<btmp.length;i++){
				for(int j=0;j<btmp[i].length;j++)
					System.out.print(btmp[i][j]+" ");
				System.out.println();
			}
			System.out.println("-");
		}
	}
}
