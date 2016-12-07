package com.newland.bi.bigdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.newland.bi.bigdata.bean.Person;

public class MemoryOutPut {
	public static void main(String[] args) {
		// Set<Person> set = new HashSet<Person>();
		// Person p1 = new Person("唐僧","pwd1",25);
		// Person p2 = new Person("孙悟空","pwd2",26);
		// Person p3 = new Person("猪八戒","pwd3",27);
		// System.out.println(p3.hashCode());
		// set.add(p1);
		// set.add(p2);
		// set.add(p3);
		// System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:3 个元素!
		// p3.setAge(2); //修改p3的年龄,此时p3元素对应的hashcode值发生改变
		// System.out.println(p3.hashCode());
		//
		// // set.remove(p3); //此时remove不掉，造成内存泄漏
		//
		// set.add(p3); //重新添加，居然添加成功
		// System.out.println("总共有:"+set.size()+" 个元素!"); //结果：总共有:4 个元素!
		// for (Person person : set){
		// System.out.println(person.hashCode());
		// }

		Map<Key, String> map = new HashMap<Key, String>(1000);

		int counter = 0;
		while (true) {
			// creates duplicate objects due to bad Key class
			map.put(new Key("dummyKey"), "value");
			counter++;
			if (counter % 1000 == 0) {
				System.out.println("map size: " + map.size());
				System.out.println("Free memory after count " + counter
						+ " is " + getFreeMemory() + "MB");

				sleep(1000);
			}
		}
	}

	// delay for a given period in milli seconds
	public static void sleep(long sleepFor) {
		try {
			Thread.sleep(sleepFor);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// get available memory in MB
	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory() / (1024 * 1024);
	}

	// inner class key without hashcode() or equals() -- bad implementation
	static class Key {
		private String key;

		public Key(String key) {
			this.key = key;
		}
	}
}
