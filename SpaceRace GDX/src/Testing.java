public class Testing {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		Person alex = new Person("Alex", 18);
		
		String x = "name";
		System.out.println(alex.getClass().getDeclaredField(x).toString());
		
		//alex.getClass().getF
		
		/*for(Method m : alex.getClass().getMethods()) {
			System.out.println(m.toGenericString());
		}*/
		
		System.out.println("Done");
	}
}

class Person {
	private String name;
	private int age;
	
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}
	
	public void woof() {
		System.out.println(name + ": Meow");
	}
}