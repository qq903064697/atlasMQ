package cn.atlas.atlasmq.nameserver.event.model;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 注册事件（首次链接nameserver使用）
 */
public class RegistryEvent extends Event {

    private String user;
    private String password;
    private String brokerIp;
    private Integer brokerPort;

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

    @Override
    public String toString() {
        return "RegistryEvent{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", brokerIp='" + brokerIp + '\'' +
                ", brokerPort=" + brokerPort +
                '}';
    }
}
