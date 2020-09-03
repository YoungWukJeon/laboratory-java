package strategy_pattern;

public class ActionHeal implements Actionable {
    @Override
    public void doAction() {
        System.out.println("It healed.");
    }
}
