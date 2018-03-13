package com.cqx.algorithm.bitmap;

public class BitMapTest {
	//保存数据的
    private byte[] bits;
    
	//能够存储多少数据
    private int capacity;
    
    public BitMapTest(int capacity){
        this.capacity = capacity;
        
        //1bit能存储8个数据，那么capacity数据需要多少个bit呢，capacity/8+1,右移3位相当于除以8
        bits = new byte[(this.capacity >>3 )+1];
    }

    public void add(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3; 
        
        // num%8得到在byte[index]的位置
        int position = num & 0x07; 
        
        //将1左移position后，那个位置自然就是1，然后和以前的数据做|，这样，那个位置就替换成1了。
        bits[arrayIndex] |= 1 << position; 
    }
    
    public boolean contain(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3; 
        
        // num%8得到在byte[index]的位置
        int position = num & 0x07; 
        
        //将1左移position后，那个位置自然就是1，然后和以前的数据做&，判断是否为0即可
        return (bits[arrayIndex] & (1 << position)) !=0; 
    }
    
    public void clear(int num){
        // num/8得到byte[]的index
        int arrayIndex = num >> 3; 
        
        // num%8得到在byte[index]的位置
        int position = num & 0x07; 
        
        //将1左移position后，那个位置自然就是1，然后对取反，再与当前值做&，即可清除当前的位置了.
        bits[arrayIndex] &= ~(1 << position); 

    }

    public static void main(String[] args) {
    	BitMapTest bitmap = new BitMapTest(200);
    	int num = 171;
        bitmap.add(num);
        System.out.println("插入"+num+"成功");
        
        boolean isexsit = bitmap.contain(num);
        System.out.println(num+"是否存在:"+isexsit);
        
        bitmap.clear(num);
        isexsit = bitmap.contain(num);
        System.out.println(num+"是否存在:"+isexsit);
        
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int cnt = 0;
        System.out.println("begin "+df.format(new java.util.Date()));
        String str = "abcdefghijklmnopqrstuvwxyz";
        for(int i=0;i<40000000;i++){
        	String rstr = str.replaceAll("n", "1");
        	cnt++;
        	if(cnt%10000==0)System.out.println("deal "+cnt);
        }
        System.out.println("end "+df.format(new java.util.Date()));
        System.exit(1);
    }
}
