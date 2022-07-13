package com.yzx.rpc.transform.netty;

import com.yzx.rpc.transform.Command;
import com.yzx.rpc.transform.Header;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author baozi
 * @Description: Command转码
 * @Date created on 2022/7/12
 */
public abstract class CommandEncoder extends MessageToByteEncoder<Command> {

    //@Override
    //protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf byteBuf) throws Exception {
    //    if (!(msg instanceof Command)) {
    //        throw new Exception(String.format("Unknown type: %s!", msg.getClass().getCanonicalName()));
    //    }
    //    Command command = (Command) msg;
    //    // 写入长度
    //    byteBuf.writeInt(command.getHeader().length() + command.getPayload().length);
    //    // 写入请求头
    //    encodeHeader(command.getHeader(), byteBuf);
    //    // 写入参数
    //    byteBuf.writeBytes(command.getPayload());
    //}

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Command command, ByteBuf byteBuf)
            throws Exception {
        // 写入长度
        byteBuf.writeInt(command.getHeader().length() + command.getPayload().length);
        // 写入请求头
        encodeHeader(command.getHeader(), byteBuf);
        // 写入参数
        byteBuf.writeBytes(command.getPayload());
    }

    public abstract void encodeHeader(Header header, ByteBuf byteBuf);
}
