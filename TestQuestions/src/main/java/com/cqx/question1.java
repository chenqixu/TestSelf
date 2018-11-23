package com.cqx;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <b>问题1</b><br>
 * 小明和小张，小明削苹果5秒，剥荔枝2秒，吃苹果30秒，吃荔枝5秒；小张削苹果6秒，剥荔枝3秒，吃苹果25秒，吃荔枝4秒；<br>
 * 现有苹果xx，荔枝xx，小明削苹果，小张剥荔枝，小明吃小张剥的荔枝，小张吃小明削的苹果<br>
 * <b>规则：</b>吃完一个才能削（剥）下一个，削（剥）之后如果有吃的要先吃；<br>
 * 请问，苹果xx，荔枝xx多少，小明先削完，或 小张先完。
 * */
public class question1 {
	public static void main(String[] args) {
		int apple = 5;
		int litchi = 20;
		Queue<String> appleQueue = new ConcurrentLinkedQueue<String>();
		Queue<String> litchiQueue = new ConcurrentLinkedQueue<String>();
		Queue<String> peel1Queue = new ConcurrentLinkedQueue<String>();
		Queue<String> peel2Queue = new ConcurrentLinkedQueue<String>();
		for(int i=0;i<apple;i++){
			appleQueue.add("apple");
		}
		for(int i=0;i<litchi;i++){
			litchiQueue.add("litchi");
		}
		new XiaomingThread(appleQueue, litchiQueue, peel1Queue, peel2Queue).start();
		new XiaozhangThread(litchiQueue, appleQueue, peel2Queue, peel1Queue).start();
	}
}

class XiaomingThread extends Thread {
	private String fruits1;
	private String fruits2;
	private Queue<String> fruits1Queue;
	private Queue<String> fruits2Queue;
	private Queue<String> peelQueue;
	private Queue<String> eatQueue;
	private Person p;
	public XiaomingThread(Queue<String> _fruits1Queue, Queue<String> _fruits2Queue,
			Queue<String> _peelQueue, Queue<String> _eatQueue){
		this.fruits1Queue = _fruits1Queue;
		this.fruits2Queue = _fruits2Queue;
		this.peelQueue = _peelQueue;
		this.eatQueue = _eatQueue;
		p = new Xiaoming();
	}
	@Override
	public void run(){
		while((fruits1=fruits1Queue.poll())!=null || !fruits2Queue.isEmpty()){
			if(fruits1!=null){
				p.peel(fruits1);
				synchronized(peelQueue){
					peelQueue.add(fruits1);
				}
			}
			while((fruits2=eatQueue.poll())!=null){
				p.eat(fruits2);
				break;
			}
		}
	}
}

class XiaozhangThread extends Thread {
	private String fruits1;
	private String fruits2;
	private Queue<String> fruits1Queue;
	private Queue<String> fruits2Queue;
	private Queue<String> peelQueue;
	private Queue<String> eatQueue;
	private Person p;
	public XiaozhangThread(Queue<String> _fruits1Queue, Queue<String> _fruits2Queue,
			Queue<String> _peelQueue, Queue<String> _eatQueue){
		this.fruits1Queue = _fruits1Queue;
		this.fruits2Queue = _fruits2Queue;
		this.peelQueue = _peelQueue;
		this.eatQueue = _eatQueue;
		p = new Xiaozhang();		
	}
	@Override
	public void run(){
		while((fruits1=fruits1Queue.poll())!=null || !fruits2Queue.isEmpty()){
			if(fruits1!=null){
				p.peel(fruits1);
				synchronized(peelQueue){
					peelQueue.add(fruits1);
				}
			}
			while((fruits2=eatQueue.poll())!=null){
				p.eat(fruits2);
				break;
			}
		}
	}
}

interface Person {
	void eat(String fruits);
	void peel(String fruits);
}

class Xiaoming implements Person {
	int sleeps = 0;
	@Override
	public void eat(String fruits) {
		System.out.println(new Date()+" Xiaoming eat "+fruits+" begin...");
		if(fruits.equals("apple")){
			sleeps = 30000;
		}else if(fruits.equals("litchi")){
			sleeps = 5000;
		}
		try {
			Thread.sleep(sleeps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date()+" Xiaoming eat "+fruits+" end.");
	}

	@Override
	public void peel(String fruits) {
		System.out.println(new Date()+" Xiaoming peel "+fruits+" begin...");
		if(fruits.equals("apple")){
			sleeps = 5000;
		}else if(fruits.equals("litchi")){
			sleeps = 2000;
		}
		try {
			Thread.sleep(sleeps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date()+" Xiaoming peel "+fruits+" end.");
	}
}

class Xiaozhang implements Person {
	int sleeps = 0;
	@Override
	public void eat(String fruits) {
		System.out.println(new Date()+" Xiaozhang eat "+fruits+" begin...");
		if(fruits.equals("apple")){
			sleeps = 25000;
		}else if(fruits.equals("litchi")){
			sleeps = 4000;
		}
		try {
			Thread.sleep(sleeps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date()+" Xiaozhang eat "+fruits+" end.");
	}

	@Override
	public void peel(String fruits) {
		System.out.println(new Date()+" Xiaozhang peel "+fruits+" begin...");
		if(fruits.equals("apple")){
			sleeps = 6000;
		}else if(fruits.equals("litchi")){
			sleeps = 3000;
		}
		try {
			Thread.sleep(sleeps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date()+" Xiaozhang peel "+fruits+" end.");
	}
}
