package think.A3;

public class A37 {
	public static void main(String[] args) {
		Dog g1=new Dog();
		g1.name="spot";
		g1.says="Ruff!";
		Dog g2=new Dog();
		g2.name="scruffy";
		g2.says="Wruf!";
		
		System.out.println(g1.name+" "+g1.says);
		System.out.println(g2.name+" "+g2.says);
		
		Dog g3=g1;
		System.out.println("[g1==g3]"+(g1==g3));
		System.out.println("[g1.equals(g3)]"+(g1.equals(g3)));
	}
}

class Dog {
	String name;
	String says;
}
