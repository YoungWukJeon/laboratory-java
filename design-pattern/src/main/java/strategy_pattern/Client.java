package strategy_pattern;

public class Client {
    public static void main(String[] args) {
        Actionable healedScv = new ActionHeal();
        Actionable repairedScv = new ActionRepair();

        healedScv.doAction();
        repairedScv.doAction();
    }
}
