package cn.atlas.atlasmq.broker.utils;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author xiaoxin
 * @Create 2025/2/18 下午2:20
 * @Version 1.0
 */
public class TestMMapUtil {
    private MMapUtil mMapUtil;
    private static final String filePath = "D:/Sync/Study/项目/atlasMQ/代码/atlasmq/broker/store/order_cancel_topic/00000000";

    @Before
    public void setUp() throws IOException {
        mMapUtil = new MMapUtil();
        mMapUtil.loadFileInMMap(filePath, 0 , 100 * 1024 * 1024);
        System.out.println("文件映射内存成功：100m");
    }

    @Test
    public void testLoadFile() throws IOException {
   //     mMapUtil.loadFileInMMap(filePath, 0 , 100 * 1024 * 1024);
    }

    @Test
    public void testWriteAndReadFile() {
        String str = "this is a content";
        byte[] content = str.getBytes();
        mMapUtil.writeContent(content);
        byte[] readContent = mMapUtil.readContent(0, content.length);
        System.out.println(new String(readContent));

    }
}
