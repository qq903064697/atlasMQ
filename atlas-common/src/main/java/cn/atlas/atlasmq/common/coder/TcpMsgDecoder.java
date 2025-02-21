package cn.atlas.atlasmq.common.coder;

import cn.atlas.atlasmq.common.constants.BrokerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午1:54
 * @Version 1.0
 */
public class TcpMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> in) throws Exception {
        if (byteBuf.readableBytes() > 2 + 4 + 4) {
            if (byteBuf.readShort() != BrokerConstants.DEFAULT_MAGIC_NUM) {
                ctx.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            if (byteBuf.readableBytes() < len) {
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            TcpMsg tcpMsg = new TcpMsg(code, body);
            in.add(tcpMsg);
        }
    }
}
