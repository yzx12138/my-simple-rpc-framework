package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;

/**
 * @author baozi
 * @Description: Request解码
 * @Date created on 2022/7/12
 */
public class RequestDecoder extends CommandDecoder {

    @Override
    public Header decodeHeader(ByteBuf byteBuf) {
        int type = byteBuf.readInt();
        int version = byteBuf.readInt();
        long requestId = byteBuf.readLong();
        return new Header(requestId, version, type);
    }
}
