package cn.atlas.atlasmq.client.producer;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.*;
import cn.atlas.atlasmq.common.enums.*;
import cn.atlas.atlasmq.common.remote.BrokerNettyRemoteClient;
import cn.atlas.atlasmq.common.remote.NameServerNettyRemoteClient;
import cn.atlas.atlasmq.common.utils.AssertUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 提供基础的mq发送接口
 */
public class DefaultProducerImpl implements Producer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProducerImpl.class);

    //连接nameserver，给nameserver发送心跳，拉取broker地址
    //与broker建立连接，发送数据给到broker节点

    private String nsIp;
    private Integer nsPort;
    private String nsUser;
    private String nsPwd;
    //broker地址会有多个？broker节点会有多个，水平扩展的效果，水平扩展（存储内容会增加，承载压力也会大大增加，节点的选择问题）
    private List<String> brokerAddressList;
    private NameServerNettyRemoteClient nameServerNettyRemoteClient;
    private Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap = new ConcurrentHashMap<>();

    public String getNsIp() {
        return nsIp;
    }

    public void setNsIp(String nsIp) {
        this.nsIp = nsIp;
    }

    public Integer getNsPort() {
        return nsPort;
    }

    public void setNsPort(Integer nsPort) {
        this.nsPort = nsPort;
    }

    public String getNsUser() {
        return nsUser;
    }

    public void setNsUser(String nsUser) {
        this.nsUser = nsUser;
    }

    public String getNsPwd() {
        return nsPwd;
    }

    public void setNsPwd(String nsPwd) {
        this.nsPwd = nsPwd;
    }

    public Map<String, BrokerNettyRemoteClient> getBrokerNettyRemoteClientMap() {
        return brokerNettyRemoteClientMap;
    }

    public void setBrokerNettyRemoteClientMap(Map<String, BrokerNettyRemoteClient> brokerNettyRemoteClientMap) {
        this.brokerNettyRemoteClientMap = brokerNettyRemoteClientMap;
    }

    public NameServerNettyRemoteClient getNameServerNettyRemoteClient() {
        return nameServerNettyRemoteClient;
    }

    public void setNameServerNettyRemoteClient(NameServerNettyRemoteClient nameServerNettyRemoteClient) {
        this.nameServerNettyRemoteClient = nameServerNettyRemoteClient;
    }

    public List<String> getBrokerAddressList() {
        return brokerAddressList;
    }

    public void setBrokerAddressList(List<String> brokerAddressList) {
        this.brokerAddressList = brokerAddressList;
    }

    public void start() {
        nameServerNettyRemoteClient = new NameServerNettyRemoteClient(nsIp, nsPort);
        nameServerNettyRemoteClient.buildConnection();
        boolean isRegistrySuccess = this.doRegistry();
        if (isRegistrySuccess) {
            this.startHeartBeatTask();
            //拉broker ip地址
            this.fetchBrokerAddress();
            //连接到broker节点上
            this.connectBroker();
        }
    }

    /**
     * 连接broker程序
     */
    private void connectBroker() {
        AssertUtils.isNotEmpty(this.getBrokerAddressList(), "broker地址不能为空");
        for (String brokerAddress : brokerAddressList) {
            String[] brokerAddressArr = brokerAddress.split(":");
            BrokerNettyRemoteClient brokerNettyRemoteClient = new BrokerNettyRemoteClient(brokerAddressArr[0],
                    Integer.valueOf(brokerAddressArr[1]));
            brokerNettyRemoteClient.buildConnection();
            this.getBrokerNettyRemoteClientMap().put(brokerAddress, brokerNettyRemoteClient);
        }
    }

    /**
     * 开启注册
     * @return
     */
    private boolean doRegistry() {
        String registryMsgId = UUID.randomUUID().toString();
        ServiceRegistryReqDTO serviceRegistryReqDTO = new ServiceRegistryReqDTO();
        serviceRegistryReqDTO.setMsgId(registryMsgId);
        serviceRegistryReqDTO.setUser(nsUser);
        serviceRegistryReqDTO.setPassword(nsPwd);
        serviceRegistryReqDTO.setRegistryType(RegistryTypeEnum.PRODUCER.getCode());
        TcpMsg tcpMsg = new TcpMsg(NameServerEventCode.REGISTRY.getCode(), JSON.toJSONBytes(serviceRegistryReqDTO));
        TcpMsg registryResponse = nameServerNettyRemoteClient.sendSyncMsg(tcpMsg, registryMsgId);
        if (NameServerResponseCode.REGISTRY_SUCCESS.getCode() == registryResponse.getCode()) {
            return true;
        } else {
            logger.error("注册账号失败");
            return false;
        }
    }

    private void startHeartBeatTask() {
        Thread heartBeatTask = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        logger.info("执行心跳数据发送");
                        String heartBeatMsgId = UUID.randomUUID().toString();
                        HeartBeatDTO heartBeatDTO = new HeartBeatDTO();
                        heartBeatDTO.setMsgId(heartBeatMsgId);
                        TcpMsg heartBeatResponse = nameServerNettyRemoteClient.sendSyncMsg(new TcpMsg(NameServerEventCode.HEART_BEAT.getCode(),
                                JSON.toJSONBytes(heartBeatDTO)), heartBeatMsgId);
                        logger.info("heart beat response data is :{}", JSON.parseObject(heartBeatResponse.getBody()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "heart-beat-task");
        heartBeatTask.start();
    }

    public void fetchBrokerAddress() {
        String fetchBrokerAddressMsgId = UUID.randomUUID().toString();
        PullBrokerIpDTO pullBrokerIpDTO = new PullBrokerIpDTO();
        pullBrokerIpDTO.setRole("single");
        pullBrokerIpDTO.setMsgId(fetchBrokerAddressMsgId);
        TcpMsg heartBeatResponse = nameServerNettyRemoteClient.sendSyncMsg(new TcpMsg(NameServerEventCode.PULL_BROKER_IP_LIST.getCode(),
                JSON.toJSONBytes(pullBrokerIpDTO)), fetchBrokerAddressMsgId);
        //获取broker节点ip地址，并且缓存起来，可能由多个master-broker角色
        PullBrokerIpRespDTO pullBrokerIpRespDTO = JSON.parseObject(heartBeatResponse.getBody(), PullBrokerIpRespDTO.class);
        this.setBrokerAddressList(pullBrokerIpRespDTO.getAddressList());
        logger.info("fetch broker address:{}", this.getBrokerAddressList());
    }


    @Override
    public SendResult send(MessageDTO messageDTO) {
        // 路由的关系（topic -》 定位到具体的broker实例的ip）
        BrokerNettyRemoteClient remoteClient = this.getBrokerNettyRemoteClientMap().values().stream().collect(Collectors.toList()).get(0);
        String msgId = UUID.randomUUID().toString();
        messageDTO.setMsgId(msgId);
        messageDTO.setSendWay(MessageSendWay.SYNC.getCode());
        TcpMsg tcpMsg = new TcpMsg(BrokerEventCode.PUSH_MSG.getCode(), JSON.toJSONBytes(messageDTO));
        TcpMsg responseMsg = remoteClient.sendSyncMsg(tcpMsg, msgId);
        SendMessageToBrokerResponseDTO sendMessageToBrokerResponseDTO  = JSON.parseObject(responseMsg.getBody(), SendMessageToBrokerResponseDTO.class);
        int responseStatus = sendMessageToBrokerResponseDTO.getStatus();
        SendResult sendResult = new SendResult();
        if (responseStatus == 0) {
            sendResult.setSendStatus(SendStatus.SUCCESS);
        } else if (responseStatus == 1) {
            sendResult.setSendStatus(SendStatus.FAIL);
        }
        return sendResult;
    }

    @Override
    public void sendAsync(MessageDTO messageDTO) {
        BrokerNettyRemoteClient remoteClient = this.getBrokerNettyRemoteClientMap().values().stream().collect(Collectors.toList()).get(0);
        messageDTO.setSendWay(MessageSendWay.ASYNC.getCode());
        TcpMsg tcpMsg = new TcpMsg(BrokerEventCode.PUSH_MSG.getCode(), JSON.toJSONBytes(messageDTO));
        remoteClient.sendAsyncMsg(tcpMsg);
    }
}
