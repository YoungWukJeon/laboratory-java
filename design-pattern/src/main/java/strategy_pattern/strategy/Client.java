package strategy_pattern.strategy;

public class Client {
    public static void main(String[] args) {
        Acting healedScv = new ActionHeal();
        Acting repairedScv = new ActionRepair();

        healedScv.setRepairableStrategy(new HealStrategy());
        repairedScv.setRepairableStrategy(new RepairStrategy());

        healedScv.doAction();
        repairedScv.doAction();

        repairedScv.setRepairableStrategy(new HealStrategy());
        repairedScv.doAction();
    }
}
