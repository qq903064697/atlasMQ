package cn.atlas.atlasmq.broker.event.spi.listener;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.event.model.ConsumerMsgEvent;
import cn.atlas.atlasmq.broker.model.ConsumerQueueConsumeReqModel;
import cn.atlas.atlasmq.broker.rebalance.ConsumerInstance;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.ConsumerMsgBaseRespDTO;
import cn.atlas.atlasmq.common.dto.ConsumerMsgReqDTO;
import cn.atlas.atlasmq.common.dto.ConsumerMsgRespDTO;
import cn.atlas.atlasmq.common.enums.BrokerResponseCode;
import cn.atlas.atlasmq.common.event.Listener;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 消费者拉取消息监听器
 */
public class ConsumerMsgListener implements Listener<ConsumerMsgEvent> {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerMsgListener.class);

    @Override
    public void onReceive(ConsumerMsgEvent event) throws Exception {
        ConsumerMsgReqDTO consumerMsgReqDTO = event.getConsumerMsgReqDTO();
        String currentReqId = consumerMsgReqDTO.getIp() + ":" + consumerMsgReqDTO.getPort();
        String topic = consumerMsgReqDTO.getTopic();
        ConsumerInstance consumerInstance = new ConsumerInstance();
        consumerInstance.setIp(consumerMsgReqDTO.getIp());
        consumerInstance.setPort(consumerMsgReqDTO.getPort());
        consumerInstance.setConsumerReqId(currentReqId);
        consumerInstance.setTopic(consumerMsgReqDTO.getTopic());
        consumerInstance.setConsumerGroup(consumerMsgReqDTO.getConsumerGroup());
        consumerInstance.setBatchSize(10);
        //加入到消费池中
        CommonCache.getConsumerInstancePool().addInstancePool(consumerInstance);
        ConsumerMsgBaseRespDTO consumerMsgBaseRespDTO = new ConsumerMsgBaseRespDTO();
        List<ConsumerMsgRespDTO> consumerMsgRespDTOS = new ArrayList<>();
        consumerMsgBaseRespDTO.setConsumerMsgRespDTOList(consumerMsgRespDTOS);
        consumerMsgBaseRespDTO.setMsgId(event.getMsgId());
        Map<String, List<ConsumerInstance>> consumerGroupMap = CommonCache.getConsumerHoldMap().get(topic);
        //有可能当前消费组还没经过第一轮重平衡，因此不会那么快消费到数据,所以要通知客户端，目前服务端还没将队列分配好
        if(consumerGroupMap==null) {
            //直接返回空数据
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.CONSUME_MSG_RESP.getCode(),
                    JSON.toJSONBytes(consumerMsgBaseRespDTO)));
            return;
        }
        List<ConsumerInstance> consumerInstances = consumerGroupMap.get(consumerMsgReqDTO.getConsumerGroup());
        if(CollectionUtils.isEmpty(consumerInstances)) {
            //直接返回空数据
            event.getChannelHandlerContext().writeAndFlush(new TcpMsg(BrokerResponseCode.CONSUME_MSG_RESP.getCode(),
                    JSON.toJSONBytes(consumerMsgBaseRespDTO)));
            return;
        }
        for (ConsumerInstance instance : consumerInstances) {
            if(instance.getConsumerReqId().equals(currentReqId)) {
                //当前消费者有占有队列的权利,可以消费
                for (Integer queueId : instance.getQueueIdSet()) {
                    ConsumerQueueConsumeReqModel consumerQueueConsumeReqModel = new ConsumerQueueConsumeReqModel();
                    consumerQueueConsumeReqModel.setTopic(topic);
                    consumerQueueConsumeReqModel.setQueueId(queueId);
                    consumerQueueConsumeReqModel.setBatchSize(instance.getBatchSize());
                    consumerQueueConsumeReqModel.setConsumerGroup(instance.getConsumerGroup());
                    List<byte[]> commitLogContentList = CommonCache.getConsumerQueueConsumeHandler().consume(consumerQueueConsumeReqModel);
                    ConsumerMsgRespDTO consumerMsgRespDTO = new ConsumerMsgRespDTO();
                    consumerMsgRespDTO.setQueueId(queueId);
                    consumerMsgRespDTO.setCommitLogContentList(commitLogContentList);
                    consumerMsgRespDTOS.add(consumerMsgRespDTO);
                }
            }
        }
        byte[] bytes = consumerMsgBaseRespDTO.getConsumerMsgRespDTOList().get(0).getCommitLogContentList().get(0);
        logger.info("content:{}", new String(bytes));
        TcpMsg tcpMsg = new TcpMsg(BrokerResponseCode.CONSUME_MSG_RESP.getCode(), JSON.toJSONBytes(consumerMsgBaseRespDTO));
        event.getChannelHandlerContext().writeAndFlush(tcpMsg);
    }
}
