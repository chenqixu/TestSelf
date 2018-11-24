package com.newland.bi.bigdata.gc;

public class GCTest {
	private Person pp;
	
	public void test() {
		Person p1 = new Person("张三", 19);
		test2(p1);
	}
	
	public void test2(Person p) {
		System.out.println(p);
//		pp = p;
		try {
			pp = (Person) p.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public Person getPerson() {
		return this.pp;
	}
	
	class Person {
		String name;
		int age;
		public Person(String name, int age) {
			this.name = name;
			this.age =age;
		}
		
		@Override
		public String toString() {
			return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " "
					+ this.name + " 有 " + this.age + " 岁了。";
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			Object object = super.clone();
			return object;
		}
	}
	
	public static void main(String[] args) {
		GCTest g1= new GCTest();
		g1.test();
		System.out.println(g1.getPerson());
	}
}
