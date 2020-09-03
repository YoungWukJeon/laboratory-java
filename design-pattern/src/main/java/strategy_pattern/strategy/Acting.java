package strategy_pattern.strategy;

public class Acting {
    private ActionableStrategy actionableStrategy;

    public void doAction() {
        actionableStrategy.doAction();
    }

    void setActionableStrategy(ActionableStrategy actionableStrategy) {
        this.actionableStrategy = actionableStrategy;
    }
}
