package strategy_pattern.strategy;

public class Client {
    public static void main(String[] args) {
        Acting healedScv = new ActionHeal();
        Acting repairedScv = new ActionRepair();

        healedScv.setActionableStrategy(new HealStrategy());
        repairedScv.setActionableStrategy(new RepairStrategy());

        healedScv.doAction();
        repairedScv.doAction();

        repairedScv.setActionableStrategy(new HealStrategy());
        repairedScv.doAction();
    }
}
