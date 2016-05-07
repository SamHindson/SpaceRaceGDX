public class Testing {

    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalAccessException {
        Dude alex = new Dude();
        alex.aeg = 69;

        System.out.println(Dude.class.getDeclaredField("aeg").getFloat(alex));
    }
}

class Dude {
    int aeg;
}