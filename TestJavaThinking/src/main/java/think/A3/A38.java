package think.A3;

import java.util.Random;

public class A38 {
	public static void main(String[] args) {
		// 模拟扔硬币
		Random rand = new Random();
		for(int i=0;i<5;i++)
			System.out.println("[第"+i+"次模拟扔硬币结果] "+(rand.nextInt(2)==0?"正面":"反面"));		
	}
}
