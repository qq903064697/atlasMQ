package cn.atlas.atlasmq.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiaoxin
 * @Version 1.0
 *
 */
public class MMapFileModelManager {
    /**
     * key: 主题名称
     * value: 文件的mmap对象
     */
    private static Map<String, MMapFileModel> mMapFileModelMap = new HashMap<>();

    public void put(String topic, MMapFileModel mMapFileModel) {
        mMapFileModelMap.put(topic, mMapFileModel);
    }

    public MMapFileModel get(String topic) {
        return mMapFileModelMap.get(topic);
    }
}
