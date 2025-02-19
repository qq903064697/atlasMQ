package cn.atlas.atlasmq.broker.config;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午3:14
 * @Version 1.0
 */
public class GlobalProperties {
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
}
