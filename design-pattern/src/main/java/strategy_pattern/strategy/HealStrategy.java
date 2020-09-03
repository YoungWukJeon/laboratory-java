package strategy_pattern.strategy;

public class HealStrategy implements ActionableStrategy {
    @Override
    public void doAction() {
        System.out.println("It healed.");
    }
}
