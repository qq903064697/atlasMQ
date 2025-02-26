package cn.atlas.atlasmq.common.dto;

/**
 * @Author xiaoxin
 * @Description
 */
public class PullBrokerIpDTO extends BaseNameServerRemoteDTO{

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
