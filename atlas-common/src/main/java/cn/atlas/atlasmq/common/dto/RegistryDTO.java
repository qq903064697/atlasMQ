package cn.atlas.atlasmq.common.dto;

import java.io.Serializable;

/**
 * @Author xiaoxin
 * @Description
 */
public class RegistryDTO implements Serializable {

    private String user;
    private String password;
    private String brokerIp;
    private Integer brokerPort;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }
}
