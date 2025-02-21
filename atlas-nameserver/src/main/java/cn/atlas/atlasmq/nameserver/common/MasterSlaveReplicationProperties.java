package cn.atlas.atlasmq.nameserver.common;

/**
 * @Author xiaoxin
 * @Description
 */
public class MasterSlaveReplicationProperties {

    private String master;

    private String role;

    private String type;

    private Integer port;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
