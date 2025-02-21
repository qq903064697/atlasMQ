package cn.atlas.atlasmq.nameserver.core;

import cn.atlas.atlasmq.nameserver.common.CommonCache;
import cn.atlas.atlasmq.nameserver.common.MasterSlaveReplicationProperties;
import cn.atlas.atlasmq.nameserver.common.NameserverProperties;
import cn.atlas.atlasmq.nameserver.common.TraceReplicationProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午3:09
 * @Version 1.0
 */
public class PropertiesLoader {
    private Properties properties = new Properties();
    public void loadProperties() throws IOException {
        String atlasMqHome = "D:/Sync/Study/项目/atlasMQ/代码/atlasmq";
        properties.load(new FileInputStream(new File(atlasMqHome+"/broker/config/nameserver.properties")));
        NameserverProperties nameserverProperties = new NameserverProperties();
        nameserverProperties.setNameserverPwd(getStr("nameserver.password"));
        nameserverProperties.setNameserverUser(getStr("nameserver.user"));
        nameserverProperties.setNameserverPort(getInt("nameserver.port"));
        nameserverProperties.setReplicationMode(getStr("nameserver.replication.mode"));
        TraceReplicationProperties traceReplicationProperties = new TraceReplicationProperties();
        traceReplicationProperties.setNextNode(getStrCanBeNull("nameserver.replication.next.node"));
        nameserverProperties.setTraceReplicationProperties(traceReplicationProperties);
        MasterSlaveReplicationProperties masterSlaveReplicationProperties = new MasterSlaveReplicationProperties();
        masterSlaveReplicationProperties.setMaster(getStrCanBeNull("nameserver.replication.master"));
        masterSlaveReplicationProperties.setRole(getStrCanBeNull("nameserver.replication.master.slave.role"));
        masterSlaveReplicationProperties.setType(getStrCanBeNull("nameserver.replication.master.slave.type"));
        masterSlaveReplicationProperties.setPort(getInt("nameserver.replication.port"));
        nameserverProperties.setMasterSlaveReplicationProperties(masterSlaveReplicationProperties);
        nameserverProperties.print();
        CommonCache.setNameserverProperties(nameserverProperties);
    }

    private String getStrCanBeNull(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    private String getStr(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("配置参数：" + key + "不存在");
        }
        return value;
    }

    private Integer getInt(String key) {
        return Integer.valueOf(getStr(key));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
