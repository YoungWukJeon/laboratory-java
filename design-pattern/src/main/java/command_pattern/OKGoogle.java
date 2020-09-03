package command_pattern;

public class OKGoogle {
    private static String[] modes = {"heater", "lamp"};

    private Heater heater;
    private Lamp lamp;
    private String mode;

    public OKGoogle(Heater heater, Lamp lamp) {
        this.heater = heater;
        this.lamp = lamp;
    }

    public void setMode(int idx) {
        this.mode = modes[idx];
    }

    public void talk() {
        switch (this.mode) {
            case "heater":
                heater.powerOn();
                break;
            case "lamp":
                lamp.turnOn();
                break;
        }
    }
}
