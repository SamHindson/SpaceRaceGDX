import java.lang.reflect.InvocationTargetException;

public class Testing {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Dude bruh = new Dude(69);

        bruh.getClass().getDeclaredConstructor(int.class).newInstance(13);
    }
}

class Dude {
	private int aeg = 450;

    public Dude(int aeg) {
        this.aeg = aeg;
        System.out.println("Sweet");
    }

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