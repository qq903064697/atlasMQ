package cn.atlas.atlasmq.broker.config;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午3:14
 * @Version 1.0
 */
public class GlobalProperties {

    // nameserver属性
    private String nameserverIp;
    private Integer nameserverPort;
    private String nameserverUser;
    private String nameserverPassword;
    private Integer brokerPort;

    /**
     * 读取环境变量中配置的mq存储绝对路径地址
     */
    private String atlasMqHome;

    public String getAtlasMqHome() {
        return atlasMqHome;
    }

    public void setAtlasMqHome(String atlasMqHome) {
        this.atlasMqHome = atlasMqHome;
    }

    public String getNameserverIp() {
        return nameserverIp;
    }

    public void setNameserverIp(String nameserverIp) {
        this.nameserverIp = nameserverIp;
    }

    public Integer getNameserverPort() {
        return nameserverPort;
    }

    public void setNameserverPort(Integer nameserverPort) {
        this.nameserverPort = nameserverPort;
    }

    public String getNameserverUser() {
        return nameserverUser;
    }

    public void setNameserverUser(String nameserverUser) {
        this.nameserverUser = nameserverUser;
    }

    public String getNameserverPassword() {
        return nameserverPassword;
    }

    public void setNameserverPassword(String nameserverPassword) {
        this.nameserverPassword = nameserverPassword;
    }

    public Integer getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(Integer brokerPort) {
        this.brokerPort = brokerPort;
    }
}
