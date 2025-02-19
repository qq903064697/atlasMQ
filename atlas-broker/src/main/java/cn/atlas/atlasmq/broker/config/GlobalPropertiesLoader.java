package cn.atlas.atlasmq.broker.config;

import cn.atlas.atlasmq.broker.cache.CommonCache;
import cn.atlas.atlasmq.broker.constants.BrokerConstants;
import io.netty.util.internal.StringUtil;

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
        globalProperties.setAtlasMqHome(atlasMqHome);
        CommonCache.setGlobalProperties(globalProperties);
    }
}
