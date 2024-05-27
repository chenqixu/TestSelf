package com.cqx.algorithm.bitmap;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public interface BitmapUv {
	
	public BitmapUv add(Long data);
	
	public long cardinality();
	
	public BitmapUv clear();
	
	public void serToFile(File file) throws  IOException ;
	
	public byte[] serToBytes()throws IOException;
	
	
	public void deserFromFile(File file) throws  IOException ;
	
	public void deserFromBytes(byte[] bs)throws IOException;
	
	
	
	public BitmapUv or(BitmapUv bitmapUv);
	
	public BitmapUv and(BitmapUv bitmapUv);
	
	public BitmapUv andNot(BitmapUv bitmapUv);
	
	public boolean contains(Long a);
	
	public static BitmapUv newBitmapUv(){
		return new RoaringBitmapUv();
	}

	Iterator<Long> iterator();
	
}
