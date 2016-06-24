import java.lang.reflect.InvocationTargetException;

public class Testing {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Dude alex = new Dude();

		Dude.class.getDeclaredMethod("setAeg", int.class).invoke(alex, 17);
	}
}

class Dude {
	private int aeg = 450;

	public int getAeg() {
		return aeg;
	}
	
	public void setAeg(int aeg) {
		this.aeg = aeg;
	}
	
	public void sayHi(String message) {
		System.out.println("Alex says " + message);
	}
}