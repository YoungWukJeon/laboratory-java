package strategy_pattern;

public class ActionRepair implements Actionable {
    @Override
    public void doAction() {
        System.out.println("It repaired.");
    }
}
