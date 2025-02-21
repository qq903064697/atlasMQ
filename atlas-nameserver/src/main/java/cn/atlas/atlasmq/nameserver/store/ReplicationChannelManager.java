package cn.atlas.atlasmq.nameserver.store;

import cn.atlas.atlasmq.common.coder.TcpMsg;
import cn.atlas.atlasmq.common.enums.NameServerEventCode;
import cn.atlas.atlasmq.nameserver.common.CommonCache;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiaoxin
 * @Description
 */
public class ReplicationChannelManager {

    private static Map<String, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();

    public Map<String, ChannelHandlerContext> getChannelHandlerContextMap() {
        return channelHandlerContextMap;
    }

    /**
     * 返还有效的slaveChannel
     * @return
     */
    public Map<String, ChannelHandlerContext> getValidSlaveChannelMap() {
        List<String> inValidChannelReqIdList = new ArrayList<>();
        for (String reqId : channelHandlerContextMap.keySet()) {
            Channel slaveChannel = channelHandlerContextMap.get(reqId).channel();
            if (!slaveChannel.isActive()) {
                inValidChannelReqIdList.add(reqId);
                continue;
            }
        }
        if (!inValidChannelReqIdList.isEmpty()) {
            for (String reqId : inValidChannelReqIdList) {
                // 移除无效的channel
                channelHandlerContextMap.remove(reqId);
            }
        }
        return channelHandlerContextMap;
    }

    public static void setChannelHandlerContextMap(Map<String, ChannelHandlerContext> channelHandlerContextMap) {
        ReplicationChannelManager.channelHandlerContextMap = channelHandlerContextMap;
    }

    public void put(String reqId, ChannelHandlerContext channelHandlerContext) {
        channelHandlerContextMap.put(reqId, channelHandlerContext);
    }

    public void get(String reqId) {
        channelHandlerContextMap.get(reqId);
    }
}
