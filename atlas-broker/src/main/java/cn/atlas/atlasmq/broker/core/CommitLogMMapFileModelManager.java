package cn.atlas.atlasmq.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Version 1.0
 *
 */
public class CommitLogMMapFileModelManager {
    /**
     * key: 主题名称
     * value: 文件的mmap对象
     */
    private static Map<String, CommitLogMMapFileModel> mMapFileModelMap = new HashMap<>();

    public void put(String topic, CommitLogMMapFileModel commitLogMMapFileModel) {
        mMapFileModelMap.put(topic, commitLogMMapFileModel);
    }

    public CommitLogMMapFileModel get(String topic) {
        return mMapFileModelMap.get(topic);
    }
}
