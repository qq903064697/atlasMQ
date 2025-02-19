package cn.atlas.atlasmq.broker.utils;

import sun.nio.ch.FileChannelImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: 支持基于Java的MMap api访问文件能力（文件的读写）
 * 支持指定的offset的文件映射（结束映射的offset-开始映射的offset=映射的内存体积）
 * 文件从指定的offset开始读取
 * 文件从指定的offset开始写入
 * 文件映射后的内存释放
 */
public class MMapUtil {
    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;

    /**
     * 指定offset做文件的映射
     *
     * @param filePath    文件路径
     * @param startOffset 开始映射的offset
     * @param mappedSize  映射的内存体积
     */
    public void loadFileInMMap(String filePath, int startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + " invalid");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 支持从文件的指定offset开始读取指定大小的内容
     *
     * @param readOffset
     * @param readSize
     * @return
     */
    public byte[] readContent(int readOffset, int readSize) {
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[readSize];
        for (int i = 0; i < readSize; i++) {
            // 这里是从内存空间读取数据
            byte b = mappedByteBuffer.get(readOffset + i);
            content[i] = b;
        }
        return content;
    }

    /**
     * 写入数据到磁盘中，默认方法
     *
     * @param content
     */
    public void writeContent(byte[] content) {
        // 默认写入到page cache中
        // 如果需要强制刷盘，这里要兼容
        mappedByteBuffer.put(content);
    }

    /**
     * 写入数据到磁盘中，支持刷盘
     *
     * @param content
     * @param force
     */
    public void writeContent(byte[] content, boolean force) {
        mappedByteBuffer.put(content);
        if (force) {
            // 强制刷盘
            mappedByteBuffer.force();
        }
    }

    //不推荐使用
    public void clear() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 在关闭资源是执行以下代码释放内存
        // 不推荐的原因是因为使用了sun包下不稳定的代码
        Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
        m.setAccessible(true);
        m.invoke(FileChannelImpl.class, mappedByteBuffer);
    }

    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) {
            return;
        }
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null) {
            return buffer;
        } else {
            return viewed(viewedBuffer);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        MMapUtil mMapUtil = new MMapUtil();
        //映射1mb
        mMapUtil.loadFileInMMap("D:/Sync/Study/项目/atlasMQ/代码/atlasmq/broker/store/order_cancel_topic/00000000", 0, 1024 * 1024 * 1);
        CountDownLatch count = new CountDownLatch(1);
        CountDownLatch allWriteSucess = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        count.await();
                        //多线程并发
                        mMapUtil.writeContent(("this is content-" + finalI).getBytes());
                        allWriteSucess.countDown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            task.start();
        }

        System.out.println("准备执行并发写入mmap测试");
        count.countDown();
        allWriteSucess.await();
        System.out.println("并发写入mmap测试结束，读取文件内容测试");
        byte[] content = mMapUtil.readContent(0, 1000);
        System.out.println("内容：" + new String(content));
    }

}
