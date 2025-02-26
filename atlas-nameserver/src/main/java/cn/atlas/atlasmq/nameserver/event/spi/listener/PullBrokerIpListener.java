package cn.atlas.atlasmq.nameserver.event.spi.listener;

import cn.atlas.atlasmq.common.event.Listener;
import com.alibaba.fastjson.JSON;
import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.dto.PullBrokerIpRespDTO;
import cn.atlas.atlasmq.common.enums.NameServerResponseCode;
import cn.atlas.atlasmq.common.enums.RegistryTypeEnum;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.enums.PullBrokerIpRoleEnum;
import cn.atlas.atlasmq.nameserver.event.model.PullBrokerIpEvent;
import cn.atlas.atlasmq.nameserver.store.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author idea
 * @Date: Created in 16:48 2024/6/11
 * @Description
 */
public class PullBrokerIpListener implements Listener<PullBrokerIpEvent> {

    @Override
    public void onReceive(PullBrokerIpEvent event) throws Exception {
        String pullRole = event.getRole();
        PullBrokerIpRespDTO pullBrokerIpRespDTO = new PullBrokerIpRespDTO();
        List<String> addressList = new ArrayList<>();
        Map<String, ServiceInstance> serviceInstanceMap = CommonCache.getServiceInstanceManager().getServiceInstanceMap();
        for (String reqId : serviceInstanceMap.keySet()) {
            ServiceInstance serviceInstance = serviceInstanceMap.get(reqId);
            if(RegistryTypeEnum.BROKER.getCode().equals(serviceInstance.getRegistryType())){
                Map<String,Object> brokerAttrs = serviceInstance.getAttrs();
                String role = (String) brokerAttrs.get("role");
                if (PullBrokerIpRoleEnum.MASTER.getCode().equals(pullRole)
                        && PullBrokerIpRoleEnum.MASTER.getCode().equals(role)) {
                    addressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                } else if (PullBrokerIpRoleEnum.SLAVE.getCode().equals(pullRole)
                        && PullBrokerIpRoleEnum.SLAVE.getCode().equals(role)) {
                    addressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                } else if (PullBrokerIpRoleEnum.SINGLE.getCode().equals(pullRole)
                        && PullBrokerIpRoleEnum.SINGLE.getCode().equals(role)) {
                    addressList.add(serviceInstance.getIp()+":"+serviceInstance.getPort());
                }
            }
        }
        pullBrokerIpRespDTO.setMsgId(event.getMsgId());
        pullBrokerIpRespDTO.setAddressList(addressList);
        event.getChannelHandlerContext().writeAndFlush(new TcpMsg(NameServerResponseCode.PULL_BROKER_ADDRESS_SUCCESS.getCode(),
                JSON.toJSONBytes(pullBrokerIpRespDTO)));
    }
}
