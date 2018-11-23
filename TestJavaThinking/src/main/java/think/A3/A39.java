package think.A3;

public class A39 {
	public static void main(String[] args) {
		// 直接常量
		int i0 = 000000000000000001100100; // 2 进制
		System.out.println("i0:"+Integer.toBinaryString(i0));
		int i1 = 0x2f; // 16进制
		System.out.println("i1:"+Integer.toBinaryString(i1));
		int i2 = 0x2F; // 16进制
		System.out.println("i2:"+Integer.toBinaryString(i2));
		int i3 = 0177; // 8进制
		System.out.println("i3:"+Integer.toBinaryString(i3));
		
		System.out.println("i1 Long.toBinaryString:"+Long.toBinaryString(i1));
		System.out.println("i3 Long.toBinaryString:"+Long.toBinaryString(i3));
		
		// 指数
		float expFloat = 1.39e-43f;
		expFloat = 1.39E-43f;
		System.out.println("[expFloat]"+expFloat);
		double expDouble = 47e47d;
		double expDouble2 = 47e47;
		System.out.println("[expDouble]"+expDouble);
		System.out.println("[expDouble2]"+expDouble2);
		
		float minnum = 1e-1f;
		double maxnum = Double.MAX_VALUE;
		System.out.println("minnum:"+minnum);
		System.out.println("maxnum:"+maxnum);
	}
}
