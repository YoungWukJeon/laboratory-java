package command_pattern.command;

public class HeaterOnCommand implements Command {
    private Heater heater;

    public HeaterOnCommand(Heater heater) {
        this.heater = heater;
    }

    @Override
    public void run() {
        heater.powerOn();
    }
}
