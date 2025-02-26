package cn.atlas.atlasmq.broker.rebalance.strategy;

/**
 * @Author xiaoxin
 * @Description
 */
public interface IReBalanceStrategy {

    /**
     * 根据策略执行重分配
     *
     * @param reBalanceInfo
     */
    void doReBalance(ReBalanceInfo reBalanceInfo);
}
