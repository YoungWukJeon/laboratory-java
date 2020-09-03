package templete_method_pattern;

public class Client {
    public static void main(String[] args) {
        ChildA childA = new ChildA();
        childA.someMethod();

        System.out.println("----------");

        ChildB childB = new ChildB();
        childB.someMethod();
    }
}
