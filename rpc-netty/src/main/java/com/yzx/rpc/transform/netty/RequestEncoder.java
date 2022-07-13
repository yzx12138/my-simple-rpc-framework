package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;

/**
 * @author baozi
 * @Description: RequestEncoder
 * @Date created on 2022/7/12
 */
public class RequestEncoder extends CommandEncoder {

    @Override
    public void encodeHeader(Header header, ByteBuf byteBuf) {
        byteBuf.writeInt(header.getType());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeLong(header.getRequestId());
    }
}
