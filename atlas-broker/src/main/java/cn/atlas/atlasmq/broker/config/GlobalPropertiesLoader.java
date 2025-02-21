package cn.atlas.atlasmq.broker.config;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.common.constants.BrokerConstants;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @Author xiaoxin
 * @Version 1.0
 */
public class GlobalPropertiesLoader {

    public void loadProperties() {
        GlobalProperties globalProperties = new GlobalProperties();
        // 通过系统环境变量获取ATLAS_MQ_HOME，现在先写死
//        String atlasMqHome = System.getenv(BrokerConstants.ATLAS_MQ_HOME);
        String atlasMqHome = "D:/Sync/Study/项目/atlasMQ/代码/atlasmq";
        if (StringUtil.isNullOrEmpty(atlasMqHome)) {
            throw new IllegalArgumentException("ATLAS_MQ_HOME is null");
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(atlasMqHome + BrokerConstants.BROKER_PROPERTIES_PATH)));
            globalProperties.setNameserverIp(properties.getProperty("nameserver.ip"));
            globalProperties.setNameserverPort(Integer.valueOf(properties.getProperty("nameserver.port")));
            globalProperties.setNameserverUser(properties.getProperty("nameserver.user"));
            globalProperties.setNameserverPassword(properties.getProperty("nameserver.password"));
            globalProperties.setBrokerPort(Integer.valueOf(properties.getProperty("broker.port")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        globalProperties.setAtlasMqHome(atlasMqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
