package strategy_pattern.strategy;

public class RepairStrategy implements ActionableStrategy {
    @Override
    public void doAction() {
        System.out.println("It repaired.");
    }
}
