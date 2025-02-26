package cn.atlas.atlasmq.broker.event.spi.listener;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.event.model.ConsumeMsgAckEvent;
import cn.atlas.atlasmq.broker.model.AtlasMqTopicModel;
import cn.atlas.atlasmq.broker.rebalance.ConsumerInstance;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.ConsumeMsgAckReqDTO;
import cn.atlas.atlasmq.common.dto.ConsumeMsgAckRespDTO;
import cn.atlas.atlasmq.common.enums.AckStatus;
import cn.atlas.atlasmq.common.enums.BrokerResponseCode;
import cn.atlas.atlasmq.common.event.Listener;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @Author xiaoxin
 * @description 消费端回应ack的监听器
 */
public class ConsumeMsgAckListener implements Listener<ConsumeMsgAckEvent> {
    private final Logger logger = LoggerFactory.getLogger(ConsumeMsgAckListener.class);

    @Override
    public void onReceive(ConsumeMsgAckEvent event) throws Exception {
        ConsumeMsgAckReqDTO consumeMsgAckReqDTO = event.getConsumeMsgAckReqDTO();
        String topic = consumeMsgAckReqDTO.getTopic();
        String consumeGroup = consumeMsgAckReqDTO.getConsumeGroup();
        Integer queueId = consumeMsgAckReqDTO.getQueueId();
        Integer ackCount = consumeMsgAckReqDTO.getAckCount();
        ConsumeMsgAckRespDTO consumeMsgAckRespDTO = new ConsumeMsgAckRespDTO();
        consumeMsgAckRespDTO.setMsgId(event.getMsgId());
        AtlasMqTopicModel atlasMqTopicModel = CommonCache.getAtlasMqTopicModelMap().get(topic);
        if (atlasMqTopicModel == null) {
            //topic不存在，ack失败
            consumeMsgAckRespDTO.setAckStatus(AckStatus.FAIL.getCode());
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(),
                    JSON.toJSONBytes(consumeMsgAckRespDTO)));
            return;
        }
        Map<String, List<ConsumerInstance>> consumerInstanceMap = CommonCache.getConsumerHoldMap().get(topic);
        if (consumerInstanceMap == null || consumerInstanceMap.isEmpty()) {
            consumeMsgAckRespDTO.setAckStatus(AckStatus.FAIL.getCode());
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(),
                    JSON.toJSONBytes(consumeMsgAckRespDTO)));
            return;
        }
        List<ConsumerInstance> consumeGroupInstances = consumerInstanceMap.get(consumeGroup);
        if (CollectionUtils.isEmpty(consumeGroupInstances)) {
            consumeMsgAckRespDTO.setAckStatus(AckStatus.FAIL.getCode());
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(),
                    JSON.toJSONBytes(consumeMsgAckRespDTO)));
            return;
        }
        String currentConsumeReqId = consumeMsgAckReqDTO.getIp() + ":" + consumeMsgAckReqDTO.getPort();
        ConsumerInstance matchInstance = consumeGroupInstances.stream().filter(item -> {
            return item.getConsumerReqId().equals(currentConsumeReqId);
        }).findAny().orElse(null);
        if (matchInstance == null) {
            consumeMsgAckRespDTO.setAckStatus(AckStatus.FAIL.getCode());
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(),
                    JSON.toJSONBytes(consumeMsgAckRespDTO)));
            return;
        }
        //数据的ack，到底应该客户端传递offset过来好 还是在服务端计算offset值好？
        for (int i = 0; i < ackCount; i++) {
            CommonCache.getConsumerQueueConsumeHandler().ack(topic, consumeGroup, queueId);
        }
        logger.info("broker receive offset value ,topic is {},consumeGroup is {},queueId is {},ackCount is {}",
                topic, consumeGroup, queueId, ackCount);
        consumeMsgAckRespDTO.setAckStatus(AckStatus.SUCCESS.getCode());
        TcpMsg tcpMsg = new TcpMsg(BrokerResponseCode.BROKER_UPDATE_CONSUME_OFFSET_RESP.getCode(),
                JSON.toJSONBytes(consumeMsgAckRespDTO));
        event.getChannelHandlerContext().writeAndFlush(tcpMsg);
    }
}
