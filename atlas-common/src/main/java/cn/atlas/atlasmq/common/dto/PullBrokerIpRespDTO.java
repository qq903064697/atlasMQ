package cn.atlas.atlasmq.common.dto;

import java.util.List;

/**
 * @Author xiaoxin
 * @Description
 */
public class PullBrokerIpRespDTO extends BaseNameServerRemoteDTO{

    private List<String> addressList;

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }
}
