package think.A3;

import java.util.Random;

public class A38 {
	public static void main(String[] args) {
		// ģ����Ӳ��
		Random rand = new Random();
		for(int i=0;i<5;i++)
			System.out.println("[��"+i+"��ģ����Ӳ�ҽ��] "+(rand.nextInt(2)==0?"����":"����"));		
	}
}
