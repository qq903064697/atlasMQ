package cn.atlas.atlasmq.broker.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author xiaoxin
 * @Version 1.0
 * @Description: commitLog文件的写入offset封装
 */
public class CommitLogModel {
    /**
     * 最新commitLog文件的名称
     */
    private String fileName;

    /**
     * commitLog文件写入数据的最大地址
     */
    private Long offsetLimit;
    /**
     * 最新commitLog文件写入数据的地址
     */
    private AtomicInteger offset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AtomicInteger getOffset() {
        return offset;
    }

    public void setOffset(AtomicInteger offset) {
        this.offset = offset;
    }

    public Long getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public Long countDiff() {
        return this.offsetLimit - this.offset.get();
    }

    @Override
    public String toString() {
        return "CommitLogModel{" +
                "fileName='" + fileName + '\'' +
                ", offsetLimit=" + offsetLimit +
                ", offset=" + offset +
                '}';
    }
}
