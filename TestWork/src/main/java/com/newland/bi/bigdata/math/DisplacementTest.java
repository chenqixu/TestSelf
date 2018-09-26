package com.newland.bi.bigdata.math;

public class DisplacementTest {
	
	/**
	 * 输出一个int的二进制数
	 * 
	 * @param num
	 */
	public static void printInfo(int num) {
		System.out.println(Integer.toBinaryString(num));
	}
	
	public static void main(String[] args) {
		int number = 10;
		//原始数二进制
		printInfo(number);
		//左移一位
		number = number << 1;
		printInfo(number);
		//右移一位
		number = number >> 1;
		printInfo(number);
	}
}
