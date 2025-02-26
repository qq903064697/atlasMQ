package cn.atlas.atlasmq.client.consumer;

import cn.atlas.atlasmq.common.enums.ConsumerResultStatus;

/**
 * @Author idea
 * @Date: Created in 11:06 2024/6/16
 * @Description
 */
public class ConsumerResult {

    /**
     * 消费结果
     */
    private int consumeResultStatus;

    public int getConsumeResultStatus() {
        return consumeResultStatus;
    }

    public void setConsumeResultStatus(int consumeResultStatus) {
        this.consumeResultStatus = consumeResultStatus;
    }

    public ConsumerResult(int consumeResultStatus) {
        this.consumeResultStatus = consumeResultStatus;
    }

    public static ConsumerResult CONSUME_SUCCESS() {
        return new ConsumerResult(ConsumerResultStatus.CONSUMER_SUCCESS.getCode());
    }

    public static ConsumerResult CONSUME_LATER() {
        return new ConsumerResult(ConsumerResultStatus.CONSUMER_LATER.getCode());
    }
}
