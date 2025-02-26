package cn.atlas.atlasmq.nameserver.event.model;

import cn.atlas.atlasmq.common.event.model.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description 注册事件（首次链接nameserver使用）
 */
public class RegistryEvent extends Event {

    private String registryType;
    private String user;
    private String password;
    private String ip;
    private Integer port;
    private Map<String,Object> attrs = new HashMap<>();

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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


}
