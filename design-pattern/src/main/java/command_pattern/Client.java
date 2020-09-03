package command_pattern;

public class Client {
    public static void main(String[] args) {
        Heater heater = new Heater();
        Lamp lamp = new Lamp();
        OKGoogle okGoogle = new OKGoogle(heater, lamp);

        // 히터 켜짐
        okGoogle.setMode(0);
        okGoogle.talk();

        // 램프 켜짐
        okGoogle.setMode(1);
        okGoogle.talk();
    }
}
